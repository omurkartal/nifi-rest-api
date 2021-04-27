package edu.omur.nifirestapi.configuration;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

@Configuration
@PropertySource("classpath:nifi-${spring.profiles.active}.properties")
@ConfigurationProperties(prefix = "nifi")
@Getter
@Setter
@NoArgsConstructor
public class NifiProperties {
    private static final Logger logger = LoggerFactory.getLogger(NifiProperties.class);

    private String tokenUrl;
    private String inquiryUrl;
    private String flowFileQueuesUrl;
    private int minDurationForQueueContent;
    private String rootProcessorGroupId;
    private ArrayList<String> excludedProcessorGroupIdList;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private boolean hasSecureConnection;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private boolean usePreviousToken;
    private String username;
    private String password;

    @PostConstruct
    public void checkRequiredFields() {
        logger.info(this.toString());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("tokenUrl: %s %s", tokenUrl, System.lineSeparator()));
        sb.append(String.format("inquiryUrl: %s %s", inquiryUrl, System.lineSeparator()));
        sb.append(String.format("flowFileQueuesUrl: %s %s", flowFileQueuesUrl, System.lineSeparator()));
        sb.append(String.format("minDurationForQueueContent: %d %s", minDurationForQueueContent, System.lineSeparator()));
        sb.append(String.format("rootProcessorGroupId: %s %s", rootProcessorGroupId, System.lineSeparator()));
        sb.append(String.format("excludedProcessorGroupIdList: %s %s", String.join(",", excludedProcessorGroupIdList), System.lineSeparator()));
        sb.append(String.format("hasSecureConnection: %s %s", hasSecureConnection, System.lineSeparator()));
        sb.append(String.format("usePreviousToken: %s %s", usePreviousToken, System.lineSeparator()));
        sb.append(String.format("username: %s %s", username, System.lineSeparator()));
        return sb.toString();
    }

    public boolean usePreviousToken() {
        return usePreviousToken;
    }

    public boolean hasSecureConnection() {
        return hasSecureConnection;
    }
}
