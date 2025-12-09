package net.snytkine.wiremock_middleware.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "net.snytkine.rest-client-middleware.wiremock")
@Data
public class WireMockProperties {
    private boolean enabled = false;
    private Integer containerThreads;
    private Boolean asynchronousResponseEnabled;
    private Integer asynchronousResponseThreads;
    private String rootDirectory;
    private Boolean journalDisabled;
    private Integer maxRequestJournalEntries;
    private Boolean gzipDisabled;
    private Boolean disableOptimizeXmlFactories;
    private Boolean stubCorsEnabled;
    private Boolean stubRequestLoggingDisabled;
    private Long maxTemplateCacheEntries;
    private Boolean globalTemplating;
    private boolean templatingEnabled;
    private String mappingsClassPath;
    private boolean proxyPassThrough;
    private String mockResponseHeader;
}
