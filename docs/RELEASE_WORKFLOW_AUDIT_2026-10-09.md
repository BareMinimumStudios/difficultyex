# Release workflow audit, October 9, 2026

The manual workflow remains restricted to branch `1.21.1`. No publication was executed and no account tokens were read.

## Dependency identities

Every dependency now has an explicit CurseForge alias alongside its Modrinth identity. This avoids slug lookup selecting an older project or missing a differently named continuation.

| Dependency | Modrinth | CurseForge | Verified project |
| --- | --- | --- | --- |
| Fabric API | P7dR8mSH | 306612 | [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api) |
| Fabric Language Kotlin | Ha28R6CL | 308769 | [Fabric Language Kotlin](https://www.curseforge.com/minecraft/mc-mods/fabric-language-kotlin) |
| Fzzy Config | VSNURh3q | 1005914 | [Fzzy Config](https://www.curseforge.com/minecraft/mc-mods/fzzy-config) |
| PlayerEx | 4UKlJSdk | 958325 | [PlayerEx: DC](https://www.curseforge.com/minecraft/mc-mods/playerex-dc) |
| Data Attributes | KCGxOJsE | 955929 | [Data Attributes: DC](https://www.curseforge.com/minecraft/mc-mods/dataattributes) |
| Remnant | oLPaySSb | 1019391 | [Remnant: OPC](https://www.curseforge.com/minecraft/mc-mods/remnant-opc) |
| Kotlin for Forge | kotlin-for-forge | 351264 | [Kotlin for Forge](https://www.curseforge.com/minecraft/mc-mods/kotlin-for-forge) |

The old `playerex` CurseForge slug belongs to project 409221, whose latest listed release targets Minecraft 1.19.2. DifficultyEx needs the current DC project instead. Alias syntax was checked against the [pinned mc-publish action documentation](https://github.com/Kira-NT/mc-publish/blob/52307b03863581dec6b652b83e597aec02ebb075/README.md#dependencies).

The pinned public PlayerEx Fabric artifact contains nested Remnant and Crunch JARs, confirmed through MixinMCP. DifficultyEx still declares Remnant as required, matching its own loader metadata and existing acceptance installation.

## Pre-upload checks

- Missing Modrinth or CurseForge tokens fail before building or publishing. Token values are never printed.
- Each loader must have exactly one nonempty release JAR matching the upload glob.
- Workflow concurrency prevents two runs uploading simultaneously and does not cancel a running upload.
- The existing branch gate, Java 21 setup, clean build and pinned action revisions remain in place.

## Local validation

Official actionlint 1.7.12 was downloaded outside the repository and checked against the release SHA256 manifest. Workflow validation passed. ShellCheck and Pyflakes were disabled because they are not installed; all four shell blocks passed Git Bash syntax checking.

Isolated execution passed four synthetic credential cases, five artifact cases (none, missing loader, empty file, valid pair, duplicate), CRLF version extraction and selection of the actual two built release JARs. Temporary fixtures were removed. These checks validate local logic, not account permissions or platform upload behavior.

No application sources changed in this audit; the last clean both-loader build remains 63 tests with zero failures. Authenticated credentials, final release type/changelog review and platform upload acceptance remain pending. Gameplay acceptance and the Fabric named development-launch discrepancy remain separate tasks.
