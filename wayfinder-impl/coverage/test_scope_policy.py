"""Scope gate regressions: run with python3 -m unittest discover -s this-directory."""

import copy
import json
import unittest

from scope_policy import classify
from validate_coverage import HERE, ROOT, check_scopes, read


class ScopePolicyTests(unittest.TestCase):
    def setUp(self):
        self.rows = read("api-coverage.tsv")
        policy = json.loads((HERE / "coverage-policy.json").read_text())
        self.policy = json.loads((HERE / policy["scope_policy"]).read_text())

    def test_saved_classification_matches_reviewed_policy(self):
        check_scopes(self.rows)

    def test_new_overload_of_excluded_receiver_requires_review(self):
        row = copy.deepcopy(next(r for r in self.rows if r["native_type"] == "org.apache.geode.cache.RegionShortcut"))
        row.update(id="API-new-overload", member="newMethod", descriptor="()V")
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
        next(r for r in self.rows if r["scope"] == "unresolved")["owner"] = retired
        with self.assertRaisesRegex(AssertionError, "active implementation and consumer owners"):
            classify(self.rows, self.policy, ROOT)

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


if __name__ == "__main__":
    unittest.main()
