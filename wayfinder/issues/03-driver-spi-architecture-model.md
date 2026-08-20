Type: grilling
Status: resolved

## Question

What is the overall shape of the abstraction: how do Spring Data GemFire, the SPI/API layer, and version-specific GemFire drivers relate to each other, and how are drivers discovered at runtime?

## Answer

Model this on the **JDBC driver pattern**: two artifact kinds.

- **Driver SPI/API** — bundled with Spring Data GemFire. Spring Data GemFire code compiles only against this; it has no direct reference to GemFire (`org.apache.geode.*`) at all.
- **Driver Impl** — one per supported GemFire version, a swappable runtime dependency, implementing the SPI's contracts against a specific GemFire version's native API.

Drivers are discovered and registered via **`ServiceLoader`**, auto-discovered/auto-registered at runtime rather than wired explicitly by the application. This lets a single Spring Data GemFire release (per Spring Data version) run against multiple GemFire versions by swapping which Driver Impl is on the runtime classpath.
