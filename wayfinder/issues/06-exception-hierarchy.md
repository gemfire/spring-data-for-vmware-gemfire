Type: grilling
Status: resolved

## Question

GemFire throws its own native exception types, and Spring's `GemfireCacheUtils`-style translation layer currently converts these directly into Spring's `DataAccessException` hierarchy. Once Spring Data GemFire no longer references GemFire directly, what exception types cross the SPI boundary?

## Answer

The SPI owns its **own exception hierarchy**. Driver Impls are responsible for translating native GemFire exceptions into SPI-defined exception types at the boundary. Spring's existing `GemfireCacheUtils`-style translation to `DataAccessException` is retargeted to translate **from SPI exceptions**, not from native GemFire exceptions — Spring Data GemFire code never catches or references a native GemFire exception type.
