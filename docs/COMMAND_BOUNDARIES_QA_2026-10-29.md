# Command boundaries and diagnostic output

Verified October 29, 2026 using isolated Minecraft 1.21.1 development servers with Fabric Loader 0.20.1 and NeoForge 21.1.26. Both loaders passed the same actual dispatcher checks.

## Permissions and inputs

- Permission levels 0 and 1 were denied for validate, inspect and set. Mob state and the settings snapshot remained unchanged. Permission level 2 could validate.
- Zero, negative values, integer overflow, fractional values and NaN were rejected as command levels without mutation.
- A configured maximum of 17 rejected level 18 with the correct range message; endpoints 1 and 17 were accepted.
- Both inspect and set rejected an armor stand with the mob-only error.
- Missing target/level arguments, selectors allowing multiple targets and a selector with no matching entity were rejected without mutation.

## Health and inspection

The actual set command preserved a cow's injured health ratio and did not revive zero HP. Passive inspection returned level 17 and reported the absent attack attribute as `n/a`.

## Diagnostic limit

A runtime snapshot with 25 unknown dimension rules produced exactly 22 feedback messages: a summary, 20 details and a five-more footer. All 25 individual findings appeared in each server's log. Validation did not mutate the snapshot or mob state.

## Scope and cleanup

The fixture invoked the registered dispatcher with scripted sources, permission levels and bound entities. It created no authenticated players and did not test client chat presentation, network command-tree delivery or remote administration. The bound cow and armor stand were real, untracked entity instances. Deliberately invalid rules were supplied in memory; no user config file was edited.

Both servers stopped gracefully and saved all dimensions. Temporary source hooks, local run configurations and disposable server directories were removed. The final IDE clean/test/build passed 69 tests across 14 suites with zero failures, errors or skips. Both loader JARs include both required licenses and no temporary QA classes. All 28 Markdown files parsed, 160 repository/local links resolved, all 27 settings remained documented and hidden desktop/mobile previews passed. The original development config hash is unchanged.
