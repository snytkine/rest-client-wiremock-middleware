package net.snytkine.springboot.wiremock_middleware;

import static org.junit.jupiter.api.Assertions.*;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import net.snytkine.springboot.wiremock_middleware.model.WireMockProperties;
import org.junit.jupiter.api.Test;

class WireMockConfigurationFactoryTest {

  @Test
  void createsConfigurationWithDefaults() {
    WireMockProperties properties = new WireMockProperties();
    WireMockConfigurationFactory factory = new WireMockConfigurationFactory(properties);

    WireMockConfiguration cfg = factory.wireMockConfiguration();
    assertNotNull(cfg);
  }

  @Test
  void createsConfigurationWithCustomValues() {
    WireMockProperties properties = new WireMockProperties();
    properties.setContainerThreads(5);
    properties.setAsynchronousResponseEnabled(true);
    properties.setAsynchronousResponseThreads(3);
    properties.setRootDirectory("/tmp/wiremock");
    properties.setJournalDisabled(true);
    properties.setMaxRequestJournalEntries(200);
    properties.setGzipDisabled(true);
    properties.setDisableOptimizeXmlFactories(true);
    properties.setStubCorsEnabled(true);
    properties.setStubRequestLoggingDisabled(true);
    properties.setTemplatingEnabled(true);
    properties.setMappingsClassPath("mappings");
    properties.setProxyPassThrough(true);

    WireMockConfigurationFactory factory = new WireMockConfigurationFactory(properties);
    WireMockConfiguration cfg = factory.wireMockConfiguration();
    assertNotNull(cfg);
  }
}
