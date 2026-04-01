# DGAP-8 Implementation Plan

## Acceptance Criteria

| ID   | Criterion                                                                                                              |
|------|------------------------------------------------------------------------------------------------------------------------|
| AC-1 | `docgap.llm.provider` property defined with default value `anthropic`                                                  |
| AC-2 | `docgap.pipeline.*` properties defined: `max-epics-per-run`, `epic-lookback-days`, `pause-for-approval`                |
| AC-3 | `docgap.output.*` properties defined: `console`, `json`, `json-path`, `confluence`                                     |
| AC-4 | `docgap.atlassian.*` properties defined: `cloud-id`, `jira-project-key`, `confluence-space-key`, `session-log-page-id` |
| AC-5 | `docgap.github.*` properties defined with `default-repo`                                                               |
| AC-6 | `spring.ai.*` blocks present for both Anthropic and Azure OpenAI with placeholder values                               |
| AC-7 | `spring.ai.mcp.client.sse.connections.*` configured for Atlassian and GitHub MCP servers                               |
| AC-8 | `springdoc.*` configured for Swagger UI                                                                                |
| AC-9 | Application starts successfully with configuration in place                                                            |

## Implementation Steps

### Step 1: Write Configuration Loading Tests

**File:** `src/test/java/com/jellisisland/docugap/config/ConfigurationPropertiesTests.java`  
**Class:** `ConfigurationPropertiesTests`

**Test profile:** Create `src/test/resources/application-test.yaml` with stub/placeholder values for all
required env-var-backed properties (e.g. `ANTHROPIC_API_KEY`, `AZURE_OPENAI_API_KEY`) and set
`spring.ai.mcp.client.enabled: false` so no MCP connections are attempted during unit tests. Annotate the
class with `@ActiveProfiles("test")`.

| Test Case                            | Inputs                                                 | Expected Outcome                                     |
|--------------------------------------|--------------------------------------------------------|------------------------------------------------------|
| `testLlmProviderDefaultsToAnthropic` | Load properties with no explicit `docgap.llm.provider` | Default provider is `anthropic`                      |
| `testPipelinePropertiesLoaded`       | Load properties with all `docgap.pipeline.*` set       | All three properties accessible via binding class    |
| `testAtlassianPropertiesLoaded`      | Load properties with all `docgap.atlassian.*` set      | All four properties accessible via binding class     |
| `testGitHubPropertiesLoaded`         | Load properties with `docgap.github.default-repo` set  | Property accessible and matches input                |
| `testOutputPropertiesLoaded`         | Load properties with all `docgap.output.*` set         | All four properties accessible via binding class     |
| `testSchedulerPropertiesLoaded`      | Load properties with all `docgap.scheduler.*` set      | All four fields accessible via `SchedulerProperties` |

### Step 2: Create Configuration Properties Binding Classes

**File:** `src/main/java/com/jellisisland/docugap/config/DocGapProperties.java`  
**Class:** `DocGapProperties`  
**Methods/Inner Classes:**

- Root class with `@ConfigurationProperties(prefix = "docgap")`
- Inner class `LlmProperties` (prefix: `llm`)
- Inner class `PipelineProperties` (prefix: `pipeline`)
- Inner class `OutputProperties` (prefix: `output`)
- Inner class `AtlassianProperties` (prefix: `atlassian`)
- Inner class `GitHubProperties` (prefix: `github`)

**File:** `src/main/java/com/jellisisland/docugap/config/SchedulerProperties.java`  
**Class:** `SchedulerProperties`  
**Methods/Inner Classes:**

- Separate class with `@ConfigurationProperties(prefix = "docgap.scheduler")`
- Fields: `enabled`, `cron`, `defaultProjectKey`, `defaultSpaceKey`

**Enabling configuration properties scanning:** Add `@ConfigurationPropertiesScan` to `DocugapApplication.java`
(or use explicit `@EnableConfigurationProperties` on a `@Configuration` class) so Spring picks up all binding
classes.

**Azure OpenAI conditional:** Wrap the Azure OpenAI autoconfiguration with `@ConditionalOnProperty(name =
"spring.ai.azure.openai.api-key")` so the bean is only created when credentials are present — falling back to
Anthropic otherwise.

### Step 3: Create Application YAML Configuration

**File:** `src/main/resources/application.yaml`  
**Content Structure:**

- `spring.application.name: docugap`
- `spring.ai.anthropic.*` (API key from env var, model, max-tokens)
- `spring.ai.azure.openai.*` (API key, endpoint, deployment name from env vars)
- `spring.ai.mcp.client.*` (enabled, type, request-timeout, SSE connections)
- `docgap.*` (all properties from AC-1 through AC-8)
- `springdoc.*` (Swagger UI configuration)
- `management.*` (Actuator endpoints)
- `logging.*` (root and package-level logging)

### Step 4: Write Integration Tests for Application Startup

**File:** `src/test/java/com/jellisisland/docugap/DocugapApplicationTests.java`  
**Class:** `DocugapApplicationTests` (updates the existing file — do not create a new class)

Use `@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)` and `@ActiveProfiles("test")`
on the class so the test profile disables MCP connections and provides stub credentials.

For `testApplicationFailsWithMissingCriticalProperty`, isolate the failing context in a **nested static
`@SpringBootTest` class** with a custom `ApplicationContextInitializer` that removes the `ANTHROPIC_API_KEY`
environment property. This prevents the failing context from interfering with the other tests in the class.

| Test Case                                         | Inputs                                           | Expected Outcome                                   |
|---------------------------------------------------|--------------------------------------------------|----------------------------------------------------|
| `testApplicationStartsWithValidConfiguration`     | All env vars set (mock MCP servers not required) | Application context loads successfully             |
| `testSwaggerUIEndpointAccessible`                 | Application started                              | Swagger UI accessible at `/swagger-ui.html`        |
| `testActuatorHealthEndpointAccessible`            | Application started                              | Health endpoint returns UP status                  |
| `testApplicationFailsWithMissingCriticalProperty` | Omit `ANTHROPIC_API_KEY` env var                 | Application context fails to load with clear error |

### Step 5: Create Configuration Documentation (Optional but Recommended)

**Files:**

- `docs/configuration/application-yml-properties.md`
- `.env.example` (root of repo) — lists all required and optional environment variables with descriptions and
  example values; no real credentials

**`application-yml-properties.md` Content:**

- Property reference table for all DocuGap-specific properties
- Default values
- Required vs. optional
- Environment variable substitution syntax

---

## Risks

| Risk                                        | Impact | Mitigation                                                                                                                 |
|---------------------------------------------|--------|----------------------------------------------------------------------------------------------------------------------------|
| YAML syntax errors prevent startup          | High   | Use IDE validation; run Step 4 tests before committing                                                                     |
| Environment variables not set in deployment | Medium | Document all required env vars; provide `.env.example` file; test locally with env vars                                    |
| MCP server URLs not reachable               | Medium | MCP client requests are asynchronous; phase tests mock the client; verify URLs in DGAP-12 (Atlassian) and DGAP-15 (GitHub) |
| Property names change during refactor       | Low    | All properties are bound to classes; rename refactoring will be caught at compile time                                     |
| Azure OpenAI credentials missing            | Low    | Conditional bean instantiation via `@ConditionalOnProperty`; defaults to Anthropic if not configured                       |
| Actuator endpoints expose sensitive info    | Low    | Restrict exposure to `health,info` only; do not include `env` or `configprops`                                             |

