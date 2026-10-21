Type: task
Status: open
Blocked by: 18, 19, 20

# Region interest registration and subscription SPI

## Question

Complete application Region interest registration and list inspection, including inherited convenience methods. This is the subscription portion split from [Complete region operations and metadata SPI](18-complete-region-operations-and-metadata-spi.md), not a scope exclusion.

## Required implementation

- Implement every registerInterest, registerInterestRegex, registerInterestForAllKeys and registerInterestForKeys overload, unregister variants, getInterestList and getInterestListRegex.
- Use typed InterestResultPolicy from region configuration; preserve default policy, durable and receive-values flags, iterable handling, key/regex identity and unsupported local/proxy combinations.
- Delegate to native overloads and preserve pool subscription settings and durable-client lifecycle; reject remote subscription behavior explicitly in the local mock.
- Keep server InterestRegistrationListener/InterestRegistrationEvent work with cache-server SPI and CQ contracts with their own owner; test cross-boundary interaction where required.

## Required validation

Real separate-server/client fixtures for policy NONE/KEYS/KEYS_VALUES, invalidation versus value delivery, durable registration, key/list/iterable/regex/all-keys overloads, unregister and list inspection. Tests must observe delivery and absence after unregistration, not just successful method calls.

## Completion

Complete assigned subscription rows and preserve all consumer migration obligations. This ticket blocks cache assembly and continuous-query SPI. Record version, commands, behavior evidence and limitations before resolving.
