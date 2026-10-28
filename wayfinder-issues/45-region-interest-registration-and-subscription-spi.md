Type: task
Status: open
Blocked by: 18, 19, 20, 59

# Region interest registration and subscription SPI

## Client-only scope — 2026-10-22

Apply the [approved client-only boundary](../wayfinder-impl/client-only-scope.md). [Cleanup ticket 59](59-remove-server-only-spi-and-reconcile-client-scope.md) must complete first. Full/complete coverage in this ticket means every retained client requirement; historical native reachability alone does not require peer/server APIs. Preserve all retained client tests and native-free boundaries.

## Question

Complete application Region interest registration and list inspection, including inherited convenience methods. This is the subscription portion split from [Complete region operations and metadata SPI](18-complete-region-operations-and-metadata-spi.md), not a scope exclusion.

## Required implementation

- Implement every registerInterest, registerInterestRegex, registerInterestForAllKeys and registerInterestForKeys overload, unregister variants, getInterestList and getInterestListRegex.
- Use typed InterestResultPolicy from region configuration; preserve default policy, durable and receive-values flags, iterable handling, key/regex identity and unsupported local/proxy combinations.
- Delegate to native overloads and preserve pool subscription settings and durable-client lifecycle; reject remote subscription behavior explicitly in the local mock.
- Server InterestRegistrationListener/InterestRegistrationEvent APIs are excluded and removed by ticket 59. Preserve client interest registration and delivery; CQ remains with ticket 22. Native server fixtures stay private to driver integration tests.

## Required validation

Real separate-server/client fixtures for policy NONE/KEYS/KEYS_VALUES, invalidation versus value delivery, durable registration, key/list/iterable/regex/all-keys overloads, unregister and list inspection. Tests must observe delivery and absence after unregistration, not just successful method calls.

## Completion

Complete assigned subscription rows and preserve all consumer migration obligations. This ticket blocks cache assembly and continuous-query SPI. Record version, commands, behavior evidence and limitations before resolving.

## Task 66 scope handoff — 2026-10-28

All 24 assigned region interest APIs are required-client; see the [source-backed ledger](../wayfinder-impl/investigations/client-region-and-callback-scope.md#client-region-interest). Preserve native defaults, policy/durable/receive-values flags, subscription validation, key/regex list inspection and unregister behavior. Region.registerInterestForKeys iterates registration per key: preserve order and partial progress on failure, rather than substituting one atomic list request. All-keys delegates to regex `.*`; legacy List/ALL_KEYS overload paths remain separately observable. Tasks 30/31 own consumers, 20/48 own pool and policy collaborators. Existing real-server delivery acceptance remains required; this audit adds no runtime probe.

## Task 75 handoff — 2026-10-28

Task 75 retains subscription-policy values in 48/30; see the [client subscription evidence](../wayfinder-impl/investigations/client-partition-membership-and-subscription-scope.md#client-subscription-policy-values). Distinguish default SubscriptionAttributes (CACHE_CONTENT), client-factory policy (ALL), InterestResultPolicy initial results and explicit interest registration/pool subscription enablement. Test real delivery only through supported registration and connection paths; policy readback alone establishes no delivery guarantee. No additional dependency or assigned ID is introduced.
