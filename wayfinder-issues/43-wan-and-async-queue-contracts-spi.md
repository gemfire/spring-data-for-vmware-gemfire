Type: task
Status: resolved
Resolution: superseded by client-only scope; not implemented

# WAN and asynchronous queue contracts and callbacks SPI

## Answer — 2026-10-22

The user explicitly requested a client-only backlog and removal of existing server-side SPI work. This server-hosting requirement is retired by the [approved scope correction](../wayfinder-impl/client-only-scope.md). Native Cache reachability alone does not make server hosting or WAN/queue administration a required client capability.

[Remove server-only SPI and reconcile client scope](59-remove-server-only-spi-and-reconcile-client-scope.md) owns removal of existing code and coverage reclassification. Existing WAN-specific callback payloads and settings are cleanup input; no WAN delivery implementation is required.

This resolution means superseded, not delivered or behavior-validated. Client operations against servers, client subscriptions and private native server fixtures remain in scope. No active task may depend on delivery of this former server contract.
