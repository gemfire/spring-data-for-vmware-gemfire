Type: grilling
Status: resolved

## Question

Existing Spring Data GemFire code references native `org.apache.geode.*` types directly in many places — including as annotation attribute types (e.g. `@ClientCacheApplication(dataPolicy = DataPolicy.PERSISTENT_REPLICATE)`). Do these all migrate to SPI-owned types as part of this effort, and is a compatibility facade preserving the old native-typed signatures kept alongside the new SPI-typed ones?

## Answer

This is a **breaking major version** migration: every native `org.apache.geode.*` type reference — including annotation attribute types — moves to a driver-SPI-owned type. There is **no permanent facade or adapter** preserving native-typed signatures for backward source/binary compatibility; the old native-typed APIs go away in the major version that adopts this architecture.

This decision governs [Annotation attribute types](05-annotation-attribute-types.md): since there's no facade to fall back on, annotation attributes must get real SPI-owned replacement types, not just an internal-only translation layer.
