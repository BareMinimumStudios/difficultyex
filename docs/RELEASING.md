# Publishing

[Documentation](README.md) · [Release notes](../RELEASE_NOTES.md)

1. Set mod_version in gradle.properties and match the version heading in RELEASE_NOTES.md.
2. Build both loaders and run the preflight below with Python 3.11 or newer.
3. Set MODRINTH_TOKEN and CURSEFORGE_TOKEN in repository secrets.
4. Dispatch the publish workflow on branch 1.21.1. The default channel is release; alpha and beta remain available.
5. Check both loader files on both platforms after upload.

```powershell
python .github/scripts/test_prepare_release.py
python .github/scripts/prepare_release.py --release-type release
python .github/scripts/prepare_release.py --verify
```

The manifest in build/release/manifest.json records JAR sizes/hashes and the release-notes hash. To verify an extracted CI artifact, add `--root "C:/path/to/artifact"` to the verification command.

Version identifiers are `<mod_version>+1.21.1-fabric` and `<mod_version>+1.21.1-neoforge`. Uploads are sequential; inspect completed uploads before retrying a partial failure. Normal pushes do not publish.
