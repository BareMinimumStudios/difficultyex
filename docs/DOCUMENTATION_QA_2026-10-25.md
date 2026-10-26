# Documentation and artwork validation

Validated October 25, 2026. No visible application interaction, game launch, upload or publication was used for this pass.

- New DX logo and Difficulty EX banner were visually inspected against the supplied logo and Remnant studio references.
- Logo and packaged icon are square PNGs, 1254 by 1254. Banner is a 3:1 PNG, 2172 by 724.
- All 27 shared configuration fields appear in the configuration reference. Descriptions were checked against current defaults and scaling implementation.
- Markdown was parsed and local links plus branch-specific repository URLs were resolved against files in this checkout.
- README desktop (1120 px), README mobile (390 px) and CurseForge copy (960 px) were rendered through headless Edge. No page-level horizontal overflow occurred. Tables and code blocks can scroll within their containers. Remote badges were blocked for the local previews, so their network rendering is not claimed.
- The existing IntelliJ clean/test/build configuration produced both loader JARs and fresh reports for 63 tests: zero failures, errors or skips. Both JARs contain the refreshed icon.
- Original development config SHA256 remained B441095EEA5B7ED42E0DDB48C9DF84F60E8457F0A383E45725BF8A3D595D3F3C. Main was unchanged.

The platform Markdown uses absolute 1.21.1 branch URLs and becomes shareable after the staged files are pushed. No claim is made that those new remote image URLs already exist. Public project IDs and the CurseForge project URL were checked; account permissions and publication were not tested.

This pass does not resolve the Fabric development launch or complete multiplayer/gameplay acceptance. See [port status](PORT_STATUS.md). Image generation prompts and asset usage are recorded in [branding](BRANDING.md).
