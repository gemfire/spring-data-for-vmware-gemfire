# Client function permission values — compatibility review

Task 64 continuation, 2026-10-28. The two function permission callbacks are required-client under the [function disposition](client-function-member-scope.md#client-function-permission-disposition). Their reachable value family contains **57 required-client frozen rows**: 31 on `ResourcePermission` and 26 on its three nested enums. The disposition below retains typed equivalents; all 57 implementation statuses remain missing. Tasks 24/25 own the eventual native-free value/callback implementation; task 32 owns function consumer adoption, alongside task 35 for security entry points.

## Pinned evidence

Review uses GemFire **10.4.0-build.3056.df98c835**, source commit `df98c8357afe85853d9a4776255d4f0e7cf064cb` in the local GemFire checkout. Relevant sources:

- `gemfire-core/src/main/java/org/apache/geode/security/ResourcePermission.java`: constructors, component fields/accessors, enum vocabulary, aliases and delegation to inherited equality/implication.
- `gemfire-core/src/test/java/org/apache/geode/management/internal/security/ResourcePermissionTest.java`: native constructor, null/wildcard, target normalization, implication and rendering expectations.
- `gemfire-shiro/src/main/java/org/apache/shiro/authz/permission/WildcardPermission.java` at the same pinned commit: parser, protected parts access/mutation, implication, equality and hashing. Confirmed against `javap -c -p -constants` on the resolved `gemfire-shiro-10.4.0-build.3056.df98c835.jar`. GemFire ships its own implementation under the Shiro package name; upstream `shiro-core:2.2.1` is not the behavior authority. The inventory now resolves inherited signatures from the bundled artifact as well.

## Representation findings

| Surface | Source-established behavior | Consequence for the typed mapping |
| --- | --- | --- |
| Ten constructors | Empty construction uses NULL/NULL/*/*. Component forms normalize resource/operation enum names and strip leading slashes from string targets. Target/key case remains significant; null target/key default to wildcard. | Preserve construction mode, defaults and validation. Region callback arguments themselves must not be normalized. |
| Wildcard-string constructor | Calls GemFire's inherited parsing, which defaults to case-sensitive parsing and preserves case. It leaves component fields at NULL/NULL/*/*. | Component getters and `toString()` are not a lossless serialization. Copying four fields can silently change permission semantics. |
| Equality/hash versus implication | Equality compares ordered part lists containing subpart sets. Implication allows omitted trailing parts and wildcards. `data:read` and `data:read:*:*` imply one another but are not equal. | Do not canonicalize away trailing wildcard parts if native equality must survive. Keep comparison separate from authorization. |
| Component target/key subparts | Comma-separated target/key values become sets; their input strings remain in fields. Set order does not affect equality, while component rendering may differ. | Preserve effective parts and observable component values, rather than derive one from the other. |
| Protected inherited methods | `getParts()` returns the backing list; `setParts(List)` retains the supplied list. The one-argument string setter preserves case; the boolean overload lowercases only when explicitly passed false, independently of component fields. | A claim that the native value is always immutable is incorrect despite its annotation. Use instance-owned typed declaration editing and copy both list and sets at transport boundaries, as specified below. |
| Generic inherited `implies(Permission)` | Accepts other wildcard permissions; unrelated implementations return false. | Review the retained typed comparison domain without importing Shiro's open permission interface. This overload is not authorization-service API evidence. |
| Enum and alias vocabulary | ALL maps to `*`; NULL is a distinct sentinel. CLUSTER/MANAGE and target labels can occur as function declarations. Deprecated region/key aliases share wildcard values; `getRegionName()` delegates to `getTarget()`. | Declaration vocabulary does not authorize adding server administration or hosting operations. Preserve retained aliases by equivalent native-free behavior where required. |

The source suggests a native-free value will need to distinguish component construction from parsed wildcard construction, and preserve effective part structure separately where necessary. The disposition below fixes the semantic requirements; public names and adapter implementation remain task 24 work. Do not add a native/Shiro escape hatch or silently replace unsupported declarations with an empty permission list. Permission callbacks remain dynamic and execute on the server-selected function; local declaration comparison does not establish a user's authorization.

## Native probes — validated 2026-10-28

[NativeClientPermissionValueTests](../../spring-data-gemfire-driver-10.4/src/integrationTest/java/com/vmware/gemfire/driver/gemfire104/NativeClientPermissionValueTests.java) contains seven tests:

1. Component defaults, nulls, aliases, target normalization and case preservation.
2. Wildcard construction's different effective parts and component fields, including loss through getters/rendering.
3. Component subpart-set equality/hashing and case-sensitive implication.
4. Wildcard implication versus strict value equality, including omitted trailing parts.
5. Protected setters' case modes and backing-list/nested-set aliasing, through a private test subclass.
6. Native Java serialization preserving both representations, equality and implication.
7. Malformed wildcard/component declarations failing rather than becoming unrestricted permissions.

These are private native compatibility probes, not a new SPI or Spring dependency. Serialization here is a native Java object round trip; it does not prove GemFire wire transport of a future SPI function adapter. No secured authorization, complete constructor/enum matrix, arbitrary subclass adaptation, or final immutable snapshot policy is claimed. The inherited getter/setter rows and generic comparison overload have individual equivalent-behavior decisions below; these native probes do not validate their future typed implementation.

The first user-run result compiled these probes and reported two assertion failures among ten tests. Both came from incorrectly assuming upstream Shiro's case-insensitive default. The pinned GemFire source and bytecode establish `DEFAULT_CASE_SENSITIVE = true`. The corrected assertions preserve case for wildcard construction and the default setter; the explicit false mode still checks lowercasing and implication. The other five value tests and three permission-hook tests passed in that run. The corrected rerun passed: local JUnit XML records all seven value tests and three permission-hook tests with zero failures, errors or skips (2026-10-28T00:17:57–00:17:59Z). The user confirmed the full command passed, including transitional migration boundary verification.

Validated command, run by the user under the standing Gradle workflow: The existing three permission-hook tests are included because they establish the client submission path that makes these values relevant. Neither suite establishes secured allow/deny behavior.

```sh
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-gemfire-driver-10.4:integrationTest \
  --tests '*NativeClientPermissionValueTests' \
  --tests '*NativeClientFunctionPermissionTests' \
  verifyMigrationBoundary
```

## Client permission value disposition

All **57 rows are required-client through typed equivalents**, not a requirement to copy native class names, inheritance or overload shapes. The entry path is a retained client function's permission callback, not server security-manager configuration. Pinned implementation plus the validated probes establish locally constructible and inspectable declarations. In particular, a client may submit a function declaring CLUSTER:MANAGE:DEPLOY; retaining that vocabulary does not provide deployment or server administration operations.

| Decision group | Rows | Required equivalent |
| --- | ---: | --- |
| construction | 11 | Declaration type and all ten constructor forms, mapped to typed construction with native defaults, null handling, normalization, parsing and validation. Keep component and wildcard construction distinct. |
| aliases | 4 | Wildcard, NULL and legacy key/region wildcard values; aliases may converge on the same typed constant. |
| observation | 8 | Seven component getters (including region alias) and component-based rendering. Preserve their divergence from effective parts. |
| comparison | 3 | Equality/hash by ordered parts containing sets, and typed implication including omitted trailing parts. Neither rendering nor mutual implication defines equality. |
| vocabulary | 26 | Three enum types, all constants, getName, valueOf and values behavior, including wildcard name mapping and invalid lookup. |
| inherited members | 5 | Individual decisions below; preserve client declaration manipulation/comparison without importing Shiro. |

### Inherited member dispositions

| Frozen ID | Member | Equivalent and boundary |
| --- | --- | --- |
| `API-b30cc8cb42089f2e` | `getParts()` | Retain effective-part inspection and editing through instance-owned typed declaration state. The native backing list and nested sets are mutable: an immutable four-field projection is not equivalent. |
| `API-da2ddaa872a33f9c` | `setParts(String)` | Typed editing replaces effective parts with the pinned case-sensitive parse, leaving component fields unchanged; preserve malformed-input failure. |
| `API-1865955877f32d48` | `setParts(String, boolean)` | Retain explicit case mode, including native lowercasing when false. Test locale-sensitive behavior against each pinned implementation; do not assume a case-insensitive default. |
| `API-85b97f0041dfdae6` | `setParts(List)` | Retain ordered parts, set semantics and supplied-list/nested-set alias effects within local editable declarations. This is instance state, not a static registry. Invalid structures must never silently become unrestricted declarations; specify rejection/failure parity in task 24. |
| `API-c674050c447c2a62` | `implies(Permission)` | Map the wildcard comparison domain to typed permission parts, including parsed wildcard values not expressible through the component enums. An unrelated native Permission implementation returns false and is not a supported typed authorization-provider extension. Do not export Shiro's open interface or adapt arbitrary native subclasses. |

### Conversion and acceptance ownership

Task 24 owns the native-free declaration/editing model and versioned conversion. Preserve component fields and effective parts separately. Retain local editing/alias behavior; when a declaration crosses transport or is explicitly frozen, copy both the list and each set so later edits cannot alter the captured value. This snapshot is per evaluation/transport, never a cache of a function's permission policy. Do not add a permanent native-typed facade or native unwrap. The exact Java API and private conversion strategy must satisfy these behaviors before any row advances from missing.

Acceptance must cover all ten construction forms and all enum/alias rows, malformed input, default and explicit case modes, parts replacement independent of fields, list and nested-set mutation, equality versus implication, unrelated/null comparison handling, rendering, and serialization of both representations. Validate that changing an editable source after capture does not change the snapshot, while the next callback evaluation observes new state. The seven native probes are initial evidence, not this complete matrix or a tested typed conversion.

Task 25 consumes the values in both permission hooks, preserves dynamic region/argument declarations on the server-selected function and proves submitted-object transport. Combine tasks 24/25 for secured allow/deny, conjunctive permissions, no body effects on denial, region/key/wildcard restrictions and per-user authenticated-view tests. Never normalize callback arguments to permission targets or pre-authorize on the client. Native Java serialization probes alone do not establish future SPI adapter wire compatibility.

Task 32 is the primary consumer owner for all 57 rows: migrate function submission/configuration and typed failures using native-free collaborators. Task 35 retains entry-point wiring and must not introduce native/Shiro security types. Pure declaration/parts tests can run without native dependencies; the mock may exercise those local semantics but cannot claim remote authentication or authorization. Preserve server-backed tests under each versioned driver.

Scope is now **499 required-client / 629 excluded-server / 2,723 unresolved**. All 3,851 IDs and implementation totals are unchanged. Task 64 still owns remote function submission representation and the remaining unresolved families; parent 59 remains open.

## Frozen member ledger

| ID | Receiver | Member and descriptor | Required-client group |
| --- | --- | --- | --- |
| `API-5b5a2ca144d04b60` | `ResourcePermission` | `<init>()V` | construction |
| `API-fb5bd06cdb30ff4d` | `ResourcePermission` | `<init>(Ljava/lang/String;)V` | construction |
| `API-cf415095915e290b` | `ResourcePermission` | `<init>(Ljava/lang/String;Ljava/lang/String;)V` | construction |
| `API-11369a9e881e2b88` | `ResourcePermission` | `<init>(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V` | construction |
| `API-556cab629859e2d9` | `ResourcePermission` | `<init>(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V` | construction |
| `API-ae93cd089e291927` | `ResourcePermission` | `<init>(Lorg/apache/geode/security/ResourcePermission$Resource;Lorg/apache/geode/security/ResourcePermission$Operation;)V` | construction |
| `API-c5abe6c2979a2d27` | `ResourcePermission` | `<init>(Lorg/apache/geode/security/ResourcePermission$Resource;Lorg/apache/geode/security/ResourcePermission$Operation;Ljava/lang/String;)V` | construction |
| `API-714d539d88003d5d` | `ResourcePermission` | `<init>(Lorg/apache/geode/security/ResourcePermission$Resource;Lorg/apache/geode/security/ResourcePermission$Operation;Ljava/lang/String;Ljava/lang/String;)V` | construction |
| `API-909fe4bf52c43295` | `ResourcePermission` | `<init>(Lorg/apache/geode/security/ResourcePermission$Resource;Lorg/apache/geode/security/ResourcePermission$Operation;Lorg/apache/geode/security/ResourcePermission$Target;)V` | construction |
| `API-58f07e553604e4d2` | `ResourcePermission` | `<init>(Lorg/apache/geode/security/ResourcePermission$Resource;Lorg/apache/geode/security/ResourcePermission$Operation;Lorg/apache/geode/security/ResourcePermission$Target;Ljava/lang/String;)V` | construction |
| `API-6659600c09e01462` | `ResourcePermission` | `<type>` | construction |
| `API-623542206c62e9f6` | `ResourcePermission` | `ALLLjava/lang/String;` | aliases |
| `API-20787b4e0344645d` | `ResourcePermission` | `ALL_KEYSLjava/lang/String;` | aliases |
| `API-505a475bed6d7212` | `ResourcePermission` | `ALL_REGIONSLjava/lang/String;` | aliases |
| `API-bbd80b19af74f12a` | `ResourcePermission` | `NULLLjava/lang/String;` | aliases |
| `API-91904a177f4cf9aa` | `ResourcePermission` | `equals(Ljava/lang/Object;)Z` | comparison |
| `API-09d9c68043b3a70c` | `ResourcePermission` | `getKey()Ljava/lang/String;` | observation |
| `API-a095b7f22fb133aa` | `ResourcePermission` | `getOperation()Lorg/apache/geode/security/ResourcePermission$Operation;` | observation |
| `API-b8f3b8ed04825728` | `ResourcePermission` | `getOperationString()Ljava/lang/String;` | observation |
| `API-b30cc8cb42089f2e` | `ResourcePermission` | `getParts()Ljava/util/List;` | parts-access |
| `API-0202919ea56b98f8` | `ResourcePermission` | `getRegionName()Ljava/lang/String;` | observation |
| `API-1c631eaf995926bd` | `ResourcePermission` | `getResource()Lorg/apache/geode/security/ResourcePermission$Resource;` | observation |
| `API-6d87f1dc3758bc4b` | `ResourcePermission` | `getResourceString()Ljava/lang/String;` | observation |
| `API-1b79fb92a86a17c7` | `ResourcePermission` | `getTarget()Ljava/lang/String;` | observation |
| `API-732204ee709d7fb3` | `ResourcePermission` | `hashCode()I` | comparison |
| `API-addcb0302e3e87e3` | `ResourcePermission` | `implies(Lorg/apache/geode/security/ResourcePermission;)Z` | comparison |
| `API-c674050c447c2a62` | `ResourcePermission` | `implies(Lorg/apache/shiro/authz/Permission;)Z` | wildcard-comparison |
| `API-da2ddaa872a33f9c` | `ResourcePermission` | `setParts(Ljava/lang/String;)V` | parts-default-parse |
| `API-1865955877f32d48` | `ResourcePermission` | `setParts(Ljava/lang/String;Z)V` | parts-case-mode |
| `API-85b97f0041dfdae6` | `ResourcePermission` | `setParts(Ljava/util/List;)V` | parts-list |
| `API-08481d3c360c0836` | `ResourcePermission` | `toString()Ljava/lang/String;` | observation |
| `API-2a8ad4c0a4826755` | `ResourcePermission$Operation` | `<type>` | vocabulary |
| `API-79723f7e16f5b0ae` | `ResourcePermission$Operation` | `ALLLorg/apache/geode/security/ResourcePermission$Operation;` | vocabulary |
| `API-8ce9481e4b389a17` | `ResourcePermission$Operation` | `MANAGELorg/apache/geode/security/ResourcePermission$Operation;` | vocabulary |
| `API-bb3ba98c913da7d2` | `ResourcePermission$Operation` | `NULLLorg/apache/geode/security/ResourcePermission$Operation;` | vocabulary |
| `API-b44ee0e9b47e8dc9` | `ResourcePermission$Operation` | `READLorg/apache/geode/security/ResourcePermission$Operation;` | vocabulary |
| `API-8cd325d9236f50c0` | `ResourcePermission$Operation` | `WRITELorg/apache/geode/security/ResourcePermission$Operation;` | vocabulary |
| `API-9692d872fa5dd37b` | `ResourcePermission$Operation` | `getName()Ljava/lang/String;` | vocabulary |
| `API-42ee4cc39e4eebeb` | `ResourcePermission$Operation` | `valueOf(Ljava/lang/String;)Lorg/apache/geode/security/ResourcePermission$Operation;` | vocabulary |
| `API-04f9dfe50001638f` | `ResourcePermission$Operation` | `values()[Lorg/apache/geode/security/ResourcePermission$Operation;` | vocabulary |
| `API-111473ea11296fb1` | `ResourcePermission$Resource` | `<type>` | vocabulary |
| `API-9623e7eb1fe1d7f5` | `ResourcePermission$Resource` | `ALLLorg/apache/geode/security/ResourcePermission$Resource;` | vocabulary |
| `API-2367de8eee1ba62b` | `ResourcePermission$Resource` | `CLUSTERLorg/apache/geode/security/ResourcePermission$Resource;` | vocabulary |
| `API-3666244bea9c4472` | `ResourcePermission$Resource` | `DATALorg/apache/geode/security/ResourcePermission$Resource;` | vocabulary |
| `API-042880a6cb37a186` | `ResourcePermission$Resource` | `NULLLorg/apache/geode/security/ResourcePermission$Resource;` | vocabulary |
| `API-31cc83253aca6598` | `ResourcePermission$Resource` | `getName()Ljava/lang/String;` | vocabulary |
| `API-28b244ef154ccadc` | `ResourcePermission$Resource` | `valueOf(Ljava/lang/String;)Lorg/apache/geode/security/ResourcePermission$Resource;` | vocabulary |
| `API-0e3f321a50cfdeca` | `ResourcePermission$Resource` | `values()[Lorg/apache/geode/security/ResourcePermission$Resource;` | vocabulary |
| `API-97e3841e98835236` | `ResourcePermission$Target` | `<type>` | vocabulary |
| `API-ccdcc943bfc7c28c` | `ResourcePermission$Target` | `ALLLorg/apache/geode/security/ResourcePermission$Target;` | vocabulary |
| `API-01409ea1d4c34dae` | `ResourcePermission$Target` | `DEPLOYLorg/apache/geode/security/ResourcePermission$Target;` | vocabulary |
| `API-56068acf4ae0ff73` | `ResourcePermission$Target` | `DISKLorg/apache/geode/security/ResourcePermission$Target;` | vocabulary |
| `API-faa367c80cbad740` | `ResourcePermission$Target` | `GATEWAYLorg/apache/geode/security/ResourcePermission$Target;` | vocabulary |
| `API-886f7671cbd61617` | `ResourcePermission$Target` | `QUERYLorg/apache/geode/security/ResourcePermission$Target;` | vocabulary |
| `API-ad42d574d95ec6c8` | `ResourcePermission$Target` | `getName()Ljava/lang/String;` | vocabulary |
| `API-886a663b9b790e01` | `ResourcePermission$Target` | `valueOf(Ljava/lang/String;)Lorg/apache/geode/security/ResourcePermission$Target;` | vocabulary |
| `API-64b37aaf4fd5271f` | `ResourcePermission$Target` | `values()[Lorg/apache/geode/security/ResourcePermission$Target;` | vocabulary |
