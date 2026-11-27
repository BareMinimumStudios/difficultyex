# Publishing

[Documentation](README.md) · [Changelog](../CHANGELOG.md)

1. Set the mod version in gradle.properties. Add the matching dated entry below Unreleased in CHANGELOG.md and group changes by category.
2. Build both loaders and run the preflight below with Python 3.11 or newer. It generates build/release/notes.md from the current version entry.
3. Set MODRINTH_TOKEN and CURSEFORGE_TOKEN in repository secrets.
4. Dispatch the publish workflow on branch 1.21.1. The default channel is release; alpha and beta remain available.
5. Check both loader files on Modrinth and CurseForge, and both JAR attachments on the GitHub Release.

```powershell
python .github/scripts/test_prepare_release.py
python .github/scripts/prepare_release.py --release-type release
python .github/scripts/prepare_release.py --verify
```

The manifest in build/release/manifest.json records JAR sizes/hashes and the release-notes hash. To verify an extracted CI artifact, add `--root "C:/path/to/artifact"` to the verification command.

Modrinth and CurseForge version identifiers are `<mod_version>+1.21.1-fabric` and `<mod_version>+1.21.1-neoforge`. GitHub uses one release and tag named `<mod_version>+1.21.1` for both JARs, with `build/release/notes.md` as the release body. Alpha and beta dispatches create GitHub prereleases. Uploads are sequential; inspect completed uploads before retrying a partial failure. Normal pushes do not publish.
