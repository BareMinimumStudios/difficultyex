# Final interactive acceptance

[Remaining work](../TODO.md) · [Release preparation](RELEASING.md) · [Configuration](CONFIGURATION.md)

These checks are pending. This guide is a run sheet, not evidence that they passed. Use disposable test worlds and matching loader/dependency builds. Repeat the client checks on Fabric and NeoForge. Record the exact JAR hash, loader, config and result for each run.

## Two authenticated clients

Use an online-mode dedicated server and two separately authenticated accounts. Keep the configured maximum level at least 27 for this fixture. One account needs operator permission level 2.

Summon one protected fixture in a loaded area:

```mcfunction
/summon minecraft:cow ~ ~ ~ {Tags:["difficultyex_acceptance"],NoAI:1b,Invulnerable:1b,PersistenceRequired:1b}
/difficultyex set @e[tag=difficultyex_acceptance,limit=1] 17
```

- [ ] Both clients see level 17 for that cow.
- [ ] Set level 27; both update without reconnecting.
- [ ] Client A disables nameplates in its Fzzy editor and selects Changes > Apply Changes. A loses the label; B retains it.
- [ ] B changes its health/bar/level display preferences. A's preferences remain independent.
- [ ] Reconnect A, then B. Each retains its own preferences and receives the current level.
- [ ] An ordinary non-operator cannot use DifficultyEx commands or apply server gameplay changes.
- [ ] An operator gameplay-config change reaches both clients while preserving their seven local preferences.

Use `/difficultyex inspect` to compare server state with the displayed state. The earlier codec and scripted-handler checks do not establish these actual network results.

## Client visual cases

- [ ] Each level, health-bar and HP-text toggle changes only its intended element.
- [ ] Disabling all elements leaves vanilla nameplate behavior intact.
- [ ] Labels respect configured distance, walls, spectator mode and global name rendering.
- [ ] Team name-tag visibility and viewer-relative invisibility are respected for same-team and other-team viewers, including friendly invisibility settings.
- [ ] Hostile-only and blacklist settings hide the appropriate labels.
- [ ] Passive, hostile, small, large and differently shaped mobs have readable labels without overlap or clipping during movement.

Record screenshots where useful.

## Human combat and XP

Use separate ordinary mobs without the protected fixture's Invulnerable flag.

- [ ] Normal attacks at low and high mob levels behave consistently with the documented scaling.
- [ ] Repeat with equipment, Sharpness, armor and Protection, using normal attack cooldowns.
- [ ] Collect eligible kill XP with and without damaged Mending equipment.
- [ ] Compare a fresh PlayerEx XP chunk with an exhausted chunk. Record downstream suppression rather than assuming all reward differences come from DifficultyEx.

Record difficulty, weapon, enchantments, mob level, relevant stat rates and collected XP. Scripted-player checks already cover arithmetic and several real interactions; human input and normal timing remain pending.

## Structures and populated-world performance

- [ ] Real players approach and leave a configured structure while chunks stream in and out.
- [ ] New mobs respond to available structure origins; already assigned mob levels stay stable.
- [ ] Repeat near multiple structure variants and overlapping minimum/maximum rules.
- [ ] Run a representative populated-world workload with players moving, ordinary mob AI and intended structure rules.

Record player count, mob count, loaded chunks, structure radius/rules, hardware and duration. Vanilla `/debug start` and `/debug stop` can capture a server profile. Retain the resulting file for analysis. Earlier empty-player stress samples are not a populated-world performance guarantee.

## Result record

| Item | Record |
| --- | --- |
| Artifact SHA256 and loader | Pending |
| Server config and client preferences | Pending |
| Accounts/player count | Pending |
| Scenario and expected behavior | Pending |
| Actual behavior and logs/screenshots/profile | Pending |
| Pass, fail or intentionally deferred | Pending |

Fix failures before marking acceptance complete. Explicitly review deferred items with the selected release channel. Delete disposable worlds or remove the fixture and any temporary team/config changes when finished.
