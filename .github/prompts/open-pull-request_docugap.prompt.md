---
mode: agent
tools: [ 'github', 'atlassian' ]
description: 'Open a pull request for a completed feature branch'
---

# Open Pull Request

Triggered by: `/open-pull-request` or "open pull request for <JIRA-KEY>"

```
JIRA_KEY: {{input:jira_key:Jira issue key (e.g. DGAP-42)}}
```

---

## Steps

### 1. Identify the Ticket and Branch

- If the developer provided a ticket ID, use it.
- Otherwise, read the ticket ID from the current Git branch name (branch names follow the pattern
  `DGAP-####_short-description`).
- If you cannot determine the ticket ID, **stop and ask the developer**:
  > "Which ticket is this PR for? Please provide the ticket ID (e.g. DGAP-42)."
- Confirm the current branch is **not** `main`. If it is, stop:
  > "You are on `main`. Please switch to the feature branch before opening a PR."

### 2. Verify the Branch is Ready

Run the following checks in order. **Do not skip any step silently.** If a step cannot be completed, stop and tell the
developer exactly which step failed and why.

1. Run `./gradlew build`. If the build fails, stop and report all errors to the developer. Do not open a PR with a
   failing build.
2. Check for uncommitted changes using `git status`. If any exist, **and you have developer permission,** stage and
   commit them with an appropriate conventional commit message before continuing.

### 3. Load the Plan

Read `docs/implementation-plans/{{JIRA_KEY}}-plan.md` to retrieve:

- The story summary (used in the PR title)
- The Jira issue link (used in the PR description)
- The full plan content (embedded in the PR description)

If the plan file does not exist, ask the developer for the story summary and Jira URL before continuing.

### 4. Push the Branch

Push the current feature branch to `joellis13/docugap`.

### 5. Open a Pull Request

Open a pull request against `main` with the following structure:

**Title:** `{{JIRA_KEY}}: <Story Summary>`

**Description:**

```
## Description
<Brief summary of what was built and why, derived from the plan's Summary section.>

## Jira
<Full URL to the Jira issue, e.g. https://joellis13.atlassian.net/browse/DGAP-42>

## Change Log
<Bullet list of every meaningful change. Use `git log main..<branch> --oneline` to
enumerate commits as a starting point. Do not invent changes — derive from actual git history.>

## How to Test
<Step-by-step instructions a reviewer can follow to manually verify the feature works.
Derive these from the plan's Testing Strategy and Acceptance Criteria sections.>

## Verification Checklist
<Check off items that are confirmed complete. Leave unchecked anything that requires
the developer to verify locally.>

- [ ] Build passes (`./gradlew build`)
- [ ] All new tests pass
- [ ] Acceptance criteria verified manually
- [ ] No new warnings or suppressed errors introduced
- [ ] Swagger/OpenAPI docs updated (if applicable)

## Development Plan

<details>
<summary>Implementation Plan — {{JIRA_KEY}}</summary>

<paste full contents of docs/implementation-plans/{{JIRA_KEY}}-plan.md here>

</details>
```

### 6. Archive the Plan

#### 6a. Post the plan to Jira

File attachment is not available via the Atlassian MCP. Instead, post the full contents of
`docs/implementation-plans/{{JIRA_KEY}}-plan.md` as a Jira comment on the story, using this
format:

```
## Implementation Plan — {{JIRA_KEY}}

<full verbatim contents of the plan file>
```

#### 6b. Delete local plan files

Once the comment is confirmed posted, delete every file in `docs/implementation-plans/`:

```bash
rm -f docs/implementation-plans/*
```

If the directory is now empty, leave it in place (it is gitignored and will not appear in the PR).

### 7. Report to the Developer

- Share the PR URL.
- List any checklist items left unchecked and why.
- Remind the developer:
  > "Review the unchecked items locally, then mark the PR ready for review when done."

---

## Rules

- Never open a PR from `main` to `main`.
- Do not squash or rebase commits; leave history as-is.
- The change log must be derived from the actual git history — do not invent changes.
- If a merge conflict is encountered, **stop immediately**, notify the developer, and wait for instructions. Do not
  attempt to resolve conflicts autonomously.
- If the branch has no commits ahead of `main`, stop and tell the developer there is nothing to PR.
- If any required step fails (build, push, etc.), report the exact failure and wait for the developer before continuing.

