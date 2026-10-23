"""Reviewed scope is independent of native availability and implementation status."""

from collections import Counter
from pathlib import Path

SCOPES = ("required-client", "excluded-server", "unresolved")
SCOPE_COLUMNS = ("scope", "scope_decision", "scope_reason", "scope_evidence", "scope_owner")


def retired(owner):
    return Path(owner).name.split("-", 1)[0] in {"42", "43"}


def decision_fields(decision, row):
    return dict(scope=decision["scope"], scope_decision=decision["id"],
                scope_reason=decision["reason"], scope_evidence=decision["evidence"],
                scope_owner=decision.get("owner", row["owner"]))


def classify(rows, policy, root):
    """Match exact reviewed IDs; newly discovered rows always need review."""
    by_id = {r["id"]: r for r in rows}
    assert len(by_id) == len(rows), "Duplicate API IDs in scope input"
    decisions, reviewed = set(), {}
    default = policy["unresolved"]
    assert default["scope"] == "unresolved", "Unreviewed APIs must remain unresolved"
    assert not default.get("api_ids"), "The unresolved fallback cannot select reviewed IDs"
    for decision in [default] + policy["decisions"]:
        assert decision["id"] not in decisions, "Duplicate scope decision"
        decisions.add(decision["id"])
        assert decision["scope"] in SCOPES, decision["id"]
        assert decision["reason"].strip(), "Missing scope rationale"
        evidence = decision["evidence"].split("#", 1)[0]
        assert evidence and (root / evidence).is_file(), "Missing scope evidence"
        if decision is default:
            continue
        assert decision["api_ids"], "Empty reviewed scope decision"
        for api_id in decision["api_ids"]:
            assert api_id in by_id, f"Stale scope ID: {api_id}"
            assert api_id not in reviewed, f"Conflicting scope decisions: {api_id}"
            reviewed[api_id] = decision
    result = {}
    for api_id, row in by_id.items():
        fields = decision_fields(reviewed.get(api_id, default), row)
        owner = fields["scope_owner"]
        assert owner.startswith("wayfinder-issues/") and (root / owner).is_file(), owner
        assert not retired(owner), f"Scope review/removal needs an active owner: {api_id}"
        if fields["scope"] != "excluded-server":
            assert not retired(row["owner"]) and not retired(row["consumer_owner"]), (
                f"Retained/unresolved API needs active implementation and consumer owners: {api_id}")
        else:
            assert row["status"] != "removed", f"Scope exclusion is not native removal: {api_id}"
        result[api_id] = fields
    return result


def counts(rows):
    return {scope: sum(r["scope"] == scope for r in rows) for scope in SCOPES}


def status_counts(rows):
    return {scope: dict(sorted(Counter(r["status"] for r in rows if r["scope"] == scope).items()))
            for scope in SCOPES}
