# Client security and authentication scope — 2026-10-28

[Task 70](../../wayfinder-issues/70-audit-security-and-authentication-client-scope.md) settles **104 assigned IDs across ten native types: 28 required-client / 76 excluded-server / zero unresolved**. This closes classification only; all 104 implementation statuses remain missing.

Native evidence: GemFire **10.4.0-build.3056.df98c835**, source commit `df98c8357afe85853d9a4776255d4f0e7cf064cb` in `/Users/uk016635/projects/gemfire`, inspected with git show. Native paths below are relative to `gemfire-core/src/main/java/org/apache/geode/`; Spring paths are under `org.springframework.data.gemfire`. Each batch applies only to its exact IDs below. Source resolves the scope questions; no new runtime probe, Java/build change or Gradle run is needed.

## client-credential-callback

**10 IDs — required-client.** `security/AuthInitialize` extends `cache/CacheCallback` and `cache/Declarable`. Its type, two credential-key constants, three init overloads, two getCredentials overloads, inherited initialize and close account for all ten rows. `internal/cache/tier/sockets/Handshake.getCredentials` supplies the client auth-init setting and isPeer=false. The static helper instantiates the callback, invokes init(loggers), obtains credentials and closes in a finally block around credential acquisition. Initialization happens before that try: do not promise close after initialization failure. Blank auth-init uses configured credentials. GemFireSecurityException passes through the native helper; other exceptions become AuthenticationRequiredException, then require the agreed SPI translation.

Preserve input property mutation and returned credential selection; do not log secrets or replace the callback with a cached username/password pair. Use native-free endpoint/initialization context and keep native LogWriter, DistributedMember and Cache behind driver adapters. Inherited Declarable init(Properties)/initialize(Cache,Properties) are configuration lifecycle, not permission to host a peer; Declarable documents XML invocation and does not promise the same invocation for Java-only configuration.

Two deprecated conveniences need explicit treatment: init() is a no-op never called by the product; getCredentials(Properties) is also never called by the product and delegates with null member and isPeer=true. Retain native-free client initialization/credential convenience behavior, but do not claim that this legacy default is the actual client handshake or add a peer-mode switch to the SPI. Task 24 must document the client-only replacement rather than copying this obsolete peer flag. The versioned native adapter must use the real client callback path with false.

Spring `config/annotation/support/AbstractAuthInitialize` finalizes logger initialization and full credential collection through doInit/doGetCredentials. `AutoConfiguredAuthenticationInitializer` explicitly initializes with null cache and mutates the supplied properties when authentication is requested. `AutoConfiguredAuthenticationConfiguration` and `EnableSecurity` retain client credential wiring. Task 24 owns typed behavior and native/mock acceptance; task 35 owns these consumers. Mock can test local callback orchestration, not claim remote credential validation.

## client-credential-keys

**3 IDs — required-client.** `security/SecurityManager.USER_NAME`, PASSWORD and TOKEN are the strings security-username, security-password and security-token. Its authenticate documentation expressly identifies AuthInitialize output as incoming credentials and explains client-visible failure/expiry. These three values remain client configuration vocabulary even though their declaring interface hosts server authentication. Provide native-free keys/equivalents, not a public SecurityManager implementation. Token vocabulary alone is not proof of token support on every version/transport; task 24 must validate retained mechanisms per version. Permission values already classified under task 64 and shared exceptions assigned to task 76 are outside this fixed set.

## client-jndi-configuration

**11 IDs — required-client.** `internal/datasource/ConfigProperty` has a no-arg constructor, a (name,value,type) constructor and six mutable String accessors. Values default to null; the bean itself does not validate. `internal/jndi/JNDIInvoker.mapDatasource(Map,List)` creates/binds configured data sources, registers an optional JDBC driver and selects the requested datasource kind. validateAndBind obtains/closes a validation connection; SQLException closes the datasource and becomes DataSourceCreateException, otherwise the context receives java:/ plus the JNDI name. Managed datasource handling uses its own wrapper/factory path. Preserve observable configuration/binding failures through typed translation; native static context/registry ownership must stay private.

This is an actual client configuration contract: `client/ClientCacheFactoryBean.postProcess` registers JndiDataSource definitions, validates/normalizes datasource type and calls mapDatasource. Its public nested JndiDataSource exposes List<ConfigProperty>. `config/xml/ClientCacheParser` parses jndi-binding attributes and config-property elements, defaults a blank type to java.lang.String and constructs properties in name,value,type order. Retain this client XML behavior and replace native generic payloads; do not delete the parser or treat the properties as opaque native objects. Task 24 owns the typed binding/configuration capability and task 35 adoption. Mock may model configuration and controlled binding outcomes, not manufacture working JDBC/XA resources or distributed transactions.

## client-tls-channel-values

**4 IDs — required-client.** `security/SecurableCommunicationChannels` supplies the type plus ALL, LOCATOR and SERVER values in this batch. `internal/net/SSLConfigurationFactory.determineIfSSLEnabled` treats ALL as enabled for a requested component. `cache/client/internal/AutoConnectionSourceImpl` requests LOCATOR transport and `ConnectionFactoryImpl` requests SERVER transport. Thus locator/server and the native all token affect real client connections. Keep ALL interpretation within the versioned configuration adapter as needed; it is not a request to enable peer services or to add an ALL enum member to Spring.

Spring `config/annotation/EnableSsl.Component` already restricts public selection to LOCATOR and SERVER; `SslConfiguration` maps those channels and certificate aliases, with empty selection meaning both client channels. Preserve this restricted surface and rejected invalid values. Task 24 owns typed TLS configuration values and driver translation, task 35 annotation/property adoption. Real TLS compatibility remains versioned acceptance, not proven by this source classification.

## server-authorization-operation-context

**55 IDs — excluded-server.** `security/AccessControl` is configured by security-client-accessor on servers and by the post-operation accessor for outgoing results/notifications. Its init overloads receive the authenticated principal, remote member and hosting cache; authorizeOperation consumes `cache/operations/OperationContext`. That context and nested OperationCode describe server authorization decisions, including pre/post-operation and isClientUpdate classifiers. An operation labelled GET or a notification to a client does not make the hosting authorization hook a client API. All eight AccessControl rows and all 47 context/code rows are excluded, including inherited methods and every constant/conversion/classifier. Deprecation is not the reason for exclusion. Client cache listeners, CQ results and received authorization failures remain retained in their own families.

## server-authenticator

**7 IDs — excluded-server.** `security/Authenticator` describes security-client-authenticator and security-peer-authenticator credential validation. authenticate returns a principal after checking the supplied credentials; the convenience overload delegates with null member, and init(Properties) delegates to the logger-bearing initialization. `internal/cache/tier/sockets/Handshake.verifyCredentials` and its ServerConnection/CacheClientNotifier callers establish the accepting-side role. The name client-authenticator means authenticating an incoming client on the server. All seven rows are excluded, including inherited lifecycle; client AuthInitialize and received failures remain required.

## server-security-manager

**5 IDs — excluded-server.** `security/SecurityManager` implements authentication/authorization policy guarding client/server, JMX and gfsh operations on the accepting member. Its type and init/authenticate/authorize/close are five server-hosting rows; the three credential-key fields are retained separately above. Client applications may receive AuthenticationFailedException, AuthenticationExpiredException and authorization failures without implementing this server callback. Do not add a mock security policy engine or public server manager to satisfy client behavior. Private secured native integration fixtures may implement the native interface.

## server-result-postprocessor

**4 IDs — excluded-server.** `security/PostProcessor` documents server-side post-processing for values returned to the requesting principal. Its type, init, processRegionValue and close are four excluded rows. This is separate from Spring bean post-processors and client listeners. Retained client tests must still observe server-filtered results and failure semantics, using private native fixture policy where needed; no public server PostProcessor SPI is required.

## server-tls-channel-values

**5 IDs — excluded-server.** The five remaining fields in `security/SecurableCommunicationChannels` identify peer cluster, WAN gateway, gRPC, JMX and web endpoints. `internal/net/SSLConfigurationFactory.createSSLConfigForComponent` selects corresponding component configuration/aliases, while the retained GemFire client connection paths request LOCATOR and SERVER. Under the approved client-library boundary, exposing server service/admin channel selection is outside scope. Native ALL interpretation remains available for the retained client transports; it does not restore these five public selectors. Existing Spring EnableSsl.Component already excludes them.

## Ownership and reconciliation

All 28 retained rows have active implementation owner 24 and consumer owner 35. The 76 exclusions have missing implementation status and no SPI/native/mock/test implementation mappings to retire; their historical implementation owners stay intact, while this audit owns the scope decisions. No excluded production/SPI type or selector reference remains. Eight linked production references become required-client: ConfigProperty in ClientCacheFactoryBean and ClientCacheParser, JNDIInvoker in ClientCacheFactoryBean, AuthInitialize in four authentication configuration/support classes, and SecurableCommunicationChannels in EnableSsl. Existing SPI scope stays unchanged.

The remaining Spring test TestSecurityManager is **server integration infrastructure**, loaded through TEST_JAR_PATH by AutoConfiguredAuthenticationConfigurationIntegrationTests. It is not compliant final fixture placement: [task 77](../../wayfinder-issues/77-retire-legacy-server-test-jar-consumers.md) already owns moving that implementation into private driver infrastructure and separating native-free credential/protocol data. Its live authentication assertions must survive. This audit records that existing fixed assignment without taking it over or claiming its cleanup complete. The separate Spring Test module retains its documented native dependency exception. Tasks 59/64 cannot close before task 77.

Authenticated-view requirements settled in other families, permission values already settled by task 64 and shared failure declarations assigned to task 76 are unchanged. Implementation acceptance still requires typed contracts, official mock behavior and per-version secured/TLS/binding tests; source scope classification does not complete any of those.

## Exact ID ledger

Every row inherits its batch rationale and source evidence. Historical implementation owner is **24**, consumer owner **35**, throughout; exclusions create no implementation obligation. Native receivers, members and descriptors remain unchanged.

### client-credential-callback — 10 IDs

| API ID | Native receiver | Member | JVM descriptor |
| --- | --- | --- | --- |
| `API-d463bde89b0473a6` | `org.apache.geode.security.AuthInitialize` | `<type>` | `(type)` |
| `API-15968be5a2f389a1` | `org.apache.geode.security.AuthInitialize` | `SECURITY_PASSWORD` | `Ljava/lang/String;` |
| `API-7fa6440cd2984cdb` | `org.apache.geode.security.AuthInitialize` | `SECURITY_USERNAME` | `Ljava/lang/String;` |
| `API-fefbcf8c092270c1` | `org.apache.geode.security.AuthInitialize` | `close` | `()V` |
| `API-755d03ee04bfa186` | `org.apache.geode.security.AuthInitialize` | `getCredentials` | `(Ljava/util/Properties;)Ljava/util/Properties;` |
| `API-822a28d6217db331` | `org.apache.geode.security.AuthInitialize` | `getCredentials` | `(Ljava/util/Properties;Lorg/apache/geode/distributed/DistributedMember;Z)Ljava/util/Properties;` |
| `API-f51504226a2e3c0a` | `org.apache.geode.security.AuthInitialize` | `init` | `()V` |
| `API-c355a5e9fc0a58b3` | `org.apache.geode.security.AuthInitialize` | `init` | `(Ljava/util/Properties;)V` |
| `API-1f7f64c6866cbd38` | `org.apache.geode.security.AuthInitialize` | `init` | `(Lorg/apache/geode/LogWriter;Lorg/apache/geode/LogWriter;)V` |
| `API-498dbfc5502dffcd` | `org.apache.geode.security.AuthInitialize` | `initialize` | `(Lorg/apache/geode/cache/Cache;Ljava/util/Properties;)V` |

### client-credential-keys — 3 IDs

| API ID | Native receiver | Member | JVM descriptor |
| --- | --- | --- | --- |
| `API-64ed1ace11083fa6` | `org.apache.geode.security.SecurityManager` | `PASSWORD` | `Ljava/lang/String;` |
| `API-02a92ea9f1cade30` | `org.apache.geode.security.SecurityManager` | `TOKEN` | `Ljava/lang/String;` |
| `API-8003deff1c0a9f93` | `org.apache.geode.security.SecurityManager` | `USER_NAME` | `Ljava/lang/String;` |

### client-jndi-configuration — 11 IDs

| API ID | Native receiver | Member | JVM descriptor |
| --- | --- | --- | --- |
| `API-d95a0e9d1fb643ec` | `org.apache.geode.internal.datasource.ConfigProperty` | `<init>` | `()V` |
| `API-753a3e188f4caeac` | `org.apache.geode.internal.datasource.ConfigProperty` | `<init>` | `(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V` |
| `API-1a032e2bcafca86a` | `org.apache.geode.internal.datasource.ConfigProperty` | `<type>` | `(type)` |
| `API-0d87ed1eebd6330f` | `org.apache.geode.internal.datasource.ConfigProperty` | `getName` | `()Ljava/lang/String;` |
| `API-f9268b7a8b5f89a8` | `org.apache.geode.internal.datasource.ConfigProperty` | `getType` | `()Ljava/lang/String;` |
| `API-a8d056c91991f702` | `org.apache.geode.internal.datasource.ConfigProperty` | `getValue` | `()Ljava/lang/String;` |
| `API-e475762ab01be2da` | `org.apache.geode.internal.datasource.ConfigProperty` | `setName` | `(Ljava/lang/String;)V` |
| `API-5e0a82106a8f8864` | `org.apache.geode.internal.datasource.ConfigProperty` | `setType` | `(Ljava/lang/String;)V` |
| `API-a372081b0fe76636` | `org.apache.geode.internal.datasource.ConfigProperty` | `setValue` | `(Ljava/lang/String;)V` |
| `API-fcecc9830729a1f6` | `org.apache.geode.internal.jndi.JNDIInvoker` | `<type>` | `(type)` |
| `API-3bf422146335d3ee` | `org.apache.geode.internal.jndi.JNDIInvoker` | `mapDatasource` | `(Ljava/util/Map;Ljava/util/List;)V` |

### client-tls-channel-values — 4 IDs

| API ID | Native receiver | Member | JVM descriptor |
| --- | --- | --- | --- |
| `API-22328550a5b6fd0d` | `org.apache.geode.security.SecurableCommunicationChannels` | `<type>` | `(type)` |
| `API-718ef6047cc3475a` | `org.apache.geode.security.SecurableCommunicationChannels` | `ALL` | `Ljava/lang/String;` |
| `API-ae5dc06a08f24c70` | `org.apache.geode.security.SecurableCommunicationChannels` | `LOCATOR` | `Ljava/lang/String;` |
| `API-b97b894108070adb` | `org.apache.geode.security.SecurableCommunicationChannels` | `SERVER` | `Ljava/lang/String;` |

### server-authorization-operation-context — 55 IDs

| API ID | Native receiver | Member | JVM descriptor |
| --- | --- | --- | --- |
| `API-dc23850b853eb69d` | `org.apache.geode.cache.operations.OperationContext` | `<init>` | `()V` |
| `API-a2942148b9eb26a1` | `org.apache.geode.cache.operations.OperationContext` | `<type>` | `(type)` |
| `API-e5d87703aeeaf3ee` | `org.apache.geode.cache.operations.OperationContext` | `getOperationCode` | `()Lorg/apache/geode/cache/operations/OperationContext$OperationCode;` |
| `API-13a8bdcbab065ce3` | `org.apache.geode.cache.operations.OperationContext` | `isClientUpdate` | `()Z` |
| `API-7cc23aaa953fd936` | `org.apache.geode.cache.operations.OperationContext` | `isClientUpdate` | `(Lorg/apache/geode/cache/operations/OperationContext;)Z` |
| `API-8285f9168a4a9560` | `org.apache.geode.cache.operations.OperationContext` | `isPostOperation` | `()Z` |
| `API-2b516158116f97a3` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `<type>` | `(type)` |
| `API-3905ec42db4b6f98` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `CLOSE_CQ` | `Lorg/apache/geode/cache/operations/OperationContext$OperationCode;` |
| `API-d154e4d445b08192` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `CONTAINS_KEY` | `Lorg/apache/geode/cache/operations/OperationContext$OperationCode;` |
| `API-dc1748252ec7541d` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `DESTROY` | `Lorg/apache/geode/cache/operations/OperationContext$OperationCode;` |
| `API-e867060615f128b4` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `EXECUTE_CQ` | `Lorg/apache/geode/cache/operations/OperationContext$OperationCode;` |
| `API-632efa18eeb7c525` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `EXECUTE_FUNCTION` | `Lorg/apache/geode/cache/operations/OperationContext$OperationCode;` |
| `API-78486221e140cef3` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `GET` | `Lorg/apache/geode/cache/operations/OperationContext$OperationCode;` |
| `API-8169a71666969fe3` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `GET_DURABLE_CQS` | `Lorg/apache/geode/cache/operations/OperationContext$OperationCode;` |
| `API-922ccc92f524c6bc` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `INVALIDATE` | `Lorg/apache/geode/cache/operations/OperationContext$OperationCode;` |
| `API-c9124d1ecdc63e82` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `KEY_SET` | `Lorg/apache/geode/cache/operations/OperationContext$OperationCode;` |
| `API-3574546c00456e29` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `PUTALL` | `Lorg/apache/geode/cache/operations/OperationContext$OperationCode;` |
| `API-395044977ebbddc1` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `PUT` | `Lorg/apache/geode/cache/operations/OperationContext$OperationCode;` |
| `API-4910b82dea09c1a1` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `QUERY` | `Lorg/apache/geode/cache/operations/OperationContext$OperationCode;` |
| `API-fed6aed05d508319` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `REGION_CLEAR` | `Lorg/apache/geode/cache/operations/OperationContext$OperationCode;` |
| `API-dd36eff426058f3f` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `REGION_CREATE` | `Lorg/apache/geode/cache/operations/OperationContext$OperationCode;` |
| `API-eb7f0f4ecb07bfd3` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `REGION_DESTROY` | `Lorg/apache/geode/cache/operations/OperationContext$OperationCode;` |
| `API-06455b416d3d65cb` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `REGISTER_INTEREST` | `Lorg/apache/geode/cache/operations/OperationContext$OperationCode;` |
| `API-da81952df3096d0b` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `REMOVEALL` | `Lorg/apache/geode/cache/operations/OperationContext$OperationCode;` |
| `API-42c84173653261c7` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `STOP_CQ` | `Lorg/apache/geode/cache/operations/OperationContext$OperationCode;` |
| `API-f876a12999bd69ac` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `UNREGISTER_INTEREST` | `Lorg/apache/geode/cache/operations/OperationContext$OperationCode;` |
| `API-fd1afd1fb96453b7` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `isCloseCQ` | `()Z` |
| `API-6e6824448eeb076d` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `isContainsKey` | `()Z` |
| `API-ba8445cd38a54483` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `isDestroy` | `()Z` |
| `API-cac05e9ced360c20` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `isExecuteCQ` | `()Z` |
| `API-80b39ab6916f95cc` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `isExecuteRegionFunction` | `()Z` |
| `API-49e2773f2032c242` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `isGet` | `()Z` |
| `API-579c8b62c3d54261` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `isGetDurableCQs` | `()Z` |
| `API-684f96b9835dfd1b` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `isInvalidate` | `()Z` |
| `API-953259a66ce41ac5` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `isKeySet` | `()Z` |
| `API-37a60293ad4b5ce3` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `isPut` | `()Z` |
| `API-1ce316fd1f22cd3a` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `isPutAll` | `()Z` |
| `API-f621b07db85d1bf6` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `isQuery` | `()Z` |
| `API-52cef4eeeaef70f4` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `isRegionClear` | `()Z` |
| `API-9e210b766dc6caac` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `isRegionCreate` | `()Z` |
| `API-4cf40fb225e2f818` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `isRegionDestroy` | `()Z` |
| `API-cec3fefd2aef3f8e` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `isRegisterInterest` | `()Z` |
| `API-e6412f6805a300c1` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `isRemoveAll` | `()Z` |
| `API-8fceb7cb3cea711c` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `isStopCQ` | `()Z` |
| `API-deee8233b93cba94` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `isUnregisterInterest` | `()Z` |
| `API-522b0aeff0e7854d` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `valueOf` | `(Ljava/lang/String;)Lorg/apache/geode/cache/operations/OperationContext$OperationCode;` |
| `API-578ea483b5ae4722` | `org.apache.geode.cache.operations.OperationContext$OperationCode` | `values` | `()[Lorg/apache/geode/cache/operations/OperationContext$OperationCode;` |
| `API-d6941b513f818352` | `org.apache.geode.security.AccessControl` | `<type>` | `(type)` |
| `API-db737c59133496e4` | `org.apache.geode.security.AccessControl` | `authorizeOperation` | `(Ljava/lang/String;Lorg/apache/geode/cache/operations/OperationContext;)Z` |
| `API-9db3166bc7ef661b` | `org.apache.geode.security.AccessControl` | `close` | `()V` |
| `API-7f8cf09e23e2e1ed` | `org.apache.geode.security.AccessControl` | `init` | `(Ljava/security/Principal;)V` |
| `API-742da3374b7820a0` | `org.apache.geode.security.AccessControl` | `init` | `(Ljava/security/Principal;Lorg/apache/geode/distributed/DistributedMember;)V` |
| `API-adde0ce1736f60bd` | `org.apache.geode.security.AccessControl` | `init` | `(Ljava/security/Principal;Lorg/apache/geode/distributed/DistributedMember;Lorg/apache/geode/cache/Cache;)V` |
| `API-c29cf24ec59a8e52` | `org.apache.geode.security.AccessControl` | `init` | `(Ljava/util/Properties;)V` |
| `API-755298ff968d3271` | `org.apache.geode.security.AccessControl` | `initialize` | `(Lorg/apache/geode/cache/Cache;Ljava/util/Properties;)V` |

### server-authenticator — 7 IDs

| API ID | Native receiver | Member | JVM descriptor |
| --- | --- | --- | --- |
| `API-5607196af3018484` | `org.apache.geode.security.Authenticator` | `<type>` | `(type)` |
| `API-bf1bddd1c8838d78` | `org.apache.geode.security.Authenticator` | `authenticate` | `(Ljava/util/Properties;)Ljava/security/Principal;` |
| `API-8cedf273f75df38c` | `org.apache.geode.security.Authenticator` | `authenticate` | `(Ljava/util/Properties;Lorg/apache/geode/distributed/DistributedMember;)Ljava/security/Principal;` |
| `API-bafed4daa2850f5b` | `org.apache.geode.security.Authenticator` | `close` | `()V` |
| `API-601bf45aa56e6fb1` | `org.apache.geode.security.Authenticator` | `init` | `(Ljava/util/Properties;)V` |
| `API-7f2e24b76f0b4b35` | `org.apache.geode.security.Authenticator` | `init` | `(Ljava/util/Properties;Lorg/apache/geode/LogWriter;Lorg/apache/geode/LogWriter;)V` |
| `API-dc16fa9b68a5ca42` | `org.apache.geode.security.Authenticator` | `initialize` | `(Lorg/apache/geode/cache/Cache;Ljava/util/Properties;)V` |

### server-security-manager — 5 IDs

| API ID | Native receiver | Member | JVM descriptor |
| --- | --- | --- | --- |
| `API-225feb4abe685376` | `org.apache.geode.security.SecurityManager` | `<type>` | `(type)` |
| `API-271ab3d2575e5b49` | `org.apache.geode.security.SecurityManager` | `authenticate` | `(Ljava/util/Properties;)Ljava/lang/Object;` |
| `API-25da2aec56c00d00` | `org.apache.geode.security.SecurityManager` | `authorize` | `(Ljava/lang/Object;Lorg/apache/geode/security/ResourcePermission;)Z` |
| `API-ddbb2191b4db8940` | `org.apache.geode.security.SecurityManager` | `close` | `()V` |
| `API-16b5661ff0f15306` | `org.apache.geode.security.SecurityManager` | `init` | `(Ljava/util/Properties;)V` |

### server-result-postprocessor — 4 IDs

| API ID | Native receiver | Member | JVM descriptor |
| --- | --- | --- | --- |
| `API-093c2478fb27404c` | `org.apache.geode.security.PostProcessor` | `<type>` | `(type)` |
| `API-d39265117c00cc9b` | `org.apache.geode.security.PostProcessor` | `close` | `()V` |
| `API-ef7fec13a0ffc43c` | `org.apache.geode.security.PostProcessor` | `init` | `(Ljava/util/Properties;)V` |
| `API-2d2d3ce29bc2b676` | `org.apache.geode.security.PostProcessor` | `processRegionValue` | `(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;` |

### server-tls-channel-values — 5 IDs

| API ID | Native receiver | Member | JVM descriptor |
| --- | --- | --- | --- |
| `API-3e5a1f032f36e3a5` | `org.apache.geode.security.SecurableCommunicationChannels` | `CLUSTER` | `Ljava/lang/String;` |
| `API-d8346f50d9f03db4` | `org.apache.geode.security.SecurableCommunicationChannels` | `GATEWAY` | `Ljava/lang/String;` |
| `API-b491357ccb471cb1` | `org.apache.geode.security.SecurableCommunicationChannels` | `GRPC` | `Ljava/lang/String;` |
| `API-e4739935bc955e53` | `org.apache.geode.security.SecurableCommunicationChannels` | `JMX` | `Ljava/lang/String;` |
| `API-6449c8cbbb3d7eb5` | `org.apache.geode.security.SecurableCommunicationChannels` | `WEB` | `Ljava/lang/String;` |
