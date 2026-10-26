# Operator commands

[Documentation](README.md) · [Configuration](CONFIGURATION.md)

All `/difficultyex` commands require **permission level 2**. These commands change or inspect mobs, not player progression.

| Command | Result |
| --- | --- |
| `/difficultyex validate` | Checks active server registry rules, patterns and formula syntax without changing settings |
| `/difficultyex inspect <target>` | Reports a mob's level, fractional health, armor and attack attribute |
| `/difficultyex set <target> <level>` | Assigns a level from 1 to 1,000,000 and updates scaling while preserving health percentage |

## Examples

Inspect the nearest zombie:

```mcfunction
/difficultyex inspect @e[type=minecraft:zombie,sort=nearest,limit=1]
```

Set the nearest cow to level 20:

```mcfunction
/difficultyex set @e[type=minecraft:cow,sort=nearest,limit=1] 20
```

Check the active configuration:

```mcfunction
/difficultyex validate
```

The validator shows up to 20 findings in chat and logs all findings. Unmatched entity patterns and rules for absent mods may be intentional; review their messages before removing rules. A passive mob without an attack attribute reports `n/a`.

The optional Fzzy editor uses `/configure difficultyex`. Select **Changes > Apply Changes** to save; closing with **Done** alone does not apply pending edits.
