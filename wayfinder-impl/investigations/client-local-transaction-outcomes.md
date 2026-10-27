# Client-local transaction outcomes

Task 64 investigation, 2026-10-27. User-run validation passed against the pinned native artifact: 16 tests, zero failures/errors/skips, plus transitional migration boundary verification.

## Scope finding

The existing `NativeTransactionTests` establishes notification silence for server-backed client caching-proxy transactions. That result cannot exclude `TransactionListener` outcomes or `TransactionEvent` from client scope: client-local application regions and empty transactions follow a different native dispatch path, now verified by real client tests. Retain the public outcome/event SPI as required-client. No server creation API is needed.

## Pinned source evidence

Target artifact: GemFire `10.4.0-build.3056.df98c835`, source commit `df98c8357afe85853d9a4776255d4f0e7cf064cb`. Source was inspected in `/Users/uk016635/projects/gemfire`. That checkout has a later HEAD; `git diff` from the pinned commit to the working files listed below was empty, confirming these inspected implementations match the pin.

Paths below are relative to `gemfire-core/src/main/java/org/apache/geode/` in that checkout.

| Source | Evidence |
| --- | --- |
| `internal/cache/TXStateProxyImpl.java`, `getRealDeal(KeyInfo, InternalRegion)` | A region with a server proxy selects `ClientTXStateStub`. A locally owned region selects `TXState`; a null region also selects `TXState` for an empty transaction. `isFireCallbacks()` delegates to the selected state. |
| `internal/cache/tx/ClientTXStateStub.java` and `internal/cache/TXStateStub.java` | The client stub inherits `isFireCallbacks() == false`. This explains the existing server-backed negative controls. |
| `internal/cache/TXState.java`, `isFireCallbacks()` | Locally hosted state enables callbacks unless the transaction event contains only internal-region events. |
| `internal/cache/TXEvent.java`, `hasOnlyInternalEvents()` | Empty transactions and transactions touching application regions return false, so they are eligible for callbacks. The event also exposes transaction identity, cache and events. |
| `internal/cache/TXManagerImpl.java`, `noteCommitSuccess`, `noteCommitFailure`, `noteRollbackSuccess` | Each dispatches its outcome when state enables callbacks and listeners exist. Nonfatal listener exceptions are logged/suppressed and dispatch continues. Thread association is cleared before outcome dispatch. |
| `internal/cache/TXEntryState.java`, `getEvent()` | Entry events expose the original value and transaction identity, with transaction entry state supplying the staged operation/value. |

## Added executable evidence

`NativeClientLocalTransactionTests` creates only a native `ClientCache` and SPI `LOCAL` regions. It exercises the existing driver adapters rather than manually invoking native listeners.

| Test case | Required observation |
| --- | --- |
| Local commit | One commit outcome, committed region value, transaction/member identity, borrowed cache/region access and entry payload. |
| Local rollback | One rollback outcome describing the discarded update; the region retains its original value. |
| Empty commit and rollback | Both callbacks arrive with the appropriate transaction IDs and empty event lists. |
| Local write conflict | A bounded worker write conflicts with the suspended transaction; commit reports `COMMIT_CONFLICT`, emits failed-commit payload and preserves the concurrent value. |
| Listener failure | A throwing listener does not undo the committed value or skip a later listener; cache closure closes the registered listener once. |

Callback assertions are captured and checked after dispatch because native code suppresses listener failures. Lazy event payloads are copied during the callback. Existing server-backed negative controls remain in `NativeTransactionTests`.

## Member-level disposition

All 11 frozen rows below are required-client. Their implementation status remains partial. Task 34 owns the remaining implementation/consumer obligations, with task 37 retaining native-free test/mock obligations.

| Native member | Frozen ID | Evidence |
| --- | --- | --- |
| `TransactionListener` type | `API-0bc29ec6357df198` | Registered listener receives actual local outcomes. |
| `afterCommit` | `API-5f7001ab50825eea` | Local and empty commits; throwing-listener isolation. |
| `afterFailedCommit` | `API-00b975c07847f048` | Real concurrent local write conflict. |
| `afterRollback` | `API-e85b516454b19cd1` | Local and empty rollbacks. |
| `close` | `API-e2460dda14fcde03` | Registration/replacement and cache-close cleanup. |
| `init` | `API-049eff05afa2bd79` | Existing native client initialization forwarding test; automatic initialization is not implied. |
| `initialize` | `API-74ef29157f0e9086` | Existing native client cache/properties forwarding test; automatic initialization is not implied. |
| `TransactionEvent` type | `API-eed21ca5e11d6e9b` | Real outcome payload adapted through SPI. |
| `getTransactionId` | `API-64c493d6caec9a22` | Callback ID matches the active transaction's captured identity. |
| `getCache` | `API-850ce7fa086f1301` | Borrowed client cache/region access; completed transaction has no thread association. |
| `getEvents` | `API-5471f618cc8585a8` | Original/staged values and entry IDs for local outcomes; empty lists for empty transactions. |

Scope totals are now 373 required-client / 625 excluded-server / 2,853 unresolved, preserving all 3,851 IDs. The SPI manager documentation distinguishes locally hosted and server-backed transaction state. No signatures or runtime implementation changed. Spring migration must preserve both dispatch paths; the mock currently rejects transactions and still needs native-free collaborators or tested transaction support for retained unit scenarios.

## Validation receipt

The user reported `BUILD SUCCESSFUL in 26s` for the command below. Local JUnit XML confirms five `NativeClientLocalTransactionTests` and eleven `NativeTransactionTests`, all passing with zero failures, errors or skips. Migration boundary success remains a transitional baseline result, not native-free compliance.

User-run command:

```sh
./gradlew --offline -DgemfireVersion=10.4.0-build.3056.df98c835 \
  :spring-data-gemfire-driver-10.4:integrationTest \
  --tests '*NativeClientLocalTransactionTests' \
  --tests '*NativeTransactionTests' \
  verifyMigrationBoundary
```

Task 64 and parent 59 remain open. This slice does not establish complete client scope or native-free compliance.
