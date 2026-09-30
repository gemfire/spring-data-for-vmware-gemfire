# Spring Data GemFire — commercial-only BOM leak

## Context

`spring-session-data-for-vmware-gemfire` (and other repos on the rollout list
in `GemFireLibRestriction.md`) use an opt-in Gradle property
`restrictGemFireLibsOnly` that, when enabled, restricts the internal/commercial
Artifactory repos (`tds-gemfire-maven-virtual`, `tds-gemfire-gradle-virtual`)
to resolve **only** `com.vmware.gemfire`-group artifacts, forcing everything
else to come from `mavenCentral()`. This is implemented via a single

```kotlin
exclusiveContent {
    forRepositories(*allInternalRepos)
    filter {
        includeGroup("com.vmware.gemfire")
    }
}
```

block, called once across *all* internal repos together. (Calling
`exclusiveContent { forRepository {...} }` separately per repo causes them to
mutually exclude each other for that group, which breaks resolution of
pinned versions even within the allowed group — this was a real bug we hit
and fixed during this rollout.)

## Problem found

While validating that restriction against a build of
`spring-session-data-for-vmware-gemfire`, we hit a problem that originates in
the `spring-data-gemfire` repo, not in the restriction logic:

The published artifact `com.vmware.gemfire:spring-data-2.7-gemfire-10.2:2.0.0-build.10`
has a POM that declares a BOM import/parent on
`org.springframework.data:spring-data-bom:2021.2.21`.

That exact version does **not exist on Maven Central** — public releases stop
at `2021.2.18` (confirmed via directory listing at
`https://repo.maven.apache.org/maven2/org/springframework/data/spring-data-bom/`).
It only exists in the internal Broadcom Artifactory repos (confirmed 200 OK
against both `tds-gemfire-maven-virtual` and `tds-gemfire-gradle-virtual` for
`org/springframework/data/spring-data-bom/2021.2.21/spring-data-bom-2021.2.21.pom`),
meaning it's a commercial-only/patched build of the Spring Data BOM.

This is a problem independent of the restriction feature: any consumer who is
*not* pointed at our internal repos (e.g. a public/OSS build using only Maven
Central) cannot resolve this artifact's POM at all, because its declared BOM
dependency doesn't exist publicly. Under the `com.vmware.gemfire`-only
restriction specifically, `includeGroup("com.vmware.gemfire")` correctly
excludes `org.springframework.data:spring-data-bom` from the internal repos
(it's not in that group), so it has nowhere to resolve from — surfacing this
pre-existing leak.

## Prompt to run in the `spring-data-gemfire` repo

> We maintain an opt-in Gradle property `restrictGemFireLibsOnly` in
> `spring-session-data-for-vmware-gemfire` (and are rolling it out to other
> repos) that, when enabled, restricts our internal/commercial Artifactory
> repos (`tds-gemfire-maven-virtual`, `tds-gemfire-gradle-virtual`) to
> resolve **only** `com.vmware.gemfire`-group artifacts, forcing everything
> else to come from `mavenCentral()`. This is implemented via a single
> `exclusiveContent { forRepositories(*allInternalRepos) ; filter {
> includeGroup("com.vmware.gemfire") } }` block (must be one call across
> *all* internal repos together — calling `exclusiveContent { forRepository
> {...} }` separately per repo causes them to mutually exclude each other
> for that group, which breaks resolution of pinned versions even within the
> allowed group).
>
> While validating that restriction against a build of
> `spring-data-for-vmware-gemfire`, we hit a real problem that originates in
> this repo: the published artifact
> `com.vmware.gemfire:spring-data-2.7-gemfire-10.2:2.0.0-build.10` has a POM
> that declares a BOM import/parent on
> `org.springframework.data:spring-data-bom:2021.2.21`. That exact version
> does **not exist on Maven Central** (public releases stop at
> `2021.2.18`) — it only exists in our internal Broadcom Artifactory repos,
> meaning it's a commercial-only/patched build of the Spring Data BOM.
>
> This is a problem independent of the restriction feature: any consumer who
> is *not* pointed at our internal repos (e.g. a public/OSS build using only
> Maven Central) cannot resolve this artifact's POM at all, because its
> declared BOM dependency doesn't exist publicly.
>
> Please investigate:
> 1. Find where this repo's build declares/produces the `2.0.0-build.10`
>    artifact and why its POM ends up pinned to `spring-data-bom:2021.2.21`
>    specifically (check version catalogs, BOM/platform declarations, or any
>    commercial-only override properties).
> 2. Determine whether `2021.2.21` is an internal-only patched version of
>    `spring-data-bom` that was never intended to leak into a published POM,
>    or whether it's a legitimate pin that should instead point to a
>    publicly available version (e.g. `2021.2.18` or later public release).
> 3. Fix the root cause so that published `com.vmware.gemfire` artifacts
>    don't declare POM dependencies on commercial-only, non-`com.vmware.gemfire`-
>    group artifacts that don't exist on Maven Central — since that breaks
>    any downstream consumer restricted to `com.vmware.gemfire`-only internal
>    resolution (and arguably any purely public build).
> 4. If this repo has (or should have) its own `restrictGemFireLibsOnly`-style
>    repository restriction, apply the same single-`exclusiveContent`/
>    `forRepositories` pattern described above — do not use one
>    `exclusiveContent { forRepository {...} }` call per repo.

## Status

- Fix confirmed correct in `spring-session-data-for-vmware-gemfire` on
  `support/10.2-3.5-2.0.x` for the mutual-exclusion bug itself (all three
  files: `settings.gradle.kts`, `build-tools/publishing/build.gradle.kts`,
  `build-tools/convention-plugins/build.gradle.kts`).
- Remaining failure (`Could not find org.springframework.data:spring-data-bom:2021.2.21`)
  is blocked on the upstream fix in `spring-data-gemfire` described above —
  not a bug in the restriction logic here.
