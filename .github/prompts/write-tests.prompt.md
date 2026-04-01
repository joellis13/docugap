---
mode: agent
tools: [ 'github', 'context7' ]
description: 'Write failing TDD tests for a Jira story based on its implementation plan'
---

# Write Tests (TDD)

Triggered by: `/write-tests` or "write tests for <JIRA-KEY>"

Run after `/plan-story`, before feature coding.

```
JIRA_KEY: {{input:jira_key:Jira issue key (e.g. DGAP-42)}}
```

---

## Steps

### 1. Load Context

Fetch in parallel:

- The Jira story (`summary`, `acceptance_criteria`)
- The implementation plan from `docs/implementation-plans/{{JIRA_KEY}}-plan.md`

Stop and report if either is missing.
If either the story or the implementation plan explicitly states that no tests are required, explain this to the developer and stop without writing any tests.

### 2. Infer Conventions

Scan the repo to determine:

- Test file location and naming pattern (e.g. `src/test/java/...`)
- Frameworks in use (expect JUnit 5, Mockito, Spring Test — confirm from `pom.xml` or `build.gradle`)
- Any base test classes or shared fixtures already in use

Do not ask the developer. Infer from the repo.

### 3. Define Test Cases

For each acceptance criterion, derive the minimal set of test cases that would
prove it works. For each test case record:

- **Layer** — Unit or Integration
- **Name** — `methodName_stateUnderTest_expectedBehavior`
- **What it proves** — one sentence

Prefer unit tests. Add integration tests only where a unit test cannot
adequately cover the behavior (e.g. DB queries, HTTP contracts, Spring context
wiring).

### 4. Write the Tests

Write all test cases as compilable Java. Tests must:

- **Fail** without the implementation (true TDD red state)
- Use `@Test`, `@ExtendWith`, `@SpringBootTest` etc. as appropriate
- Mock external dependencies with Mockito; avoid over-mocking internals
- Be self-contained — no shared mutable state between tests

Place files following the convention inferred in Step 2.

### 5. Evaluate & Improve (up to 3 passes)

Score the test suite. Improve the weakest area each pass until ≥ 8.5/10 or 3
passes are done.

| Criterion      | Weight | Pass condition                                         |
|----------------|--------|--------------------------------------------------------|
| Coverage of AC | 40%    | Every acceptance criterion has ≥ 1 failing test        |
| Test clarity   | 25%    | Each test has a single, obvious assertion              |
| Correct layer  | 20%    | No unit test that should be integration, or vice versa |
| Convention fit | 15%    | Naming, structure, and annotations match repo patterns |

If score < 8.5 after 3 passes, prepend:
> ⚠️ Confidence: MEDIUM — review test coverage before implementing.

### 6. Report

1. Tell the developer:
    - Files written and their locations
    - Number of test cases by layer (Unit / Integration), grouped by plan step pair
    - Confidence score
    - Reminder: run the full suite now to confirm all tests fail before coding
    - Ask the developer to confirm before committing to the branch
    - Suggested next step:
      > "When tests are committed and confirmed red, run `/execute-plan`. It will enforce a red → implement → green cycle for each step pair automatically."

---

## Rules

- Never write implementation code to make tests pass.
- If a behavior cannot be tested without implementation details, note it as a
  gap rather than forcing a brittle test.
- Do not ask the developer unless context from Step 1 is missing.
- In network/socket tests, always use an explicit loopback address (`"127.0.0.1"` or
  `InetAddress.getLoopbackAddress()`) — never `"localhost"`. Bind `ServerSocket` to the
  same address. This avoids false negatives on systems where `localhost` resolves to
  IPv6 (`::1`) while the listener is IPv4-only.
