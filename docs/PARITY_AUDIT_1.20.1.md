# 1.20.1 behavior parity audit

Compared the original implementation at main commit `e38e71bc063ce45825bd9f911589d7f34eed0e9c` with the 1.21.1 port. This audit distinguishes implemented behavior from scaffolding; it does not establish full runtime acceptance.

| Prior behavior | 1.21.1 status |
| --- | --- |
| PlayerEx formula, average and random variation | Implemented. Real-library fake-player scenarios passed on NeoForge. Authenticated multiplayer remains pending. |
| Dimension, biome, entity and structure bounds | Implemented with explicit maximum priority. Structure influence now uses loaded horizontal bounding boxes and a block radius, rather than nearest-structure searches that could load terrain. |
| Nearby-player eligibility | Now a true inclusive 3D radius and excludes spectators. The prior code used an inflated block AABB. |
| Health, armor, damage and XP scaling | Implemented using vanilla attributes and the common XP base hook. This replaces wrapped getters and the old mob-specific XP return hook. Human equipment/enchantment checks remain pending. |
| Mob levels persisted in CCA | Prior compound data now imports lazily to the modern synchronized level tag. Real loading and packaged Fabric restart passed with a verified fixture. Production-world upgrade acceptance remains pending. |
| Scaling and visual config | Supported legacy fields import into JSON5. A real ALBOE config passed selected-field startup checks. Config-first operation and the optional Fzzy editor replace owo-lib. |
| Nameplate level, health bar/text, distance, hostile filter, blacklist | Implemented using vanilla labels. Basic NeoForge visual checks passed; other rendering and multiplayer checks remain pending. |
| Nameplate Y offset, scale and custom colors | Not ported. These are optional visual controls, not server progression settings. |
| GeckoLib renderer hooks | Not ported or tested. Optional compatibility work. |
| Traveler's Titles region-level augmentation | Previously active through optional mixins/networking. Not ported; optional compatibility work. |
| Personal difficulty selector | The option was commented out. A Medium enum value was stored in a component but was not read by scaling or combat. This was scaffolding, not an implemented gameplay selection feature to restore. |
| EntityLevelingEvents public callbacks | The prior Fabric callback API and old namespace are not preserved. Core behavior is implemented directly across loaders. External API consumers would need an explicit compatibility scope. |
| Operator tools | The port adds inspect, set and active-config validation commands. |

The port remains under development. Optional visuals, integrations and old public API compatibility are explicit scope decisions; they should not be confused with unfinished core scaling mechanics. Required acceptance still includes authenticated multiplayer, human combat/XP, visual policy checks for the current renderer, and active-world performance/late-origin movement. Fabric packaged behavior passes; its named development setup still needs a supported fix.
