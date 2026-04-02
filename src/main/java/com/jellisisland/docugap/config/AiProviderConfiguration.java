package com.jellisisland.docugap.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AiProviderConfiguration {

    private final DocugapProperties docugapProperties;
    private final Environment environment;

    @PostConstruct
    public void validateProviderCredentials() {
        String provider = docugapProperties.getLlm().getProvider();
        if ("anthropic".equalsIgnoreCase(provider)) {
            String apiKey = environment.getProperty("spring.ai.anthropic.api-key", "");
            if (StringUtils.isBlank(apiKey)) {
                throw new IllegalStateException(
                    "LLM provider is 'anthropic' but spring.ai.anthropic.api-key is not configured. " +
                    "Set the ANTHROPIC_API_KEY environment variable.");
            }
        } else if ("azure".equalsIgnoreCase(provider)) {
            String apiKey = environment.getProperty("spring.ai.azure.openai.api-key", "");
            String endpoint = environment.getProperty("spring.ai.azure.openai.endpoint", "");
            String deploymentName = environment.getProperty(
                "spring.ai.azure.openai.chat.options.deployment-name", "");
            if (StringUtils.isBlank(apiKey)) {
                throw new IllegalStateException(
                    "LLM provider is 'azure' but spring.ai.azure.openai.api-key is not configured. " +
                    "Set the AZURE_OPENAI_API_KEY environment variable or spring.ai.azure.openai.api-key property.");
            }
            if (StringUtils.isBlank(endpoint)) {
                throw new IllegalStateException(
                    "LLM provider is 'azure' but spring.ai.azure.openai.endpoint is not configured. " +
                    "Set the AZURE_OPENAI_ENDPOINT environment variable or spring.ai.azure.openai.endpoint property.");
            }
            if (StringUtils.isBlank(deploymentName)) {
                throw new IllegalStateException(
                    "LLM provider is 'azure' but spring.ai.azure.openai.chat.options.deployment-name is not configured. " +
                    "Set the appropriate Azure OpenAI deployment name property.");
            }
        } else {
            throw new IllegalStateException(
                "Unsupported LLM provider: '" + provider + "'. Supported values are: anthropic, azure.");
        }
    }
}
