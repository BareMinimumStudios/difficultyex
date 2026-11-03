# Compatibility and limitations

[Documentation](README.md) · [Installation](INSTALLATION.md) · [Port status](VERIFICATION.md)

## Current target

Minecraft 1.21.1, Java 21, Fabric and NeoForge. The port targets PlayerEx 5.0.1, Data Attributes 3.0.0, Remnant 3.0.0 and Fzzy Config 0.7.7. Install corresponding loader builds on clients and servers. Quilt and legacy Forge are not acceptance targets for this branch.

## Optional integrations

GeckoLib-specific nameplate renderers and Traveler's Titles zone labels from the old release are not ported. Old configurable nameplate colors, scale and Y offset are also not currently supplied. Do not assume feature parity with the 1.20.1 download page.

## Verified and pending

| Area | Current evidence |
| --- | --- |
| Both packaged dedicated servers | Startup, operator commands and saved health/level persistence passed |
| Mob persistence | Fourteen real mob types passed repeated NBT cycles on both loaders; [scope and configuration changes](VERIFICATION.md) |
| Config migration | Synthetic and tested production fields passed; original files preserved |
| NeoForge nameplates | Singleplayer level, HP, bar and live enabled toggle passed |
| Config preferences | Serialization checks passed; authenticated two-client independence pending |
| Combat and XP | Scripted-player equipment/enchantment attacks, difficulty, XP pickup and Mending passed; authenticated human gameplay remains pending |
| Structures | Both-loader origin load/unload lifecycle, no implicit origin loading, geometry and generated variants passed; connected-player streaming and broader performance remain pending |
| Fabric development launch | Resolved with Loader 0.20.1; normal named server startup and expanded runtime checks passed |

The clean suite passes 69 tests. See the [verification summary](VERIFICATION.md) for completed checks and the [interactive run sheet](FINAL_ACCEPTANCE.md) for remaining acceptance.

## Report a problem

Include Minecraft version, loader/version, DifficultyEx version, dependency versions, reproduction steps and the relevant log in a [GitHub issue](https://github.com/BareMinimumStudios/difficultyex/issues). Remove account tokens, addresses and other private information before attaching logs.

## PlayerEx XP reduction

DifficultyEx scales the mob reward before normal downstream handling. PlayerEx can discard orbs in an exhausted chunk; Mending can use surviving XP for repairs. A lower collected amount does not by itself indicate a scaling error. Both interactions passed [scripted runtime checks](VERIFICATION.md).

## Visibility inputs

Real server scoreboard/invisibility inputs, wall line of sight and exact distance boundaries passed with the shared visibility policy. Client visuals, global render-name behavior and multiplayer synchronization are not established by those checks. See [runtime evidence](VERIFICATION.md).

## Changes from 1.20.1

Nearby-player eligibility now uses an inclusive 3D radius and excludes spectators. Structure rules use already loaded references and origins rather than searches that can load terrain. Supported old CCA levels import into the modern synchronized tag.

The former Fabric EntityLevelingEvents callback API and old package namespace are not preserved. External consumers need an explicit compatibility update. The old personal difficulty selector was unused scaffolding, not an implemented gameplay mode. Old nameplate styling, GeckoLib hooks and Traveler's Titles remain optional scope decisions.
