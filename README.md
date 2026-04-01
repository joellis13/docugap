# DocuGap

DocuGap is a Spring Boot application that runs an AI-powered pipeline to detect documentation gaps across Jira epics.
It connects to Atlassian (Jira + Confluence) and GitHub via the Model Context Protocol (MCP), uses an LLM to identify
missing or outdated documentation, and can publish analysis results back to Confluence.

---

## Table of Contents

- [Project Structure](#project-structure)
- [Technologies & Tools](#technologies--tools)
- [Prerequisites](#prerequisites)
- [Environment Variables](#environment-variables)
- [How to Run](#how-to-run)
- [Key Configuration](#key-configuration)
- [API Reference](#api-reference)
- [Development Workflow](#development-workflow)

---

## Project Structure

```
docugap/
├── .github/
│   ├── copilot-instructions.md     # Coding agent rules and conventions
│   └── prompts/                    # Reusable AI workflow prompts (plan, test, execute, PR)
├── gradle/wrapper/                 # Gradle wrapper files
├── src/
│   ├── main/
│   │   ├── java/com/jellisisland/docugap/
│   │   │   └── DocugapApplication.java
│   │   └── resources/
│   │       └── application.yaml   # All application configuration
│   └── test/
│       └── java/com/jellisisland/docugap/
│           └── DocugapApplicationTests.java
├── build.gradle
└── settings.gradle
```

---

## Technologies & Tools

| Category          | Technology / Library                         | Version     |
|-------------------|----------------------------------------------|-------------|
| Language          | Java                                         | 25          |
| Framework         | Spring Boot                                  | 4.0.5       |
| AI Framework      | Spring AI                                    | 2.0.0-M3    |
| LLM — Anthropic   | Spring AI Anthropic (Claude Sonnet)          | via BOM     |
| LLM — Azure       | Spring AI Azure OpenAI                       | via BOM     |
| MCP Client        | Spring AI MCP Client (streamable-HTTP)       | via BOM     |
| Web               | Spring MVC (`spring-boot-starter-webmvc`)    | via Boot    |
| API Docs          | SpringDoc OpenAPI / Swagger UI               | 3.0.2       |
| Observability     | Spring Boot Actuator                         | via Boot    |
| Utilities         | Lombok                                       | via Boot    |
| Testing           | JUnit 5 + Spring Boot Test                   | via Boot    |
| Build             | Gradle (Wrapper)                             | 9.4.1       |

---

## Prerequisites

- **Java 25** (JDK) — required by the Gradle toolchain
- **Gradle** — use the included wrapper (`./gradlew`); no local install needed
- API credentials for at least one LLM provider (see [Environment Variables](#environment-variables))
- MCP access tokens for the Atlassian and GitHub MCP servers

---

## Environment Variables

Set these before starting the application. Variables for the inactive LLM provider are still required by Spring AI
unless the corresponding auto-configuration is excluded.

| Variable                | Required | Description                                      |
|-------------------------|----------|--------------------------------------------------|
| `ANTHROPIC_API_KEY`     | Yes\*    | Anthropic Claude API key                         |
| `AZURE_OPENAI_API_KEY`  | Yes\*    | Azure OpenAI API key                             |
| `AZURE_OPENAI_ENDPOINT` | Yes\*    | Azure OpenAI endpoint URL                        |
| `AZURE_DEPLOYMENT_NAME` | Yes\*    | Azure OpenAI deployment / model name             |
| `GITHUB_DEFAULT_REPO`   | Yes      | Default GitHub repository to analyze (owner/repo) |

\* Both Anthropic and Azure variables are required because both starters are on the classpath. Set dummy values for
whichever provider is not in use.

---

## How to Run

### 1. Clone and build

```bash
git clone https://github.com/joellis13/slimdroid-api.git   # or your fork
cd docugap
./gradlew build
```

### 2. Set environment variables

```bash
export ANTHROPIC_API_KEY=sk-ant-...
export AZURE_OPENAI_API_KEY=dummy
export AZURE_OPENAI_ENDPOINT=https://dummy.openai.azure.com
export AZURE_DEPLOYMENT_NAME=dummy
export GITHUB_DEFAULT_REPO=owner/repo
```

### 3. Start the application

```bash
./gradlew bootRun
```

Or run the built JAR directly:

```bash
java -jar build/libs/docugap-0.0.1-SNAPSHOT.jar
```

### 4. Run tests

```bash
./gradlew test
```

Test reports are written to `build/reports/tests/test/index.html`.

---

## Key Configuration

All runtime configuration lives in `src/main/resources/application.yaml`.

### Switch LLM provider

```yaml
docugap:
  llm:
    provider: anthropic   # or 'azure'
```

### Pipeline behavior

```yaml
docugap:
  pipeline:
    max-epics-per-run: 10      # Epics analyzed per run
    epic-lookback-days: 180    # Skip epics closed more than N days ago
    pause-for-approval: true   # Pause after Phase 5 for manual approval via POST /gaps/approve
```

### Output destinations

```yaml
docugap:
  output:
    console: true       # Always log to console
    json: true          # Write PipelineResult JSON to ./output/
    confluence: false   # Write results to Confluence (disable during development)
```

### Scheduled runs

```yaml
docugap:
  scheduler:
    enabled: false                # Set to true to enable scheduled runs
    cron: "0 0 9 * * MON"         # Every Monday at 9 am
    default-project-key: DGAP
    default-space-key: DGAP
```

---

## API Reference

Once running, interactive API documentation is available at:

| URL                                     | Description              |
|-----------------------------------------|--------------------------|
| `http://localhost:8080/swagger-ui.html` | Swagger UI               |
| `http://localhost:8080/api-docs`        | OpenAPI JSON spec        |
| `http://localhost:8080/actuator/health` | Health check             |
| `http://localhost:8080/actuator/info`   | Application info         |

---

## Development Workflow

This project uses a TDD-first workflow driven by reusable AI prompts in `.github/prompts/`.
The recommended order is:

```
/plan-story → /write-tests → /execute-plan → /open-pull-request
```

Each `/execute-plan` cycle enforces a **red → implement → green → commit** loop per test/implementation pair.
See `.github/copilot-instructions.md` for the full workflow description.

