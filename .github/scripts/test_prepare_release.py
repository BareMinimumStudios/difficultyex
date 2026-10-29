from pathlib import Path
import hashlib
import json
import subprocess
import sys
import tempfile
import unittest

from prepare_release import prepare, verify


class ReleasePreflightTest(unittest.TestCase):
    def setUp(self):
        self.directory = tempfile.TemporaryDirectory()
        self.addCleanup(self.directory.cleanup)
        self.root = Path(self.directory.name)
        (self.root / "gradle.properties").write_bytes(b"mod_version=1.1.0\r\n")
        (self.root / "RELEASE_NOTES.md").write_text("# DifficultyEx 1.1.0 for Minecraft 1.21.1\n\nPort notes.\n")
        self.libs = self.root / "build/libs"
        self.libs.mkdir(parents=True)
        for loader in ("fabric", "neoforge"):
            (self.libs / f"DifficultyEx-1.1.0-{loader}-1.21.1.jar").write_bytes(loader.encode())

    def test_manifest_has_exact_versions_sizes_and_hashes(self):
        for channel in ("alpha", "beta", "release"):
            result = prepare(self.root, channel)
            self.assertEqual("1.1.0+1.21.1", result["version"])
            self.assertEqual(channel, result["releaseType"])
            self.assertEqual(2, len(result["artifacts"]))
            for artifact in result["artifacts"]:
                data = artifact["loader"].encode()
                self.assertEqual(len(data), artifact["bytes"])
                self.assertEqual(hashlib.sha256(data).hexdigest(), artifact["sha256"])

    def test_missing_artifact(self):
        (self.libs / "DifficultyEx-1.1.0-fabric-1.21.1.jar").unlink()
        with self.assertRaises(ValueError):
            prepare(self.root, "alpha")

    def test_empty_artifact(self):
        (self.libs / "DifficultyEx-1.1.0-neoforge-1.21.1.jar").write_bytes(b"")
        with self.assertRaises(ValueError):
            prepare(self.root, "alpha")

    def test_duplicate_artifact(self):
        (self.libs / "stale-fabric-1.21.1.jar").write_bytes(b"stale")
        with self.assertRaises(ValueError):
            prepare(self.root, "alpha")

    def test_wrong_artifact_version(self):
        artifact = self.libs / "DifficultyEx-1.1.0-fabric-1.21.1.jar"
        artifact.unlink()
        (self.libs / "DifficultyEx-0.9.0-fabric-1.21.1.jar").write_bytes(b"wrong version")
        with self.assertRaises(ValueError):
            prepare(self.root, "alpha")

    def test_artifact_directory_is_not_a_jar(self):
        artifact = self.libs / "DifficultyEx-1.1.0-fabric-1.21.1.jar"
        artifact.unlink()
        artifact.mkdir()
        with self.assertRaises(ValueError):
            prepare(self.root, "alpha")

    def test_missing_or_mismatched_notes(self):
        notes = self.root / "RELEASE_NOTES.md"
        notes.unlink()
        with self.assertRaises(ValueError):
            prepare(self.root, "alpha")
        notes.write_text("# DifficultyEx 0.9.0 for Minecraft 1.20.1\n")
        with self.assertRaises(ValueError):
            prepare(self.root, "alpha")

    def test_invalid_or_duplicate_versions(self):
        for text in ("", "mod_version=\n", "mod_version=1.1.0\nmod_version=2.0.0\n", "mod_version=1.1.0 bad\n"):
            with self.subTest(text=text):
                (self.root / "gradle.properties").write_text(text)
                with self.assertRaises(ValueError):
                    prepare(self.root, "alpha")

    def test_command_writes_manifest_and_github_output(self):
        output = self.root / "github-output"
        result = subprocess.run(
            [sys.executable, str(Path(__file__).with_name("prepare_release.py")),
             "--root", str(self.root), "--release-type", "beta", "--github-output", str(output)],
            capture_output=True, text=True,
        )
        self.assertEqual(0, result.returncode, result.stderr)
        self.assertEqual("number=1.1.0+1.21.1\n", output.read_text())
        manifest = json.loads((self.root / "build/release/manifest.json").read_text())
        self.assertEqual("beta", manifest["releaseType"])

    def write_manifest(self):
        manifest = prepare(self.root, "beta")
        destination = self.root / "build/release/manifest.json"
        destination.parent.mkdir(parents=True)
        destination.write_text(json.dumps(manifest))
        return destination, manifest

    def test_verify_downloaded_bundle_without_gradle_properties(self):
        _, expected = self.write_manifest()
        (self.root / "gradle.properties").unlink()
        self.assertEqual(expected, verify(self.root))

    def test_verify_rejects_same_size_jar_changes(self):
        self.write_manifest()
        (self.libs / "DifficultyEx-1.1.0-fabric-1.21.1.jar").write_bytes(b"FABRIC")
        with self.assertRaises(ValueError):
            verify(self.root)

    def test_verify_rejects_note_changes(self):
        self.write_manifest()
        with (self.root / "RELEASE_NOTES.md").open("a") as notes:
            notes.write("Changed after preparation.\n")
        with self.assertRaises(ValueError):
            verify(self.root)

    def test_verify_rejects_missing_jar(self):
        self.write_manifest()
        (self.libs / "DifficultyEx-1.1.0-neoforge-1.21.1.jar").unlink()
        with self.assertRaises((OSError, ValueError)):
            verify(self.root)

    def test_verify_rejects_invalid_manifest(self):
        destination, _ = self.write_manifest()
        for data in ("{", "[]", "{}"):
            with self.subTest(data=data):
                destination.write_text(data)
                with self.assertRaises(ValueError):
                    verify(self.root)

    def test_verify_rejects_unsafe_paths(self):
        destination, manifest = self.write_manifest()
        manifest["artifacts"][0]["path"] = "../outside.jar"
        destination.write_text(json.dumps(manifest))
        with self.assertRaises(ValueError):
            verify(self.root)

    def test_verify_rejects_duplicate_loader(self):
        destination, manifest = self.write_manifest()
        manifest["artifacts"][1] = manifest["artifacts"][0]
        destination.write_text(json.dumps(manifest))
        with self.assertRaises(ValueError):
            verify(self.root)

    def test_verify_cli_detects_tampering_without_rewriting_manifest(self):
        destination, _ = self.write_manifest()
        before = destination.read_bytes()
        arguments = [sys.executable, str(Path(__file__).with_name("prepare_release.py")),
                     "--root", str(self.root), "--verify"]
        self.assertEqual(0, subprocess.run(arguments, capture_output=True).returncode)
        (self.libs / "DifficultyEx-1.1.0-fabric-1.21.1.jar").write_bytes(b"changed")
        self.assertNotEqual(0, subprocess.run(arguments, capture_output=True).returncode)
        self.assertEqual(before, destination.read_bytes())

    def test_verify_rejects_invalid_loader_and_sizes(self):
        destination, manifest = self.write_manifest()
        for field, invalid in (("loader", []), ("bytes", 0), ("bytes", True), ("bytes", "6")):
            with self.subTest(field=field, invalid=invalid):
                changed = json.loads(json.dumps(manifest))
                changed["artifacts"][0][field] = invalid
                destination.write_text(json.dumps(changed))
                with self.assertRaises(ValueError):
                    verify(self.root)

    def test_verify_rejects_artifact_added_after_preparation(self):
        self.write_manifest()
        (self.libs / "stale-fabric-1.21.1.jar").write_bytes(b"unexpected upload candidate")
        with self.assertRaises(ValueError):
            verify(self.root)

    def test_invalid_channel(self):
        with self.assertRaises(ValueError):
            prepare(self.root, "stable")


if __name__ == "__main__":
    unittest.main()
