Type: grilling
Status: resolved

## Question

`rationale.md` §7–§8 already has a sign-off mechanism (a dated table) and one existing entry (Udo Kohlmeyer, 2026-08-19, architectural direction §2–§4 only, explicitly excluding §6 open items). What's still undecided is how that mechanism operates going forward, across three sub-questions:

1. **Cadence** — does a fresh §8 sign-off entry get requested once the whole spec reaches "implementation-ready" (all §6 items resolved, docs batch-updated), or after every individual item resolves?
2. **Approver scope** — is sign-off solely Udo Kohlmeyer's call, or does the process need to name other reviewers/stakeholders before implementation can start?
3. **Post-implementation-start revisions** — once implementation begins, if the spec needs to change (a design gap surfaces mid-migration), does that require a new §8 sign-off row before the change is acted on, or can it happen inline as normal implementation work?

## Answer

1. **Cadence — once, at implementation-ready.** A fresh §8 sign-off entry is requested once, when all §6 open items are resolved and the spec/rationale docs are batch-updated to reflect them — not after every individual item.
2. **Approver scope — solely Udo Kohlmeyer.** Matches the existing single-row §8 log; no multi-approver process is introduced.
3. **Post-implementation-start revisions — case-by-case, not a blanket policy.** A significant change — one that breaks or reverses already-completed decisions, or changes the overall approach — requires a new round of sign-off before being acted on. Smaller changes (clarifications, local corrections that don't unwind prior decisions) can be handled inline as part of normal implementation work, with git history on the spec files serving as the audit trail for those.
