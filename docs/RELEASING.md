# Release preparation

[Documentation](README.md) · [Release notes](../RELEASE_NOTES.md) · [Remaining acceptance](../TODO.md)

## Prepared locally

- Dedicated port release notes replace the historical full changelog as upload notes.
- The manual workflow offers alpha, beta and release channels, defaulting to alpha.
- Version and note-header checks reject missing, duplicate or malformed versions and mismatched notes.
- Artifact checks require exactly one nonempty, correctly versioned JAR for each loader.
- A manifest records version, channel, Minecraft, Java, file sizes, JAR SHA256 hashes and the release-notes hash.
- CI verifies the manifest after preparation, and publication verifies it again immediately before each loader upload.
- Release-helper tests run before the Gradle build. Normal CI archives the manifest and notes beside both JARs.
- Build and publish workflows retain generated JVM XML/HTML test reports for 14 days, including failed runs. A failure before tests produce reports leaves only a missing-report warning.

The configured mod version remains 1.1.0. No version decision, credential permission, upload or release approval is implied by these preparation checks.

## Local preflight

After building both loaders, run these commands from the repository root with Python 3.11 or newer:

```powershell
python .github/scripts/test_prepare_release.py
python .github/scripts/prepare_release.py --release-type alpha
python .github/scripts/prepare_release.py --verify
```

The preflight writes `build/release/manifest.json`. It never contacts publishing platforms or reads tokens. Update the first line of RELEASE_NOTES.md when changing mod_version; both must name the same version.

## Verify a downloaded CI bundle

Extract the CI artifact while preserving its directory structure. From a trusted checkout, run:

```powershell
python .github/scripts/prepare_release.py --verify --root "C:/path/to/extracted-artifact"
```

Verification needs only RELEASE_NOTES.md, build/release/manifest.json and the two JARs under build/libs. It does not need Gradle, gradle.properties, credentials or a game launch. It checks both artifact hashes and sizes, the notes hash/version, loader identities and expected paths. It never rewrites the manifest. A changed or incomplete bundle fails verification; prepare a new manifest only when intentionally preparing new release files.

These hashes detect changes relative to the manifest. They are not a signed publisher identity or a replacement for obtaining the bundle from the intended CI run.

The downloaded GitHub artifact and both actual CI JARs passed integrity, startup, commands and full-restart checks on October 29, 2026. See [artifact acceptance](CI_ARTIFACT_QA_2026-10-29.md). This does not replace the remaining release review.

## Before publication

1. Review the [interactive acceptance run sheet](FINAL_ACCEPTANCE.md) for remaining multiplayer, client visual, human-gameplay and performance acceptance. Record which limitations the selected channel permits.
2. Approve mod_version, the focused release notes and the chosen channel. Do not present this development port as fully accepted.
3. Confirm repository secrets MODRINTH_TOKEN and CURSEFORGE_TOKEN exist and can upload to the intended projects. These permissions have not been tested locally.
4. Review CI results, both JARs and the manifest from branch 1.21.1.
5. Explicitly dispatch the publish workflow on 1.21.1 with the approved channel.
6. Check both loaders on both platforms before announcing availability. Uploads are sequential; inspect what succeeded before retrying a partial failure.

Version identifiers are `<mod_version>+1.21.1-fabric` and `<mod_version>+1.21.1-neoforge`. Normal builds and pushes do not publish. Actual upload acceptance remains pending.

## Public version check

On October 29, 2026, the Modrinth project API listed neither proposed identifier, 1.1.0+1.21.1-fabric nor 1.1.0+1.21.1-neoforge, and no published version targeting Minecraft 1.21.1. The GitHub repository release list was empty. This check does not reserve identifiers or verify CurseForge state, credentials or upload permissions; recheck before publication.
