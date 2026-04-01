---
mode: agent
tools: [ 'github', 'atlassian' ]
description: 'Update an implementation plan after a decision or requirement change and post a Plan Corrections comment to Jira'
---

# Update Plan

Triggered by: `/update-plan` or "update plan <JIRA-KEY>"

Use this whenever a decision, requirement change, or correction affects an implementation plan that has already been posted.

```
JIRA_KEY: {{input:jira_key:Jira issue key (e.g. SLIM-42)}}
```

---

## Steps

### 1. Load the Current Plan

Read `docs/implementation-plans/{{JIRA_KEY}}-plan.md`.

- If the file does not exist, stop:
  > "No plan found at `docs/implementation-plans/{{JIRA_KEY}}-plan.md`. Nothing to update."

### 2. Identify What Changed

Clearly document:

- **What changed** — the specific section(s) and content affected
- **Why it changed** — decision, requirement change, or correction
- **Impact** — which implementation steps are added, removed, or modified

### 3. Ask for Permission

Before touching anything, ask the developer:

> "I'm about to make the following changes to the plan for `{{JIRA_KEY}}` and post a Plan Corrections comment to Jira:
>
> [bulleted list of changes]
>
> Shall I proceed?"

Do not proceed until the developer confirms.

### 4. Update the Plan File

Apply the changes to `docs/implementation-plans/{{JIRA_KEY}}-plan.md`.

- The plan file is gitignored — do not commit it.
- If the changes are significant enough to warrant re-review, change the header to:
  ```
  Status: UPDATED — REVIEW REQUIRED
  ```
- Otherwise, keep `Status: REVIEWED`.

### 5. Post a Plan Corrections Comment to Jira

Add a comment to `{{JIRA_KEY}}` with the heading `## Plan Corrections — {{JIRA_KEY}}` that states:

- What changed and in which section
- Why it changed
- The net effect on implementation steps (added / removed / modified)

### 6. Check Whether Confluence Needs Updating

Search the `SLIM` Confluence space for pages that reference `{{JIRA_KEY}}` or cover the same subject area as the changed sections (e.g. Architecture, API reference, Research, POC findings, Setup & dev environment).

For each relevant page found, decide:

- **Update needed** — the page contains information that is now inaccurate or incomplete due to the plan change.
- **No update needed** — the page is unaffected.

If one or more pages need updating:

1. Present the developer with a summary:

   > "The following Confluence page(s) may need updating based on this plan change:
   >
   > - **[Page title]** — [one sentence explaining what is outdated and what should change]
   > - ...
   >
   > Shall I create draft update(s) for your review?"

2. Do not create any drafts until the developer confirms.

3. For each approved page, create a draft version in Confluence that:
   - Applies only the necessary changes — do not rewrite unaffected content.
   - Adds a clearly visible notice at the top of the draft body:
     ```
     ⚠️ DRAFT — Pending developer review and approval before publishing.
     ```
   - Leaves the published page unchanged — the draft must be approved by the developer before it replaces the live content.

4. Report the draft URL(s) to the developer so they can review and publish when ready.

If no pages need updating, state that explicitly so the developer has a clear record.

### 7. Report to the Developer

Tell the developer:

- What was updated in the plan file
- That the Jira comment was posted
- The outcome of the Confluence check (pages updated as drafts, or no updates needed)
- Whether re-review of the plan is recommended before continuing execution

---

## Rules

- Never update the plan or post to Jira without explicit developer permission (Step 3).
- Never create Confluence drafts without explicit developer permission (Step 6).
- Never commit the plan file — it is gitignored.
- Keep corrections factual and concise — do not rewrite unaffected sections.

