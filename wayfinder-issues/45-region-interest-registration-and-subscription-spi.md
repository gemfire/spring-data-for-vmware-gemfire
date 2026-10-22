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
