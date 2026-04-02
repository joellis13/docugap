package com.jellisisland.docugap;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class DocugapApplicationTests {

    private static boolean containsInCauseChain(Throwable ex, String text) {
        for (Throwable t = ex; t != null; t = t.getCause()) {
            if (t.getMessage() != null && t.getMessage().contains(text)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Runs the application without the test profile so the main application.yaml
     * placeholder {@code ${ANTHROPIC_API_KEY}} is in play. All other required
     * env-var-backed properties are stubbed via an ApplicationContextInitializer so
     * the Anthropic key is the sole unresolvable value.
     */
    @Nested
    class MissingApiKeyTests {

        @Test
        void testApplicationFailsWithMissingCriticalProperty() {
            Exception ex = assertThrows(Exception.class, () -> {
                SpringApplication app = new SpringApplication(DocugapApplication.class);
                app.addInitializers((ConfigurableApplicationContext ctx) -> {
                    ConfigurableEnvironment env = ctx.getEnvironment();
                    Map<String, Object> overrides = new HashMap<>();
                    overrides.put("spring.ai.mcp.client.enabled", "false");
                    overrides.put("spring.ai.azure.openai.api-key", "stub");
                    overrides.put("spring.ai.azure.openai.endpoint", "https://stub.azure.com");
                    overrides.put("spring.ai.azure.openai.chat.options.deployment-name", "stub");
                    overrides.put("docugap.github.default-repo", "stub/repo");
                    // Ensure the embedded server binds to a random available port for this test run.
                    overrides.put("server.port", "0");
                    // Mask ANTHROPIC_API_KEY with an empty value so ${ANTHROPIC_API_KEY}
                    // in application.yaml resolves to blank, triggering startup validation.
                    overrides.put("ANTHROPIC_API_KEY", "");
                    env.getPropertySources().addFirst(new MapPropertySource("missingKeyOverrides", overrides));
                });
                app.run();
            });
            assertTrue(
                    containsInCauseChain(ex, "spring.ai.anthropic.api-key") ||
                    containsInCauseChain(ex, "ANTHROPIC_API_KEY"),
                    "Expected exception to reference spring.ai.anthropic.api-key or ANTHROPIC_API_KEY, but was: "
                            + ex.getMessage());
        }
    }

    @Nested
    class UnsupportedProviderTests {

        @Test
        void testApplicationFailsWithUnsupportedProvider() {
            Exception ex = assertThrows(Exception.class, () -> {
                SpringApplication app = new SpringApplication(DocugapApplication.class);
                app.addInitializers((ConfigurableApplicationContext ctx) -> {
                    ConfigurableEnvironment env = ctx.getEnvironment();
                    Map<String, Object> overrides = new HashMap<>();
                    overrides.put("spring.ai.mcp.client.enabled", "false");
                    overrides.put("spring.ai.anthropic.api-key", "stub");
                    overrides.put("docugap.llm.provider", "unsupported-provider");
                    overrides.put("server.port", "0");
                    env.getPropertySources().addFirst(new MapPropertySource("unsupportedProviderOverrides", overrides));
                });
                app.run();
            });
            assertTrue(
                    containsInCauseChain(ex, "unsupported-provider") ||
                    containsInCauseChain(ex, "Unsupported LLM provider"),
                    "Expected exception to reference unsupported provider value, but was: " + ex.getMessage());
        }
    }
}
