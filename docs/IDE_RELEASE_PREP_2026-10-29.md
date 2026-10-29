# IDE source resolution and release tooling

Verified October 29, 2026.

## Injection inspections

Shared mixins were inspected in Cloche's common module against API stubs without method bodies. Client source imports also lacked usable game dependencies in that module.

Moved the two server mixins, two client mixins and nameplate helper unchanged into src/loaderShared/java. Both loader source sets compile that directory against their full game classpaths. Common gameplay code continues using the common API. Packages, class names, injector selectors and the client-only mixin JSON section are unchanged. The IDE move tool only supports package changes within one source root, so the cross-root relocation used native file moves.

After Gradle sync, file inspections reported zero errors for MobLevelMixin, MobExperienceMixin, MobNameplateMixin, EntityNameplateMixin and DifficultyExNameplates. No inspection suppression or injector rewrite was used. This establishes the connected IDE's inspection result; it is not client visual acceptance.

The clean IDE build passed 69 tests across 14 suites with zero failures, errors or skips. Both JARs contain all four mixin classes, both licenses and no temporary QA classes. Rebuilt standalone Fabric 0.20.1 and NeoForge 21.1.256 servers started, executed config validation and level/health commands, and shut down with all dimensions saved. Fabric's initial fixture sent selectors before the summoned mob was queryable; commands spaced across ticks passed without a production change. Disposable servers were removed and reference servers were untouched.

## Release preparation

The helper passed ten tests for manifest hashes and sizes, missing/empty/duplicate/wrong-version artifacts, directory rejection, missing/mismatched notes, invalid/duplicate versions, invalid channels and command-line GitHub output. The actual current artifacts also passed preflight. Workflow syntax passed actionlint 1.7.12.

The publish workflow defaults to alpha, offers explicit beta/release choices and uses focused release notes. CI archives the hash manifest and notes beside both loader JARs. The mod version remains 1.1.0. Credentials, project upload permissions, actual publication and outstanding gameplay acceptance were not tested. See [release steps](RELEASING.md).
