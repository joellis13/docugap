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
    private DocUGapProperties docUGapProperties;

    @Autowired
    private SchedulerProperties schedulerProperties;

    @Test
    void testLlmProviderDefaultsToAnthropic() {
        assertEquals("anthropic", docUGapProperties.getLlm().getProvider());
    }

    @Test
    void testPipelinePropertiesLoaded() {
        DocUGapProperties.PipelineProperties pipeline = docUGapProperties.getPipeline();
        assertEquals(5, pipeline.getMaxEpicsPerRun());
        assertEquals(90, pipeline.getEpicLookbackDays());
        assertFalse(pipeline.isPauseForApproval());
    }

    @Test
    void testAtlassianPropertiesLoaded() {
        DocUGapProperties.AtlassianProperties atlassian = docUGapProperties.getAtlassian();
        assertEquals("test-cloud-id", atlassian.getCloudId());
        assertEquals("TEST", atlassian.getJiraProjectKey());
        assertEquals("TEST", atlassian.getConfluenceSpaceKey());
        assertEquals("12345", atlassian.getSessionLogPageId());
    }

    @Test
    void testGitHubPropertiesLoaded() {
        assertEquals("test-owner/test-repo", docUGapProperties.getGithub().getDefaultRepo());
    }

    @Test
    void testOutputPropertiesLoaded() {
        DocUGapProperties.OutputProperties output = docUGapProperties.getOutput();
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