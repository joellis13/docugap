package com.jellisisland.docugap.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class AiProviderConfiguration {

    private final DocGapProperties docGapProperties;
    private final Environment environment;

    @PostConstruct
    public void validateProviderCredentials() {
        String provider = docGapProperties.getLlm().getProvider();
        if ("anthropic".equalsIgnoreCase(provider)) {
            String apiKey = environment.getProperty("spring.ai.anthropic.api-key", "");
            if (!StringUtils.hasText(apiKey)) {
                throw new IllegalStateException(
                    "LLM provider is 'anthropic' but spring.ai.anthropic.api-key is not configured. " +
                    "Set the ANTHROPIC_API_KEY environment variable.");
            }
        }
    }
}
