# Fabric development launch and runtime checks

Validated October 25, 2026 using background tools only. No visible game window or desktop automation was used.

## Fix

Updated the pinned Fabric Loader from 0.19.3 to 0.20.1. This official release supplies Fabric's Mixin 0.17.5 implementation, which removed the permissive root-method descriptor fallback responsible for selecting PlayerEx's optional LivingEntity alias against a ServerPlayer overload in the named development environment.

PlayerEx 5.0.1, Data Attributes 3.0.0, Remnant 3.0.0 and Fzzy Config 0.7.7 are unchanged. No third-party JAR patch, injector suppression, namespace override or disabled refmap is required. Normal development mapping remapping remains enabled.

The generated Fabric metadata now requires Loader `>=0.20`. Official Loader 0.20.0 also supplies Mixin 0.17.5; actual runtime acceptance in this pass used 0.20.1. Earlier packaged acceptance on Loader 0.19.5 describes the previous build, not the new minimum.

Source/metadata references: [official 0.20.1 profile](https://meta.fabricmc.net/v2/versions/loader/1.21.1/0.20.1/profile/json), [0.20.0 profile](https://meta.fabricmc.net/v2/versions/loader/1.21.1/0.20.0/profile/json), [official source artifact](https://maven.fabricmc.net/net/fabricmc/sponge-mixin/0.17.5+mixin.0.8.7/sponge-mixin-0.17.5+mixin.0.8.7-sources.jar).

## Runtime acceptance

An isolated named Fabric development server on 127.0.0.1:25586 reached Done with online mode enabled and RCON disabled. The temporary probe ran through actual server APIs and was removed afterward.

| Check | Result |
| --- | --- |
| Command registration and clean config | Validator passed |
| Invalid config | Four findings reported without changing the settings object |
| Permissions | Permission-zero validator call denied |
| Passive mob inspection | Cow at 0.5 HP reported fractional health and attack n/a |
| Level changes | Current-health percentage preserved; zero-health cow stayed at zero |
| Combat attributes and bounds | Level 20 reported attack 9.0 and armor attribute 5.2; 1,000,001 rejected under the default maximum |
| Repeated level application | Ten applications retained max health 52 and attack 9.0 |
| Actual melee pipeline | Level-20 zombie dealt 9.0 damage to a cow target |
| Actual incoming armor pipeline | A 5-point mob attack dealt 4.5 damage to the scaled zombie |
| Saved-level conversion | Supported old compound imported level 25 and 30/60 HP; modern NBT round trip retained both |
| Active world | 300 AI-enabled, invulnerable, no-gravity zombies survived 120 real server ticks; every entity ticked at least 100 times with finite health |

The active-world sample used three structure rules at radius 128. Observed whole-tick work: mean 6.54 ms, p95 12.87 ms, maximum 180.60 ms. This is one short diagnostic on an empty-player local server, not a comparative benchmark or a populated multiplayer performance guarantee. It does not establish dense structure-reference coverage or normal ground navigation/combat load.

Initial probe attempts failed because newly added entities were not available to immediate UUID command lookup, and because the fractional-health assertion incorrectly expected the absolute value to remain below one while scaling upward. The corrected command fixture used an entity-bound @s source and compared the actual health ratio. Those failures are not recorded as passing checks.

## Clean verification

Restored the Fabric entrypoint byte-for-byte and removed the external probe source from the run configuration. The IDEA clean/test/build configuration passed: 63 tests across 14 suites, zero failures, errors or skips. Both JARs contain both license notices and no QA classes. IDEA compilation returned no problems.

A subsequent normal named Fabric development launch, with no probe or entrypoint hook, reached Done, executed `/difficultyex validate` successfully through normal console input, and stopped gracefully while saving all dimensions.

Class-path warnings about absent optional generated resource directories, Fabric's untranslated item-tag warning and existing Gradle 10 deprecations remain. They did not prevent startup. Common-stub IDE injection inspections remain a separate tooling limitation. Client launch/rendering was not tested in this background pass.

## Cleanup and remaining work

Temporary source and the disposable world are outside the repository; the world and temporary run configuration were removed after graceful shutdown. No game/server process or test listener was left running. RCON stayed disabled and the original development config hash remained unchanged.

Still pending: authenticated two-client networking/preferences/reconnect, Fabric client visuals and visibility edge cases, human player equipment/enchantments and XP pickup, real movement near late-loading structure origins, longer representative performance testing, optional parity decisions and release acceptance.
