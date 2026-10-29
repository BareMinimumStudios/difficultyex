import argparse
import hashlib
import json
from pathlib import Path
import re

MINECRAFT = "1.21.1"
LOADERS = ("fabric", "neoforge")


def prepare(root: Path, release_type: str) -> dict:
    if release_type not in ("alpha", "beta", "release"):
        raise ValueError("Release type must be alpha, beta or release")
    versions = re.findall(r"^mod_version=([^\r\n]*)$", (root / "gradle.properties").read_text(), re.MULTILINE)
    if len(versions) != 1 or not re.fullmatch(r"\d+\.\d+\.\d+(?:-[0-9A-Za-z.-]+)?", versions[0]):
        raise ValueError("Expected exactly one valid mod_version in gradle.properties")
    version = versions[0]
    notes = root / "RELEASE_NOTES.md"
    if not notes.is_file() or not notes.read_text(encoding="utf-8").startswith(
        f"# DifficultyEx {version} for Minecraft {MINECRAFT}\n"
    ):
        raise ValueError("RELEASE_NOTES.md must start with the current mod version and Minecraft version")
    artifacts = []
    for loader in LOADERS:
        matches = list((root / "build/libs").glob(f"*-{loader}-{MINECRAFT}.jar"))
        expected = f"DifficultyEx-{version}-{loader}-{MINECRAFT}.jar"
        if len(matches) != 1 or matches[0].name != expected:
            raise ValueError(f"Expected exactly one release artifact named {expected}")
        artifact = matches[0]
        if not artifact.is_file() or artifact.stat().st_size == 0:
            raise ValueError(f"Release artifact is empty or not a file: {expected}")
        with artifact.open("rb") as stream:
            digest = hashlib.file_digest(stream, "sha256").hexdigest()
        artifacts.append({
            "loader": loader,
            "path": artifact.relative_to(root).as_posix(),
            "bytes": artifact.stat().st_size,
            "sha256": digest,
        })
    return {
        "version": f"{version}+{MINECRAFT}",
        "releaseType": release_type,
        "minecraft": MINECRAFT,
        "java": 21,
        "notes": notes.relative_to(root).as_posix(),
        "notesSha256": hashlib.sha256(notes.read_bytes()).hexdigest(),
        "artifacts": artifacts,
    }


def verify(root: Path) -> dict:
    manifest = json.loads((root / "build/release/manifest.json").read_text(encoding="utf-8"))
    if not isinstance(manifest, dict):
        raise ValueError("Release manifest must be an object")
    version = manifest.get("version")
    match = re.fullmatch(r"(\d+\.\d+\.\d+(?:-[0-9A-Za-z.-]+)?)\+1\.21\.1", version) if isinstance(version, str) else None
    if not match or manifest.get("minecraft") != MINECRAFT or manifest.get("java") != 21:
        raise ValueError("Invalid manifest version or runtime target")
    if manifest.get("releaseType") not in ("alpha", "beta", "release"):
        raise ValueError("Invalid manifest release type")
    if manifest.get("notes") != "RELEASE_NOTES.md":
        raise ValueError("Invalid manifest notes path")
    notes = root / "RELEASE_NOTES.md"
    if hashlib.sha256(notes.read_bytes()).hexdigest() != manifest.get("notesSha256"):
        raise ValueError("Release notes hash does not match the manifest")
    if not notes.read_text(encoding="utf-8").startswith(f"# DifficultyEx {match[1]} for Minecraft {MINECRAFT}\n"):
        raise ValueError("Release notes version does not match the manifest")
    artifacts = manifest.get("artifacts")
    if not isinstance(artifacts, list) or len(artifacts) != len(LOADERS):
        raise ValueError("Manifest must contain exactly two loader artifacts")
    remaining = set(LOADERS)
    for artifact in artifacts:
        if not isinstance(artifact, dict) or not isinstance(artifact.get("loader"), str) or artifact.get("loader") not in remaining:
            raise ValueError("Unknown or duplicate loader in manifest")
        loader = artifact["loader"]
        remaining.remove(loader)
        expected = f"build/libs/DifficultyEx-{match[1]}-{loader}-{MINECRAFT}.jar"
        if artifact.get("path") != expected:
            raise ValueError(f"Invalid manifest artifact path for {loader}")
        if type(artifact.get("bytes")) is not int or artifact["bytes"] <= 0:
            raise ValueError(f"Invalid manifest artifact size for {loader}")
        matches = list((root / "build/libs").glob(f"*-{loader}-{MINECRAFT}.jar"))
        if len(matches) != 1 or matches[0].name != Path(expected).name:
            raise ValueError(f"Unexpected release artifact selection for {loader}")
        path = root / expected
        with path.open("rb") as stream:
            digest = hashlib.file_digest(stream, "sha256").hexdigest()
        if path.stat().st_size != artifact.get("bytes") or digest != artifact.get("sha256"):
            raise ValueError(f"Release artifact size/hash does not match for {loader}")
    return manifest

def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--root", type=Path, default=Path(__file__).resolve().parents[2])
    mode = parser.add_mutually_exclusive_group()
    mode.add_argument("--release-type", choices=("alpha", "beta", "release"), default="alpha")
    mode.add_argument("--verify", action="store_true")
    parser.add_argument("--github-output", type=Path)
    args = parser.parse_args()
    if args.verify and args.github_output:
        parser.error("--verify does not write GitHub output")
    try:
        if args.verify:
            manifest = verify(args.root)
            print(f"Release bundle verified: {manifest['version']}")
            return
        manifest = prepare(args.root, args.release_type)
    except (ValueError, OSError) as error:
        parser.exit(1, f"Release preflight failed: {error}\n")
    destination = args.root / "build/release/manifest.json"
    destination.parent.mkdir(parents=True, exist_ok=True)
    destination.write_text(json.dumps(manifest, indent=2) + "\n", encoding="utf-8")
    if args.github_output:
        with args.github_output.open("a", encoding="utf-8") as output:
            output.write(f"number={manifest['version']}\n")
    print(f"Release preflight passed: {manifest['version']} ({manifest['releaseType']}); {destination}")


if __name__ == "__main__":
    main()
