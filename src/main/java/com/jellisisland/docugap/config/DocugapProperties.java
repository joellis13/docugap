package com.jellisisland.docugap.config;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
@Data
@ConfigurationProperties(prefix = "docugap")
public class DocugapProperties {
    private LlmProperties llm = new LlmProperties();
    private PipelineProperties pipeline = new PipelineProperties();
    private OutputProperties output = new OutputProperties();
    private AtlassianProperties atlassian = new AtlassianProperties();
    private GitHubProperties github = new GitHubProperties();
    @Data
    public static class LlmProperties {
        private String provider = "anthropic";
    }
    @Data
    public static class PipelineProperties {
        private int maxEpicsPerRun;
        private int epicLookbackDays;
        private boolean pauseForApproval;
    }
    @Data
    public static class OutputProperties {
        private boolean console;
        private boolean json;
        private String jsonPath;
        private boolean confluence;
    }
    @Data
    public static class AtlassianProperties {
        private String cloudId;
        private String jiraProjectKey;
        private String confluenceSpaceKey;
        private String sessionLogPageId;
    }
    @Data
    public static class GitHubProperties {
        private String defaultRepo;
    }
}
