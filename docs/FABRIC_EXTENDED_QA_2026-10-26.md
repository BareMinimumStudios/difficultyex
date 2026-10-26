# Extended Fabric background checks

Validated October 26, 2026 after local commit `1e6159d`. No desktop automation, visible game window or authenticated client was used. Loader 0.20.1 and the existing public dependencies were unchanged.

## Additional coverage

- Encoded and decoded the real `ClientboundSetEntityDataPacket.STREAM_CODEC` using registry-aware buffers. An initial non-default metadata packet and subsequent dirty updates transferred levels 12, 27 and 1,000,000 to a separate zombie's tracked data. Packet entity IDs matched and the buffers were completely consumed. This verifies serialization and assignment on Fabric, not remote client delivery or rendering.
- Ten successive modern NBT save/load cycles retained level 25, maximum health 60 and current health 30. This is repeated in-memory save/load, not ten full server restarts.
- After normal ticking, operator inspection selected a loaded mob by UUID and through `@e[type=minecraft:zombie,sort=nearest,limit=1]`. Both returned a scaled level and stats. This supplements the prior entity-bound command fixture.
- 500 AI-enabled, invulnerable, no-gravity zombies remained active over 600 real ticks. Every entity ticked at least 580 times and retained finite health. Three structure rules were configured with radius 128.

Observed whole-tick work in this one local sample: mean 8.35 ms, p95 14.64 ms, maximum 285.20 ms. No players joined. The no-gravity setup and clustered placement do not represent ordinary ground navigation, player combat, dense structure references or sustained multiplayer load. These timings are diagnostic, not a benchmark or a tick-time guarantee.

The earlier Fabric command validation, permissions, fractional/dead-health, repeated-modifier, actual melee/armor and legacy-level checks also passed again in this run. The server stopped gracefully and saved all dimensions.

## Final verification and cleanup

The Fabric entrypoint was restored byte-for-byte. Temporary QA source remained outside the repository and its temporary run configuration was removed. The disposable server directory was deleted after shutdown. The final clean both-loader build passed all 63 tests, with zero failures, errors or skips. Both JARs were checked for absence of QA classes.

The original development configuration remained unchanged and RCON remained disabled. No game/server process or test listener was left running. Raw logs and probe source are retained outside the GitHub repository for diagnosis.

Authenticated multiplayer/preferences/reconnect, client rendering, human equipment/enchantment interactions and XP pickup, and real player movement near late-loading origins remain pending. See [port status](PORT_STATUS.md) and [the original launch-fix report](FABRIC_DEV_QA_2026-10-25.md).
