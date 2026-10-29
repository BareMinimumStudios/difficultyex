# GitHub build and downloaded artifact acceptance

Verified October 29, 2026 for commit 3d968eabcbffc5ca8927430546a6b0c99cc1d4c3 on branch 1.21.1.

## GitHub result

[Build run 40954124899](https://github.com/BareMinimumStudios/difficultyex/actions/runs/40954124899) completed successfully. Its Ubuntu Java 21 build took 7 minutes 58 seconds. Build, all 20 release-tool tests, release preparation, bundle verification and artifact upload passed.

The older run for 8dc892b was canceled when the newer push superseded it. Its log showed dependency remapping before cancellation. It was not an established compiler or test failure. No production fix was needed for the successful current run.

## Downloaded bundle

Artifact 12933217863, difficultyex-1.21.1-jars, was downloaded through the connected GitHub account. Its ZIP SHA256 matched GitHub's recorded digest:

`d4712ad58d69feacbef7fea30be70caaa72de35442beb5c66c22a91672b7d276`

Only the outer CI ZIP was extracted; JARs remained intact. The release verifier passed against the extracted bundle without Gradle properties, confirming both JAR sizes/hashes, release-note hash, version and expected paths.

| CI-built artifact | SHA256 |
| --- | --- |
| Fabric 1.1.0, Minecraft 1.21.1 | `511eb59a6d58ff97f920e1c806349dfed05d462096a4327fdc04d317291d205b` |
| NeoForge 1.1.0, Minecraft 1.21.1 | `e5656c965147dc0cb7561a3a08e03fbde5a21f188ce143521978871e027fe2e5` |

Both JARs contain the four expected mixin classes and both license notices, with no temporary QA classes.

## Actual CI JAR runtime

The downloaded Linux-built artifacts, not local replacements, passed isolated headless server checks on Java 21.0.10 with Fabric Loader 0.20.1 and NeoForge 21.1.256 and the pinned public dependencies. Both reached Done, validated active config, changed a protected zombie from level 10 at 18/36 HP to level 20 at 26/52 HP, saved and shut down gracefully. A full process restart restored level 20 and 26/52 HP on both loaders.

Disposable launch libraries, mods, worlds, configs and extracted artifact copies were removed. Reference servers were not modified. Test ports were loopback-only and RCON was disabled. No authenticated player joined and no client window opened.

## CI feedback improvements

Release-helper tests now run before the Gradle build. Both build and publish workflows retain generated XML/HTML JVM test reports for 14 days with an always-run upload step, including failed runs. Missing reports before tests execute produce a warning. Workflow syntax, local release-tool tests and real-artifact integrity checks passed; these new workflow edits require the next user push to run on GitHub.

No game code or mod version changed, and no publication was attempted. Multiplayer, visuals, human gameplay and release authorization remain pending.
