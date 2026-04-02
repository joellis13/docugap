package com.jellisisland.docugap.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class ConfigurationPropertiesTests {

    @Autowired
    private DocugapProperties docugapProperties;

    @Autowired
    private SchedulerProperties schedulerProperties;

    @Test
    void testLlmProviderConfiguredToAnthropic() {
        assertEquals("anthropic", docugapProperties.getLlm().getProvider());
    }

    @Test
    void testPipelinePropertiesLoaded() {
        DocugapProperties.PipelineProperties pipeline = docugapProperties.getPipeline();
        assertEquals(5, pipeline.getMaxEpicsPerRun());
        assertEquals(90, pipeline.getEpicLookbackDays());
        assertFalse(pipeline.isPauseForApproval());
    }

    @Test
    void testAtlassianPropertiesLoaded() {
        DocugapProperties.AtlassianProperties atlassian = docugapProperties.getAtlassian();
        assertEquals("test-cloud-id", atlassian.getCloudId());
        assertEquals("TEST", atlassian.getJiraProjectKey());
        assertEquals("TEST", atlassian.getConfluenceSpaceKey());
        assertEquals("12345", atlassian.getSessionLogPageId());
    }

    @Test
    void testGitHubPropertiesLoaded() {
        assertEquals("test-owner/test-repo", docugapProperties.getGithub().getDefaultRepo());
    }

    @Test
    void testOutputPropertiesLoaded() {
        DocugapProperties.OutputProperties output = docugapProperties.getOutput();
        assertTrue(output.isConsole());
        assertFalse(output.isJson());
        assertEquals("./test-output/", output.getJsonPath());
        assertFalse(output.isConfluence());
    }

    @Test
    void testSchedulerPropertiesLoaded() {
        assertFalse(schedulerProperties.isEnabled());
        assertEquals("0 0 9 * * MON", schedulerProperties.getCron());
        assertEquals("TEST", schedulerProperties.getDefaultProjectKey());
        assertEquals("TEST", schedulerProperties.getDefaultSpaceKey());
    }
}