---
mode: agent
tools: [ 'github', 'atlassian' ]
description: 'Research a Jira story and generate an implementation plan'
---

# Plan Story

Triggered by: `/plan-story` or "plan story <JIRA-KEY>"

```
JIRA_KEY: {{input:jira_key:Jira issue key (e.g. DGAP-42)}}
```

---

## Steps

### 1. Create Branch

Create a branch named `{{JIRA_KEY}}_<story-description>` where
`story-description` is a short slug derived from the story summary.

Then check out that branch locally so all subsequent work is on it.

### 2. Load the Jira Story

Fetch the issue. You only need:

- `summary`
- `description`
- `acceptance_criteria`

Stop and report if the issue is not found.
Self-assign the issue if unassigned.
Move story into "In Progress" if not already.

### 3. Search Confluence

Search for pages related to the story. Prioritize architecture, design, and
ADR pages. Discard pages last modified more than a year ago unless nothing
else exists. Use this context to inform the plan — do not repeat it verbatim.

### 4. Gap Analysis — One Question Only

If context is missing, and it would block writing the plan, ask the developer
**one** consolidated message covering all gaps. Accept "TBD" as a valid answer.

If gaps are minor, or you can make a reasonable call, skip this step entirely.

### 5. Write the Plan

Produce a Markdown file at:

```
docs/implementation-plans/{{JIRA_KEY}}-plan.md
```

**Sections (keep each section brief):**

1. **Acceptance Criteria** — table with ID and criterion; derive from the story
   description if not explicitly listed.
2. **Implementation Steps** — TDD order: test step then implement step for each
   component. For each step name the file, class, and method/test-case names.
   Use a table for test cases when there are multiple (columns: test name,
   inputs, expected outcome).
3. **Risks** — bullet list only; include a one-line mitigation for each.

**Do not include:** summaries, architecture context, testing strategy tables,
references, or any other prose sections. The plan must be scannable in under
30 seconds.

### 6. Evaluate (up to 4 passes)

Check these criteria. Improve the weakest area each pass until score ≥ 8.5/10
or 4 passes are done.

| Criterion     | Weight | Pass condition                                   |
|---------------|--------|--------------------------------------------------|
| Completeness  | 30%    | No empty sections or placeholders                |
| Alignment     | 25%    | Every AC maps to ≥ 1 implementation step         |
| Specificity   | 25%    | Real file, class, and test-case names throughout |
| Risk coverage | 20%    | Every meaningful unknown has a mitigation        |

If score < 8.5 after 4 passes, prepend:
> ⚠️ Confidence: MEDIUM — manual review recommended before starting.

### 7. Save & Report

1. FIRST, ask the developer to review the plan. ONLY AFTER APPROVAL, continue.
2. The plan file is gitignored — do not commit it. It lives locally only.
3. Post a comment to `{{JIRA_KEY}}` in Jira with: branch name, implementation
   steps summary, and key risks.
4. Tell the developer the branch name, file path, and suggested next step:
   > "To write tests before coding, run `/write-tests`. To go straight to
   > implementation, run `/execute-plan`."

**Documentation note:** If the story produces user-visible reference documentation
(property tables, API guides, ADRs), the plan's documentation step should specify
creating it in **both** `docs/` (versioned with code) and Confluence (`DGAP` space,
via the Atlassian MCP tool). Do not put it only in `docs/`.

---

## Rules

- Ask the developer **at most once** (Step 4).
- Never commit to `main`/`master`.
- Never write implementation code.
- If re-run for the same key, overwrite — do not duplicate.
