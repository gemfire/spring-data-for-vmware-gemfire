Type: task
Status: resolved

## Question

[SPI versioning scheme](10-spi-versioning-scheme.md) versions `driver-spi` via independent SemVer and checks a Driver Impl's declared SemVer-range against it at bind time. That basis is rejected: `driver-spi`'s external release version number is not a meaningful compatibility unit, because it says nothing about which GemFire feature generation it was built to expose. The replacement under consideration is an **internal generation-number scheme with a range-intersection compatibility check**, and this ticket exists to research it properly and get a dedicated sign-off before it supersedes ticket 10 — rather than being settled as an ordinary grilling answer.

### Proposed model (starting point for research, not yet final)

- **`driver-spi` owns the generation numbering scheme.** A generation number is defined entirely by which entries exist in the capability enum as of that generation (per [Capability enum evolution and removal](08-capability-enum-evolution-and-removal.md)). A generation bump happens only when GemFire's feature surface actually changes — e.g. GemFire 10.1/10.2 map to generation 2; GemFire 10.3 adds capabilities and becomes generation 3.
- **`driver-spi` declares the range of generations it currently supports**, `[spiMin, spiMax]`. This range narrows as old generations' backing GemFire versions go EOL (per the support matrix, tickets 11/16) and widens as new generations ship.
- **Each `driver-impl` self-reports its own generation compatibility.** Open sub-question for research: should an Impl declare a single generation, or a range `[implMin, implMax]`?
- **Proposed bind-time check — range intersection**: bind succeeds iff `implMax >= spiMin && implMin <= spiMax`; the intersection of the two ranges is the actually-usable capability surface for that specific binding (not just "does it bind at all").

### Things this research must specifically resolve, not just describe

1. **Does the range-intersection model actually preserve ticket 08's "no forward compatibility" rule?** The working argument is: capabilities beyond `driver-spi`'s current `spiMax` are structurally uninvocable regardless of what an Impl's own `implMax` declares — a higher Impl ceiling is inert until `driver-spi` catches up. Confirm this holds up as an enforceable mechanism, not just a narrative — i.e. show exactly where in the bind/invocation path this is guaranteed, not merely assumed.
2. **Is a self-declared `implMin` trustworthy on its own, or does it need to be backed by something enforceable?** The working assumption is that an Impl declaring `implMin = N` is only a valid claim if that Impl actually passes conformance/integration tests for every generation from `implMin` up through whatever it claims (per [Testing strategy](14-testing-strategy.md)'s per-Driver-Impl-version integration test configurations) — not an assumption riding on GemFire's own backward compatibility. [Ticket 15](15-driver-impl-self-declared-gemfire-compatibility-mechanics.md)'s Part 1 survey already found real precedent for self-declared ranges silently rotting into false claims absent enforcement (IntelliJ's `since-build`/`until-build`, npm's `engines`/`peerDependencies`, VS Code's inconsistently-enforced `engines.vscode`). Research needs to determine whether/how this design avoids that failure mode — e.g. is there a way to make the declared floor auditable or CI-enforced rather than a bare self-reported claim?
3. **Correctness gap: does the capability model ever *modify* an existing capability's contract, or only add/remove?** Ticket 08 describes the capability enum as a superset that only grows (addition) and shrinks at EOL (removal gated by support), but does not explicitly rule out a later generation *changing the semantics* of a capability introduced in an earlier generation. The range-intersection model implicitly assumes an Impl with a wider `implMax` behaves identically, for the lower generations within its declared range, to an Impl whose range tops out there. If capability semantics can ever change (not just be added to) between generations, that assumption breaks. This research must determine whether ticket 08's model is purely additive/non-modifying, and if it is not, what the range-intersection design needs to account for.
4. **Real-world analogs for mutual range declaration + intersection**, as distinct from [ticket 15](15-driver-impl-self-declared-gemfire-compatibility-mechanics.md)'s survey (which covered one-sided "module self-checks against a host runtime" patterns like JDBC/OSGi/npm/IDE plugins). This ticket needs examples where **both sides declare a range and the usable capability set is the intersection of the two** — e.g. protocol version negotiation schemes such as Kafka's API version negotiation, TLS version negotiation, HTTP/2 settings negotiation, gRPC/protobuf wire-compatibility windows, database client/server protocol handshakes, or compiler/ISA target-generation feature levels. For each: how is the intersection computed, how is a party's declared floor validated (if at all), and what happens on a partial/degraded overlap versus a clean one.

### Explicitly out of scope for this ticket

- [Ticket 15](15-driver-impl-self-declared-gemfire-compatibility-mechanics.md)'s axis — Driver Impl vs. actual runtime/physical GemFire (jars/cluster) compatibility — is a separate, orthogonal concern and is not affected by whatever this ticket concludes. Do not conflate the two axes.
- Do not treat the `udo.kohlmeyer/unified_driver`/GUD branch as a source, precedent, or shape-to-mirror for this research, per the standing rule in [Prior art disposition](02-prior-art-disposition.md).

### Process for this ticket

Two phases, per the user's request that this go through research and then a dedicated sign-off rather than an ordinary grilling round:

1. **Research phase (this ticket, AFK)** — produce findings for the four items above.
2. **Sign-off phase (HITL)** — findings are presented back as a grilling round; the user decides the final shape of the generation/range model. Once decided, this ticket's `## Answer` is finalized, [ticket 10](10-spi-versioning-scheme.md) is amended to point here (mirroring how ticket 11 was amended by ticket 15/16), and `map.md` is updated.

**Process note (assumption, flag if wrong):** this ticket's own resolution is treated as an ordinary wayfinder ticket resolution (an `## Answer` the user confirms during the sign-off round) — it does not by itself trigger a new `rationale.md` §8 sign-off row. That mechanism, per [ticket 17](17-spec-review-sign-off-process.md), is reserved for the whole spec reaching implementation-ready, not for individual ticket decisions.

## Answer

_Research phase complete (below). Sign-off phase (design decisions) still open — see map.md._

### Research findings

Grounded in this ticket's referenced tickets (08, 10, 14, 15) plus 07, 11, 16, 03 for bind/invocation-path context.

**1. Does the range-intersection check structurally preserve "no forward compatibility"?**

Confirmed, but the guarantee lives at a different layer than the bind-time check itself:

- **Compile-time/vocabulary layer (the real ceiling).** The `Capability` enum (ticket 08) is closed, not extensible. A given `driver-spi` jar's enum physically contains only constants up to `spiMax`. Neither Spring Data GemFire nor a Driver Impl compiled against that jar can reference a capability beyond `spiMax` — there is no constant to name. This is an absence of vocabulary, not a skippable runtime check.
- **Bind-time layer (this ticket's proposed check)** — `implMax >= spiMin && implMin <= spiMax` — rejects wholly-disjoint Impls before binding. Note: the boolean test alone does not itself compute/thread an *effective* clamped range through to invocation; that's an implementation detail this ticket leaves open (see sign-off Q3 below). Even without it, layers 1 and 3 already make out-of-range invocation impossible.
- **Invocation-time layer (ticket 07's fail-fast)** — independent, per-capability, and can only ever be asked about capabilities that exist as enum constants — which, per layer 1, tops out at `spiMax` regardless of what any Impl declares.

Conclusion: "no forward compatibility" is enforced primarily by the closed-enum design (compile-time absence) and reinforced by invocation-time fail-fast; the range-intersection check is a valuable earlier, coarser rejection, not the load-bearing mechanism.

**2. Can a self-declared `implMin` be made auditable rather than a bare claim?**

None of ticket 15's three surveyed ecosystems (IntelliJ, npm, VS Code) actually solve this for their own declared ranges — IntelliJ's Plugin Verifier checks binary/API linkage but not behavior; npm/VS Code remain purely declarative. Stronger precedents exist elsewhere that solve exactly this failure mode: **CNCF Certified Kubernetes** (Sonobuoy conformance suite must be run and submitted, no test skipping) and **Android CTS** (must pass on the software that actually ships). The common thread: the claim is only as good as an *executed, tied-to-that-range* conformance run, not a trusted metadata field.

Applied here: ticket 14 already provides the mechanism — a Driver Impl's declared `[implMin, implMax]` should only be publishable if an `integrationTest` configuration exists and passes for every generation in that range. Open question this raises: should that gate be **continuous** (re-run whenever `driver-spi`/`driver-core` change, since they're the side most likely to invalidate an old claim) or **point-in-time** (checked once at Driver Impl release)? A one-time check has the same rot risk IntelliJ's `since-build` has.

**3. Does ticket 08 ever contemplate a generation *modifying* (not adding/removing) an existing capability's contract?**

No — ticket 08 is framed entirely in terms of cardinality ("superset," "no forward compatibility," "removed once unsupported") and is silent on whether a capability's *semantics* can change once introduced. This is a real gap: the range-intersection model implicitly assumes a wide-range Impl behaves identically, at the lower generations within its range, to an Impl whose range tops out there. If GemFire (or the SPI) ever redefines an existing capability's behavior in a later generation without minting a new constant, that assumption breaks and the model has no way to detect it — it operates purely on generation numbers, not per-capability contract versions. Ticket 08 does not currently state a "capabilities are immutable once introduced" invariant, though that would be a reasonable, currently-unstated convention (treat any semantic change as retire-old + add-new).

**4. Real-world dual-declared-range + intersection precedent**

- **Kafka `ApiVersionsRequest`/`ApiVersionsResponse` (KIP-35)**: broker and client each declare `[min,max]` per API key; client computes the intersection and uses the highest mutually-supported version; no overlap → client raises an exception (hard fail); neither side's range is externally validated. ([KIP-35](https://cwiki.apache.org/confluence/display/KAFKA/KIP-35+-+Retrieving+protocol+version))
- **MongoDB wire protocol `minWireVersion`/`maxWireVersion`** — the closest structural analog. Both client and server declare `[min,max]`; source (`wire_version.h`) states verbatim: "if two agents' ranges do not intersect, they should not be allowed to communicate. If two agents have at least one version in common they can communicate." Three explicit outcomes: full overlap (identical protocol, full features), partial overlap (lower side must "compensate for not being on its partner version" — i.e. clamp to the shared subset), no overlap (hard refusal). No external validation of either side's declared range. ([wire_version.h](https://github.com/mongodb/mongo/blob/master/src/mongo/db/wire_version.h))
- **TLS 1.3** (`supported_versions`) and **PostgreSQL** (`NegotiateProtocolVersion`) are weaker/less-symmetric analogs — noted for contrast, not used as primary precedent.

Pattern across the strongest examples: live, self-reported ranges on both sides, no external audit of either declared bound, boolean-overlap logic identical in shape to this ticket's proposal, and no ecosystem surveyed implements a soft/degraded connection when ranges are fully disjoint — always a hard fail on no overlap, consistent with this project's fail-fast philosophy.

### Sign-off decisions

Signed off by Udo Kohlmeyer, 2026-08-20. Final shape of the model:

1. **Driver Impl declares a range, `[implMin, implMax]`**, mirroring `driver-spi`'s own `[spiMin, spiMax]` — not a single generation. This is what lets a single Impl expose a span of generations (the scenario that motivated dropping SemVer in the first place), and matches the strongest real-world precedents found in research (Kafka `ApiVersions`, MongoDB wire protocol), where both sides declare ranges and negotiate the overlap symmetrically.
2. **Declared ranges are backed by continuous CI enforcement**, not a point-in-time check. Every currently-published Driver Impl's declared `[implMin, implMax]` must have a passing `integrationTest` configuration (per [Testing strategy](14-testing-strategy.md)) for every generation in that range, and this suite is re-run whenever `driver-spi`/`driver-core` change — not only at the Driver Impl's own release time — since `driver-spi`/`driver-core` are the side most likely to shift underneath an already-published Impl's claim. A point-in-time-only check would reproduce the rot failure mode found in IntelliJ's `since-build`/`until-build` (research finding 2).
3. **`driver-core` computes and exposes the effective intersected range** at bind time — `[max(implMin, spiMin), min(implMax, spiMax)]` — for diagnostics/introspection. Not load-bearing for correctness (the closed-enum + invocation-fail-fast layers already guarantee it, per research finding 1), but its absence would mean a fail-fast exception could only say "this capability isn't supported," not "here's the full set you actually have" — worse debuggability than this project's fail-fast-with-descriptive-errors philosophy calls for.
4. **Capability semantics are immutable once introduced** — formalized as an explicit amendment to [ticket 08](08-capability-enum-evolution-and-removal.md) rather than left as an undocumented assumption, since the range-intersection model's correctness depends on it.

### Consequences

- [Ticket 10](10-spi-versioning-scheme.md)'s SemVer-based answer is superseded by this ticket; ticket 10 is amended accordingly.
- [Ticket 08](08-capability-enum-evolution-and-removal.md) is amended with the capability-immutability invariant (decision 4 above).
- `map.md`'s "Not yet specified" Axis A entry moves to "Decisions so far."
