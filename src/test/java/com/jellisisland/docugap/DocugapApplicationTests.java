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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class DocugapApplicationTests {

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
            assertThrows(Exception.class, () -> {
                SpringApplication app = new SpringApplication(DocugapApplication.class);
                app.addInitializers((ConfigurableApplicationContext ctx) -> {
                    ConfigurableEnvironment env = ctx.getEnvironment();
                    Map<String, Object> overrides = new HashMap<>();
                    overrides.put("spring.ai.mcp.client.enabled", "false");
                    overrides.put("spring.ai.azure.openai.api-key", "stub");
                    overrides.put("spring.ai.azure.openai.endpoint", "https://stub.azure.com");
                    overrides.put("spring.ai.azure.openai.chat.options.deployment-name", "stub");
                    overrides.put("docugap.github.default-repo", "stub/repo");
                    // Mask ANTHROPIC_API_KEY with an empty value so ${ANTHROPIC_API_KEY}
                    // in application.yaml resolves to blank, triggering startup validation.
                    overrides.put("ANTHROPIC_API_KEY", "");
                    env.getPropertySources().addFirst(new MapPropertySource("missingKeyOverrides", overrides));
                });
                app.run();
            });
        }
    }
}
