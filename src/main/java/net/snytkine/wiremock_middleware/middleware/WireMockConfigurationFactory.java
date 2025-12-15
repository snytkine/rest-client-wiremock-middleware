package net.snytkine.wiremock_middleware.middleware;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.util.Optional;
import net.snytkine.wiremock_middleware.model.WireMockProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
    prefix = "net.snytkine.rest-client-middleware.wiremock",
    name = "enabled",
    havingValue = "true")
public class WireMockConfigurationFactory {

  private final WireMockProperties wireMockProperties;

  public WireMockConfigurationFactory(WireMockProperties wireMockProperties) {
    this.wireMockProperties = wireMockProperties;
  }

  @Bean
  public WireMockConfiguration wireMockConfiguration() {
    WireMockConfiguration wireMockConfiguration = new WireMockConfiguration();

    Optional.ofNullable(wireMockProperties.getContainerThreads())
        .ifPresent(v -> wireMockConfiguration.containerThreads(v));
    Optional.ofNullable(wireMockProperties.getAsynchronousResponseEnabled())
        .ifPresent(v -> wireMockConfiguration.asynchronousResponseEnabled(v));
    Optional.ofNullable(wireMockProperties.getAsynchronousResponseThreads())
        .ifPresent(v -> wireMockConfiguration.asynchronousResponseThreads(v));
    Optional.ofNullable(wireMockProperties.getRootDirectory())
        .ifPresent(v -> wireMockConfiguration.usingFilesUnderDirectory(v));

    if (Boolean.TRUE.equals(wireMockProperties.getJournalDisabled())) {
      wireMockConfiguration.disableRequestJournal();
    }

    Optional.ofNullable(wireMockProperties.getMaxRequestJournalEntries())
        .ifPresent(v -> wireMockConfiguration.maxRequestJournalEntries(v));
    Optional.ofNullable(wireMockProperties.getGzipDisabled())
        .ifPresent(v -> wireMockConfiguration.gzipDisabled(v));
    Optional.ofNullable(wireMockProperties.getDisableOptimizeXmlFactories())
        .ifPresent(v -> wireMockConfiguration.disableOptimizeXmlFactoriesLoading(v));
    Optional.ofNullable(wireMockProperties.getStubCorsEnabled())
        .ifPresent(v -> wireMockConfiguration.stubCorsEnabled(v));
    Optional.ofNullable(wireMockProperties.getStubRequestLoggingDisabled())
        .ifPresent(v -> wireMockConfiguration.stubRequestLoggingDisabled(v));
    // templatingEnabled and proxyPassThrough are primitive booleans on properties; call directly
    wireMockConfiguration.templatingEnabled(wireMockProperties.isTemplatingEnabled());
    Optional.ofNullable(wireMockProperties.getMappingsClassPath())
        .ifPresent(v -> wireMockConfiguration.usingFilesUnderClasspath(v));
    wireMockConfiguration.proxyPassThrough(wireMockProperties.isProxyPassThrough());

    String[] myextensions = {"org.wiremock.RandomExtension"};

    wireMockConfiguration.trustAllProxyTargets(true);
    wireMockConfiguration.requestJournalDisabled();
    wireMockConfiguration.extensions(myextensions);

    return wireMockConfiguration;
  }
}
