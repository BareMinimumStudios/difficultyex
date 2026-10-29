# Configuration serialization and permission acceptance

Verified on October 29, 2026 with Fabric Loader 0.20.1 and NeoForge 21.1.26 development servers, Minecraft 1.21.1 and Fzzy Config 0.7.7. All tests ran in isolated, headless servers with no connected clients.

## Both loaders

- Ten real-library synchronization serialization rounds applied all 20 gameplay fields to two distinct configuration objects while preserving all seven independent local preferences.
- Synchronization output excluded the seven local fields. Full JSON5 file save/reload preserved gameplay settings and each object's local preferences.
- Malformed JSON5 returned an error without changing the configuration.
- Both client configuration callbacks published their current snapshot when invoked directly.

## NeoForge server permission handler

A scripted server player submitted updates through the actual Fzzy configuration registry handler. The fixture supplied the scripted player's permission level and invoked the handler directly. The real permission validator and deserializer ran unchanged; sender callbacks asserted that no network sends occurred with zero connected clients.

- Actual/claimed permission pairs 0/0, 1/1, 0/4 and 2/0 were rejected without mutation.
- Malformed operator input was rejected without mutation.
- Matching permission levels 2 and 4 applied starting levels 37 and 41 and published the DifficultyEx server snapshot.
- A local-only preference payload could not modify the server configuration.
- Rejected updates entered the real quarantine. Its rejection error messages were expected test output.

## Limits and cleanup

These are configuration-object and server-handler checks. They do not verify actual two-client transport, login, reconnect, live client metadata or rendering. Authenticated multiplayer acceptance remains open.

Both servers stopped gracefully and saved all dimensions. Temporary source hooks and local run configurations were removed. Disposable server directories were deleted; original development configuration was preserved. The final IDE clean/test/build passed 63 tests across 14 suites with zero failures, errors or skips. Both loader JARs contain both required licenses and no temporary QA classes. The original config SHA256 remains B441095EEA5B7ED42E0DDB48C9DF84F60E8457F0A383E45725BF8A3D595D3F3C. RCON is disabled and ports 25565, 25575 and 25586 have no listeners.
