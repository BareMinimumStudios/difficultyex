# Entity lifecycle and configuration changes

Verified October 29, 2026 with Minecraft 1.21.1, Fabric Loader 0.20.1 and NeoForge 21.1.26. Both isolated dedicated development servers passed the same checks with the pinned dependencies.

## Fourteen mob types

Cow, zombie, skeleton, creeper, spider, slime, bat, villager, wolf, bee, iron golem, snow golem, squid and warden each passed:

- Level 20 initialization with deterministic settings.
- Ten actual entity NBT save/load cycles into newly created instances.
- Preservation of assigned level, injured HP at 37.5% of scaled maximum, maximum health, armor and attack attributes where present.
- Exactly one DifficultyEx modifier for each supported scaled attribute after every cycle.
- A further save/load of zero HP without revival.

This totals 280 injured-entity save/load cycles across both loaders, plus 28 zero-health cycles. Mobs without an attack attribute were supported without requiring one.

## Configuration changes

A level-20 zombie retained its assigned level and current attributes when a new settings snapshot was published. Updating settings alone did not eagerly rescale existing instances.

A case-insensitive exact blacklist blocked initial level assignment for a new zombie. Reinitializing the already leveled zombie retained level 20 despite that blacklist and applied the changed health rate. A separate transient additive health modifier, representing another mod, remained intact and continued contributing to maximum health.

After removing the blacklist, a new zombie used the changed starting level of 40. Zero health rate, negative armor rate and non-finite damage rate removed only DifficultyEx's corresponding modifiers through ten reinitializations; the unrelated health modifier remained. The non-finite value was supplied directly to the runtime snapshot to test its defensive handling, not entered through the validated config editor.

## Scope

The fixture created real registry mob instances and explicitly invoked initialization. It did not add these instances to world tracking or exercise natural spawn events, AI, death loot, client rendering or network delivery. NBT cycles were in-memory entity serialization, not full world restarts. Prior packaged-server restart checks remain separate evidence.

Mob-specific projectile damage, taming, slime-size changes, boss fights and all behavior combinations remain outside this check. Human gameplay, authenticated multiplayer, client visuals and populated-world performance remain open.

## Cleanup and build

Both servers shut down gracefully and saved all dimensions. Disposable server directories and temporary source hooks were removed. Original development configuration was preserved. The final IDE clean/test/build passed 69 tests across 14 suites with zero failures, errors or skips. Both loader JARs contain both required licenses and no temporary QA classes. Documentation parsing, 156 repository/local links, all 27 setting entries and hidden desktop/mobile previews passed.
