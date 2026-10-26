# Remaining port work

[Port status](docs/PORT_STATUS.md) · [QA checklist](docs/QA_CHECKLIST.md) · [Feature parity](docs/PARITY_AUDIT_1.20.1.md)

## Acceptance

- [ ] Two authenticated clients: live metadata, independent preferences and reconnect.
- [ ] Player combat with equipment/enchantments and human XP pickup.
- [ ] Fabric nameplates and visual distance, occlusion, team and invisibility cases.
- [ ] Real player movement around partially loaded structures and late origins.
- [ ] Populated-world performance with many mobs and structure rules.

## Development and release

- [ ] Resolve the Fabric named development-launch dependency injector mismatch.
- [ ] Resolve IntelliJ common-stub injection inspection false positives.
- [ ] Review release version, changelog, credentials and platform upload acceptance.

## Optional parity

- [ ] Decide whether to restore GeckoLib render hooks, Traveler's Titles and old nameplate styling controls.

Core progression, configurable stat scaling, area/entity bounds, commands, saved-level migration and base nameplates are implemented. The old speculative rules-engine outline is not the current configuration format.
