"""Scope gate regressions: run with python3 -m unittest discover -s this-directory."""

import copy
import json
import unittest

from scope_policy import classify, classify_sources
from validate_coverage import HERE, ROOT, check_rows, check_scopes, read


class ScopePolicyTests(unittest.TestCase):
    def setUp(self):
        self.rows = read("api-coverage.tsv")
        policy = json.loads((HERE / "coverage-policy.json").read_text())
        self.policy = json.loads((HERE / policy["scope_policy"]).read_text())

    def test_saved_classification_matches_reviewed_policy(self):
        check_scopes(self.rows)

    def test_retired_excluded_mapping_can_be_empty(self):
        check_rows(self.rows)

    def test_retired_mapping_cannot_claim_retained_or_implemented_coverage(self):
        row = next(r for r in self.rows if r["scope"] == "excluded-server" and not r["native_impl"])
        for changes in ({"scope": "required-client"}, {"scope": "unresolved"},
                        {"status": "partial"}, {"status": "implemented"}, {"spi": "stale-mapping"}):
            with self.subTest(changes=changes):
                original = row.copy()
                row.update(changes)
                try:
                    with self.assertRaises(AssertionError):
                        check_rows(self.rows)
                finally:
                    row.update(original)

    def test_new_overload_of_excluded_receiver_requires_review(self):
        row = copy.deepcopy(next(r for r in self.rows if r["native_type"] == "org.apache.geode.cache.RegionShortcut"))
        pending = next(r for r in self.rows if r["scope"] == "required-client" and r["status"] == "missing")
        # A new discovery needs open owners; the excluded receiver's cleanup owner is resolved.
        row.update(id="API-new-overload", member="newMethod", descriptor="()V",
                   owner=pending["owner"], consumer_owner=pending["consumer_owner"])
        result = classify(self.rows + [row], self.policy, ROOT)[row["id"]]
        self.assertEqual("unresolved", result["scope"])
        self.assertEqual(self.policy["unresolved"]["owner"], result["scope_owner"])

    def test_unreviewed_rows_cannot_default_to_excluded(self):
        self.policy["unresolved"]["scope"] = "excluded-server"
        with self.assertRaisesRegex(AssertionError, "must remain unresolved"):
            classify(self.rows, self.policy, ROOT)

    def test_conflicting_decisions_are_rejected(self):
        duplicate = copy.deepcopy(self.policy["decisions"][0])
        duplicate["id"] = "conflicting-review"
        duplicate["scope"] = "excluded-server"
        self.policy["decisions"].append(duplicate)
        with self.assertRaisesRegex(AssertionError, "Conflicting scope"):
            classify(self.rows, self.policy, ROOT)

    def test_missing_native_row_cannot_silently_drop_a_review(self):
        self.policy["decisions"][0]["api_ids"].append("API-missing")
        with self.assertRaisesRegex(AssertionError, "Stale scope ID"):
            classify(self.rows, self.policy, ROOT)

    def test_missing_evidence_is_rejected(self):
        self.policy["decisions"][0]["evidence"] = "missing-scope-evidence.md"
        with self.assertRaisesRegex(AssertionError, "Missing scope evidence"):
            classify(self.rows, self.policy, ROOT)

    def test_exclusion_is_not_native_removal(self):
        next(r for r in self.rows if r["scope"] == "excluded-server")["status"] = "removed"
        with self.assertRaisesRegex(AssertionError, "not native removal"):
            classify(self.rows, self.policy, ROOT)

    def test_retained_api_cannot_keep_a_superseded_owner(self):
        retired = next(r["owner"] for r in self.rows if r["owner"].startswith("wayfinder-issues/42-"))
        row = next(r for r in self.rows if r["scope"] == "required-client")
        for field in ("owner", "consumer_owner"):
            with self.subTest(field=field):
                original = row[field]
                row[field] = retired
                try:
                    with self.assertRaisesRegex(AssertionError, "active (owner|implementation and consumer owners)"):
                        classify(self.rows, self.policy, ROOT)
                finally:
                    row[field] = original

    def test_generated_scope_cannot_override_reviewed_decision(self):
        next(r for r in self.rows if r["scope"] == "required-client")["scope"] = "excluded-server"
        with self.assertRaisesRegex(AssertionError, "differs from reviewed policy"):
            check_scopes(self.rows)

    def test_interest_policy_and_persistent_local_shortcuts_remain_client_requirements(self):
        result = classify(self.rows, self.policy, ROOT)
        protected = [r for r in self.rows if r["native_type"] in {
            "org.apache.geode.cache.InterestResultPolicy", "org.apache.geode.cache.client.ClientRegionShortcut"}]
        self.assertTrue(any("PERSISTENT" in r["member"] for r in protected))
        self.assertEqual({"required-client"}, {result[r["id"]]["scope"] for r in protected})

    def test_unfinished_api_cannot_keep_a_resolved_foundation_owner(self):
        row = next(r for r in self.rows if r["scope"] == "required-client" and r["status"] == "partial")
        row["owner"] = "wayfinder-issues/" + next((ROOT / "wayfinder-issues").glob("17-*.md")).name
        with self.assertRaisesRegex(AssertionError, "Unfinished API needs an open owner"):
            classify(self.rows, self.policy, ROOT)
        row["status"] = "implemented"
        classify(self.rows, self.policy, ROOT)

    def test_source_review_overrides_receiver_scope_without_changing_api_scope(self):
        sources = read("source-coverage.tsv")
        api_fields = classify(self.rows, self.policy, ROOT)
        result = classify_sources(sources, api_fields, self.policy, ROOT)
        row = next(r for r in sources if r["id"] == "REF-166bcb760c1b0a73")
        self.assertEqual("excluded-server", api_fields[row["api_row"]]["scope"])
        self.assertEqual("required-client", result[row["id"]]["scope"])

    def test_new_unlinked_source_requires_review(self):
        sources = read("source-coverage.tsv")
        sources.append(dict(id="REF-new", api_row="", text="new reference", owner=self.policy["unresolved"]["owner"]))
        result = classify_sources(sources, classify(self.rows, self.policy, ROOT), self.policy, ROOT)
        self.assertEqual("unresolved", result["REF-new"]["scope"])

    def test_source_review_rejects_stale_conflicting_or_changed_evidence(self):
        sources = read("source-coverage.tsv")
        api_fields = classify(self.rows, self.policy, ROOT)
        for mutation, expected in (("stale", "Stale source ID"), ("conflict", "Conflicting source decisions"),
                                   ("changed", "Reviewed source changed"), ("missing", "Missing source evidence"),
                                   ("duplicate", "Duplicate source decision"), ("owner", "open owner")):
            with self.subTest(mutation=mutation):
                policy = copy.deepcopy(self.policy)
                decision = policy["source_decisions"][0]
                if mutation == "stale":
                    decision["references"]["REF-missing"] = "missing"
                elif mutation == "conflict":
                    duplicate = copy.deepcopy(decision)
                    duplicate["id"] = "conflicting-source-review"
                    policy["source_decisions"].append(duplicate)
                elif mutation == "changed":
                    decision["references"][next(iter(decision["references"]))] = "changed"
                elif mutation == "missing":
                    decision["evidence"] = "missing-source-evidence.md"
                elif mutation == "duplicate":
                    policy["source_decisions"].append(copy.deepcopy(decision))
                else:
                    decision["owner"] = "wayfinder-issues/" + next((ROOT / "wayfinder-issues").glob("17-*.md")).name
                with self.assertRaisesRegex(AssertionError, expected):
                    classify_sources(sources, api_fields, policy, ROOT)


if __name__ == "__main__":
    unittest.main()
