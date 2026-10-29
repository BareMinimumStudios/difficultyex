from pathlib import Path
import hashlib
import json
import subprocess
import sys
import tempfile
import unittest

from prepare_release import prepare


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

    def test_invalid_channel(self):
        with self.assertRaises(ValueError):
            prepare(self.root, "stable")


if __name__ == "__main__":
    unittest.main()
