# Application YAML Properties Reference

All DocuGap-specific properties are bound to `DocUGapProperties` (prefix `docugap`) and
`SchedulerProperties` (prefix `docugap.scheduler`).

---

## `docugap.llm`

| Property               | Type   | Default     | Required | Description                                                 |
|------------------------|--------|-------------|----------|-------------------------------------------------------------|
| `docugap.llm.provider` | String | `anthropic` | No       | Active LLM provider. Accepted values: `anthropic`, `azure`. |

---

## `docugap.pipeline`

| Property                              | Type    | Default | Required | Description                                                               |
|---------------------------------------|---------|---------|----------|---------------------------------------------------------------------------|
| `docugap.pipeline.max-epics-per-run`  | int     | —       | Yes      | Maximum number of epics analyzed in a single pipeline run.                |
| `docugap.pipeline.epic-lookback-days` | int     | —       | Yes      | Epics closed more than this many days ago are skipped.                    |
| `docugap.pipeline.pause-for-approval` | boolean | `false` | No       | When `true`, pipeline pauses after Phase 5 awaiting POST `/gaps/approve`. |

---

## `docugap.atlassian`

| Property                                    | Type   | Default | Required | Description                                              |
|---------------------------------------------|--------|---------|----------|----------------------------------------------------------|
| `docugap.atlassian.cloud-id`                | String | —       | Yes      | Atlassian Cloud ID (UUID).                               |
| `docugap.atlassian.jira-project-key`        | String | —       | Yes      | Jira project key (e.g. `DGAP`).                          |
| `docugap.atlassian.confluence-space-key`    | String | —       | Yes      | Confluence space key (e.g. `DGAP`).                      |
| `docugap.atlassian.session-log-page-id`     | String | —       | Yes      | Confluence page ID for the Gap Analysis Session Log.     |

---

## `docugap.github`

| Property                      | Type   | Default | Required | Description                                                                                  |
|-------------------------------|--------|---------|----------|----------------------------------------------------------------------------------------------|
| `docugap.github.default-repo` | String | —       | Yes      | Default repository to analyze in `owner/repo` format. Sourced from `${GITHUB_DEFAULT_REPO}`. |

---

## `docugap.output`

| Property                     | Type    | Default | Required | Description                                                       |
|------------------------------|---------|---------|----------|-------------------------------------------------------------------|
| `docugap.output.console`     | boolean | `true`  | No       | When `true`, pipeline results are printed to stdout.              |
| `docugap.output.json`        | boolean | `false` | No       | When `true`, pipeline results are written as JSON to `json-path`. |
| `docugap.output.json-path`   | String  | —       | No       | Directory path for JSON output files (e.g. `./output/`).          |
| `docugap.output.confluence`  | boolean | `false` | No       | When `true`, results are written to Confluence.                   |

---

## `docugap.scheduler`

| Property                                | Type    | Default         | Required | Description                                         |
|-----------------------------------------|---------|-----------------|----------|-----------------------------------------------------|
| `docugap.scheduler.enabled`             | boolean | `false`         | No       | Enables the scheduled pipeline run.                 |
| `docugap.scheduler.cron`                | String  | `0 0 9 * * MON` | No       | Cron expression for scheduled runs (Spring format). |
| `docugap.scheduler.default-project-key` | String  | —               | No       | Jira project key used for scheduled runs.           |
| `docugap.scheduler.default-space-key`   | String  | —               | No       | Confluence space key used for scheduled runs.       |

---

## Environment Variable Substitution

Several properties are sourced from environment variables:

| Property                                              | Environment Variable    | Notes                                              |
|-------------------------------------------------------|-------------------------|----------------------------------------------------|
| `spring.ai.anthropic.api-key`                         | `ANTHROPIC_API_KEY`     | **Required** when `docugap.llm.provider=anthropic` |
| `spring.ai.azure.openai.api-key`                      | `AZURE_OPENAI_API_KEY`  | Required when `docugap.llm.provider=azure`         |
| `spring.ai.azure.openai.endpoint`                     | `AZURE_OPENAI_ENDPOINT` | Required when `docugap.llm.provider=azure`         |
| `spring.ai.azure.openai.chat.options.deployment-name` | `AZURE_DEPLOYMENT_NAME` | Required when `docugap.llm.provider=azure`         |
| `docugap.github.default-repo`                         | `GITHUB_DEFAULT_REPO`   | Format: `owner/repo`                               |

See `.env.example` at the repository root for a ready-to-copy template.

