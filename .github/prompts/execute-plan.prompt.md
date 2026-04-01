---
mode: agent
tools: [ 'github', 'context7' ]
description: 'Execute a saved implementation plan for a Jira story'
---

# Execute Plan

Triggered by: `/execute-plan` or "execute plan <JIRA-KEY>"

```
JIRA_KEY: {{input:jira_key:Jira issue key (e.g. SLIM-42)}}
```

---

## Steps

### 1. Read the Plan

Read `docs/implementation-plans/{{JIRA_KEY}}-plan.md`.

- If the file does not exist, stop and tell the developer:
  > "No plan found at `docs/implementation-plans/{{JIRA_KEY}}-plan.md`. Please run `/plan-story` first."
- Confirm the plan's `Status` field is `REVIEWED`. If it is not, stop and tell the developer:
  > "The plan for `{{JIRA_KEY}}` has not been reviewed yet. Please review and approve the plan before executing it."
- Summarize the **Implementation Steps** from the plan and ask for confirmation before making any changes:
  > "I'm about to execute the following tasks: [numbered list]. Shall I proceed?"

Do not proceed until the developer confirms.

### 2. Create or Switch to the Feature Branch

- The branch should already exist from `/plan-story`. Switch to it.
- If the branch does not exist, create it from `main` using the naming pattern: `{{JIRA_KEY}}_<story-description>` where
  `story-description` is a short slug derived from the plan summary.
- Never execute on `main`.

### 3. Execute the TDD Loop

The plan's Implementation Steps are arranged in test/implement pairs. Work through each
pair in order, completing all four stages before advancing to the next pair.

#### Stage A — Confirm red state

Check whether the test file from the test step already exists on disk.

- **If it exists** (from a prior `/write-tests` run): run the test class:
  ```
  ./gradlew test --tests "fully.qualified.TestClassName"
  ```
  Every test must **fail**. If any test passes before the implementation step, flag it
  to the developer — it either tests nothing new or the behavior already exists — and
  wait for instruction before continuing.

- **If it does not exist** (`/write-tests` was skipped): write the tests now following
  the rules in `write-tests.prompt.md`, then run them and confirm every test fails.

#### Stage B — Implement

Work through the paired implementation step:

- Follow the patterns, file paths, and naming conventions in the plan.
- Adhere to the coding guidelines in `.github/copilot-instructions.md`:
    - SOLID and DRY principles
    - Descriptive names for variables, methods, and classes
    - Standard Spring Boot package structure: `controller`, `service`, `model`, `config`, `exception`
    - Constructor injection — never field injection (`@Autowired` on fields)
    - Lombok (`@Data`, `@Builder`, `@RequiredArgsConstructor`, etc.) where it reduces boilerplate
    - Do not write implementation code inside test files
- `TODO` comments that reference future work must cite a real Jira key (e.g. `// TODO SLIM-13: implement GET /devices`).
  Look up the backlog in Jira before using a placeholder.
- After each file change, run `./gradlew build` and fix all compile errors before continuing.

#### Stage C — Confirm green state

Run the same test class again:
```
./gradlew test --tests "fully.qualified.TestClassName"
```
Every test must **pass**. Fix all failures before advancing. Do not move to the next
pair while any test in this pair is still red.

#### Stage D — Commit the pair

With explicit developer permission, commit the test file and implementation file together
as one logical unit. Use a conventional commit message (the ticket ID is already in the
branch name — do not repeat it):
- `feat: <description>`
- `fix: <description>`
- `refactor: <description>`
- `test: <description>`

After committing, merge `main` into the feature branch to catch conflicts early. If a
conflict is encountered, **stop immediately**, notify the developer, and wait for
instructions before continuing.

Repeat Stages A–D for each remaining pair in the plan.

### 4. Final Build Check

After all tasks are complete, run:

```
./gradlew build
```

- This validates compilation, runs all tests, and catches any integration issues.
- Fix all failures before proceeding. Do not move on while the build is red.

### 5. Evaluate Code Quality (up to 3 passes)

Score the implementation before committing. Improve the weakest area each pass
until score ≥ 8.5/10 or 3 passes are done — whichever comes first.

| Criterion        | Weight | Pass condition                                           |
|------------------|--------|----------------------------------------------------------|
| AC coverage      | 35%    | Every acceptance criterion from the plan is addressed    |
| Code correctness | 30%    | Build passes; no suppressed errors or TODOs left in code |
| Convention fit   | 20%    | Naming, structure, and annotations match repo patterns   |
| Test adequacy    | 15%    | All new behavior has corresponding test coverage         |

If score < 8.5 after 3 passes, prepend to the report:
> ⚠️ Confidence: MEDIUM — manual review recommended before merging.

### 6. Update the Plan

Update the `Status` field in `docs/implementation-plans/{{JIRA_KEY}}-plan.md` from `REVIEWED` to `COMPLETED`.

### 7. Report to the Developer

Tell the developer:

- Tasks completed and files modified
- Test results summary (pairs completed, tests written, tests passing)
- Confidence score and iteration count
- Any remaining TODOs or known gaps
- Suggested next step:
  > "When you're ready to open a pull request, run `/open-pull-request`."

---

## Rules

- Never write code directly to `main`.
- Never skip the final build check.
- Never commit without explicit developer permission.
- If a merge conflict is encountered, stop immediately and notify the developer — do not attempt to resolve it
  autonomously.
- If re-run for the same key and the branch already has work, resume from where it left off rather than starting over.
- Do not ask the developer for clarification unless a required input (e.g., ticket ID) is genuinely missing. Infer from
  the plan and repo first.

