/**
 * Copyright 2025 - 2026 Dmitri Snytkine. All rights reserved.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 *
 * <p>See the License for the specific language governing permissions and limitations under the
 * License.
 */
package net.snytkine.springboot.wm_interceptor;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import net.snytkine.springboot.wm_interceptor.model.WireMockProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
@Slf4j
/** Factory class responsible for creating and configuring WireMockConfiguration. */
public class WireMockConfigurationFactory {

  private final WireMockProperties wireMockProperties;

  public WireMockConfigurationFactory(WireMockProperties wireMockProperties) {
    this.wireMockProperties = wireMockProperties;
  }

  @Bean
  @Order(50)
  /**
   * Creates and configures a WireMockConfiguration bean based on the application's
   * WireMockProperties. This configuration includes setting up the port, enabling logging, and
   * configuring other WireMock server settings.
   *
   * @return a configured WireMockConfiguration instance
   */
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
    // templatingEnabled and proxyPassThrough are primitive booleans on properties;
    // call directly
    wireMockConfiguration.templatingEnabled(wireMockProperties.isTemplatingEnabled());
    Optional.ofNullable(wireMockProperties.getMappingsClassPath())
        .ifPresent(v -> wireMockConfiguration.usingFilesUnderClasspath(v));
    wireMockConfiguration.proxyPassThrough(wireMockProperties.isProxyPassThrough());

    log.trace("Registering Faker Extension org.wiremock.RandomExtension...");
    wireMockConfiguration.extensions(new String[] {"org.wiremock.RandomExtension"});
    wireMockConfiguration.trustAllProxyTargets(true);

    return wireMockConfiguration;
  }
}
