package net.snytkine.springboot.wiremock_middleware.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "net.snytkine.rest-client-wiremock-interceptor")
@Component
@Data
public class WireMockProperties {
  private boolean enabled = false;
  private Integer containerThreads = 1;
  private Boolean asynchronousResponseEnabled = false;
  private Integer asynchronousResponseThreads;
  private String rootDirectory;
  private Boolean journalDisabled;
  private Integer maxRequestJournalEntries;
  private Boolean gzipDisabled;
  private Boolean disableOptimizeXmlFactories = false;
  private Boolean stubCorsEnabled = false;
  private Boolean stubRequestLoggingDisabled;
  private Long maxTemplateCacheEntries;
  private Boolean globalTemplating = false;
  private boolean templatingEnabled = false;
  private String mappingsClassPath;
  private boolean proxyPassThrough;
  private String mockResponseHeader;
  private String mockResponseHeaderValue;
}
