# Structure lifecycle and visibility-state checks

Validated October 26, 2026 through background-only disposable dedicated servers. Fabric Loader 0.20.1 and NeoForge 21.1.26 used the pinned dependencies. No client window, desktop automation or authenticated player was used.

## Real origin-chunk lifecycle on both loaders

A generated plains village in the fixed-seed test world had origin chunk `(601, 670)` and reference chunk `(595, 664)`. The sampled point was X=9526, Z=10635 at Y=150. The horizontal bounding box was X=9526..9703, Z=10635..10815. The reference was verified to contain the actual village origin link.

The probe kept only the distant reference chunk forced. The origin was allowed to unload through the normal server ticket lifecycle; no forced unload or structure mock was used. The same sequence passed on Fabric and NeoForge:

1. Confirm origin absent and reference present through non-loading lookups.
2. Initialize a mob at the inside point: level 1. Confirm the lookup did not load the missing origin.
3. Explicitly load the origin. A newly initialized mob receives configured minimum 30; the earlier mob stays level 1.
4. Add a conflicting structure maximum 20. Another new mob receives level 20.
5. Move the level-30 mob beyond the box and reapply entity-load handling; it keeps level 30. Reposition and reinitialize the earlier level-1 mob inside; it keeps level 1.
6. Allow the origin to unload again. A newly initialized inside mob falls back to level 1, while earlier levels remain 1, 30 and 20. Confirm the origin remains absent after lookup.

Each observed unload took two ticks after the test stopped holding the origin. A startup fixture initially assumed immediate Fabric entity-load delivery from `addFreshEntity`; the corrected probe explicitly called the shared public initialization method after adding its mob. This check verifies structure lookup and assignment/preservation, not immediate loader event timing.

These checks exercise real generated references, origin loading, natural unloading and mob position changes. They do not emulate a connected player's chunk-streaming movement, prove every structure type, or establish client visibility of moved mobs.

## Real server visibility inputs on NeoForge

The probe used actual scoreboard teams, a NeoForge fake player, a real zombie and the same shared visibility policy called by the client renderer.

- All 12 combinations of the four name-tag visibility modes and viewer membership (none, same, other team) produced the expected policy result.
- An invisible teammate was hidden when friendly-invisible viewing was disabled and visible when it was enabled. An opposing viewer still saw it as invisible. The shared policy used these actual `isInvisibleTo` results.
- A real `hasLineOfSight` query was clear before a stone wall, blocked with the wall present, and clear again after removal.
- Actual entity distance was exactly 400 squared at 20 blocks and greater than 400 just beyond it, matching the renderer's distance comparison.

The initial Kotlin fixture accidentally selected the enum's lowercase serialized `name` field rather than its uppercase enum name; it was corrected. The production Java renderer already calls `name()` and was unchanged. A temporary Kotlin setter spelling error was also corrected before runtime acceptance.

These are server-state and shared-policy checks. They do not execute the client renderer, global render-names setting, spectator camera, label layout, rendering occlusion or client-side state synchronization. Those visual and authenticated multiplayer checks remain pending.

## Final verification

Both final server runs completed and saved all dimensions. Both production entrypoints were restored byte-for-byte. Temporary probes stayed outside the repository; both disposable worlds and local run configurations were removed. No game/server process or test listener remained. Original development config SHA256 stayed unchanged and RCON remained disabled.

The final clean both-loader build passed 63 tests with zero failures, errors or skips. The final JARs contain both license notices and no QA classes. No gameplay implementation change was needed for this pass.

[Port status](PORT_STATUS.md) · [QA checklist](QA_CHECKLIST.md) · [Configuration](CONFIGURATION.md)
