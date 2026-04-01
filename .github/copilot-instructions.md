# Copilot Instructions

## MCP Servers

- **GitHub MCP** – branch and file operations
    - The Repository is `joellis13/docugap` and the default branch is `main`
- **Atlassian MCP** – Jira and Confluence
    - The Space Key for both Jira and Confluence is `DGAP`
- **Context7** - Documentation reference

## Global Rules (VERY IMPORTANT)

- **Always ask the developer for clarification if any requirement or instruction is ambiguous.**
- **Never commit or push without explicit developer instruction or permission.**
- **NO HALLUCINATION — if you don't know, say you don't know.**
- **NO ASSUMPTIONS — if information is missing, ask the developer instead of guessing.**
- **Always use US English spelling — never British English.**

| ❌ British                | ✅ US                     |
|--------------------------|--------------------------|
| initialise               | initialize               |
| customise                | customize                |
| recognise / unrecognised | recognize / unrecognized |
| authorise                | authorize                |
| serialise                | serialize                |
| organise                 | organize                 |
| behaviour                | behavior                 |
| colour                   | color                    |
| artefact                 | artifact                 |
| licence (noun)           | license                  |
| practise (verb)          | practice                 |
| cancelled                | canceled                 |
| travelling               | traveling                |
| modelling                | modeling                 |
| fulfil                   | fulfill                  |
| programme                | program                  |

## Coding Style

- Keep to SOLID and DRY principles
- Use descriptive names for variables, functions, and files (so commenting is minimized)
- Review and account for IDE errors
- Do not create interface/impl pairs unless there are multiple implementations or the interface is required for
  testing — prefer a single concrete class
- All code, including Markdown, should be formatted according to IDE defaults

## Testing Style and Requirements

- Anytime feature code or tests are updated (like when addressing PR feedback), run any impacted or related tests and
  fix any errors before moving on.

## Prompts

Reusable workflows live in `.github/prompts/`. Use them when a developer
invokes a named command (e.g. `/plan-story`).

### Workflow Order

```
/plan-story → /write-tests → /execute-plan → /open-pull-request
```

#### How the TDD loop works

The plan produced by `/plan-story` arranges Implementation Steps in test/implement pairs
(e.g. Step 1 writes tests, Step 2 implements, Step 3 writes tests, Step 4 implements…).

`/execute-plan` enforces a **red → implement → green → commit** cycle for each pair:

1. Confirm the test class fails (red) before writing any implementation code.
2. Write the implementation step.
3. Confirm the test class passes (green) before committing or advancing.
4. Commit the pair, then move to the next one.

#### Role of `/write-tests`

`/write-tests` is **recommended but optional**. Run it after `/plan-story` to write all
test stubs upfront in a single pass — this gives you the full test picture before any
code is written and lets you review or adjust tests before implementation begins.

If you skip `/write-tests`, `/execute-plan` will write each pair's tests inline as part
of its loop (Stage A). Either path enforces the same red/green gates.

**Only skip `/write-tests` entirely when the story requires no new tests.**

Use `/update-plan` at any point after `/plan-story` when a decision, requirement change, or
correction affects the implementation plan. It updates the local plan file and posts a
Plan Corrections comment to Jira — always with explicit developer permission.

### IDE Compatibility Note

The `{{input:variable:hint}}` syntax in prompt files is **VS Code only**.
In JetBrains, include the Jira key directly in your message, for example:
> "plan story DGAP-42"

YAML frontmatter (`mode`, `tools`, `description`) in `.prompt.md` files is
VS Code only and is safely ignored by JetBrains.

## Terminal Usage

- **Prefer tools over terminal commands** — use MCP/IDE tools (file reads, GitHub MCP, Atlassian MCP, etc.) instead of
  shell commands whenever a tool can do the same job.
- **Always truncate verbose output** — pipe to `| tail -N` (prefer `tail -20` unless more is needed) or `| head -N` on
  any command that may produce long output (build logs, test output, `git log`, `git diff`, `git status`, etc.).
- **Never open a pager** — append `--no-pager` to `git` commands or pipe to `| cat` to prevent interactive pagers from
  hanging the terminal (e.g. `git --no-pager diff`, `git --no-pager log | tail -20`).
- **For `git diff`** — use `git --no-pager diff <file>` or replace with `read_file` + a mental comparison when the
  change is already known.

## Human Language Writing Style

- Clear and concise — avoid unnecessary words or jargon
