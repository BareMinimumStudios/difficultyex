# Equipment, enchantments and XP acceptance

Validated October 26, 2026 on an isolated NeoForge 21.1.26 development server with the pinned public dependencies. No desktop automation, client window or authenticated player was used.

## Scripted-player fixture

The probe used a NeoForge FakePlayer subclass with a dummy connection. Its normal blanket damage immunity was overridden and its initial respawn invulnerability was cleared for incoming-damage checks. Attack cooldown was charged explicitly. Because FakePlayer does not run ordinary player ticks, held-item attribute modifiers were applied through the equipped ItemStack's normal modifier enumeration. Calls then used actual `Player.attack`, `Mob.doHurtTarget`, `LivingEntity.hurt`, death/XP-drop logic and `ExperienceOrb.playerTouch`.

This exercises server gameplay pipelines with PlayerEx and Data Attributes present. It does not validate client input, natural cooldown timing, normal player equipment synchronization or an authenticated player's experience.

## Combat results

| Scenario | Observed damage |
| --- | ---: |
| Level-20 zombie against scripted player, Easy | 5.5 |
| Same attack, Normal | 9.0 |
| Same attack, Hard | 13.5 |
| Diamond sword through Player.attack against level-20 zombie | 6.580002 |
| Sharpness III diamond sword against fresh equivalent target | 8.639999 |
| Plain sword against target wearing diamond chestplate | 1.4000015 |
| Plain sword against target wearing Protection IV diamond chestplate | 1.1759987 |

The assertions verify the expected difficulty amounts and relative enchantment/armor effects. These observed weapon values reflect the installed dependencies and fixture, not universal vanilla damage guarantees. Each strike used a fresh target and reset invulnerability/cooldown state. Mob equipment was processed through a normal mob tick.

## XP drop and pickup

PlayerEx's chunk XP reduction can discard newly created orbs. Repeated test kills initially exposed this integration and invalidated an assumption that every computed reward must survive as collectible orbs. For exact accounting, the disposable fixture temporarily set PlayerEx's negation factor to 100 and the chunk's factor to one, then restored the original snapshot. This prevented probabilistic suppression during the arithmetic/pickup cases.

| Scenario | XP dropped and credited |
| --- | ---: |
| Level-1 zombie, default rate 0.1 | 5 |
| Level-20 zombie, default rate 0.1 | 15 |
| Level-20 zombie with Looting III held by killer | 15 dropped |
| Level-20 zombie, rate 0 | 5 |
| Level-20 zombie, rate 0.5 | 55 |
| Level-20 zombie, rate -0.5 | 5 |
| Level-20 zombie, non-finite NaN snapshot rate | 5 |
| Level-20 zombie killed without player attribution | 0 |

Actual pickup increased the scripted player's total XP by the full surviving orb amount in the normal and rate cases. Merged-orb counts were read from saved NBT and each represented pickup was consumed. Pickup delay was reset by the fixture between calls; natural collision and delay timing are not claimed. The NaN case deliberately constructed an invalid in-memory snapshot; it is not evidence that the validated file editor accepts NaN.

With Mending on a sword damaged by 100 durability, a 10-XP orb repaired 20 durability and credited zero XP. A subsequent 50-XP orb repaired the remaining 80 and credited the leftover 10 XP. Both orbs were consumed.

Finally, the original PlayerEx snapshot was restored and the disposable chunk marked exhausted. A newly constructed 10-XP orb was immediately discarded, proving that DifficultyEx does not bypass PlayerEx's suppression. No production PlayerEx file was changed.

## Verification and cleanup

The final complete probe run passed and stopped gracefully, saving all dimensions. Initial fixture attempts with spawn invulnerability or uncontrolled chunk suppression are not counted as passing acceptance. Temporary source stayed outside the repository; the entrypoint was restored byte-for-byte and the disposable world and local run configuration were removed.

The final clean both-loader build passed 63 tests with zero failures, errors or skips. Both JARs were checked for absence of QA classes. The original development config and main branch remained unchanged; RCON stayed disabled and no game/server process or test listener remained.

Human-player combat, normal equipment/cooldown behavior and XP collection still need acceptance. Authenticated multiplayer, client visuals and real movement near late-loading structure origins remain separate pending checks. See [port status](PORT_STATUS.md).
