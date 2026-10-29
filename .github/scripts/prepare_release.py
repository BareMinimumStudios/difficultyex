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
        "artifacts": artifacts,
    }


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--root", type=Path, default=Path(__file__).resolve().parents[2])
    parser.add_argument("--release-type", choices=("alpha", "beta", "release"), default="alpha")
    parser.add_argument("--github-output", type=Path)
    args = parser.parse_args()
    try:
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
