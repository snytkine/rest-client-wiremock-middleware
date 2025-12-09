package net.snytkine.wiremock_middleware.middleware;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import net.snytkine.wiremock_middleware.model.WireMockProperties;

import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WireMockConfigurationFactory {

        private final WireMockProperties wireMockProperties;

        public WireMockConfigurationFactory(WireMockProperties wireMockProperties) {
                this.wireMockProperties = wireMockProperties;
        }

        @Bean
        public WireMockConfiguration wireMockConfiguration() {
                WireMockConfiguration wireMockConfiguration = new WireMockConfiguration();

                Optional.ofNullable(wireMockProperties.getContainerThreads())
                                .ifPresent(wireMockConfiguration::containerThreads);
                Optional.ofNullable(wireMockProperties.getAsynchronousResponseEnabled())
                                .ifPresent(wireMockConfiguration::asynchronousResponseEnabled);
                Optional.ofNullable(wireMockProperties.getAsynchronousResponseThreads())
                                .ifPresent(wireMockConfiguration::asynchronousResponseThreads);
                Optional.ofNullable(wireMockProperties.getRootDirectory())
                                .ifPresent(wireMockConfiguration::usingFilesUnderDirectory);

                if (Boolean.TRUE.equals(wireMockProperties.getJournalDisabled())) {
                        wireMockConfiguration.disableRequestJournal();
                }

                Optional.ofNullable(wireMockProperties.getMaxRequestJournalEntries())
                                .ifPresent(wireMockConfiguration::maxRequestJournalEntries);
                Optional.ofNullable(wireMockProperties.getGzipDisabled())
                                .ifPresent(wireMockConfiguration::gzipDisabled);
                Optional.ofNullable(wireMockProperties.getDisableOptimizeXmlFactories())
                                .ifPresent(wireMockConfiguration::disableOptimizeXmlFactoriesLoading);
                Optional.ofNullable(wireMockProperties.getStubCorsEnabled())
                                .ifPresent(wireMockConfiguration::stubCorsEnabled);
                Optional.ofNullable(wireMockProperties.getStubRequestLoggingDisabled())
                                .ifPresent(wireMockConfiguration::stubRequestLoggingDisabled);
                Optional.ofNullable(wireMockProperties.isTemplatingEnabled())
                                .ifPresent(wireMockConfiguration::templatingEnabled);
                Optional.ofNullable(wireMockProperties.getMappingsClassPath())
                                .ifPresent(wireMockConfiguration::usingFilesUnderClasspath);
                Optional.ofNullable(wireMockProperties.isProxyPassThrough())
                                .ifPresent(wireMockConfiguration::proxyPassThrough);

                String[] myextensions = { "org.wiremock.RandomExtension" };

                wireMockConfiguration.trustAllProxyTargets(true);
                wireMockConfiguration.requestJournalDisabled();
                wireMockConfiguration.extensions(myextensions);

                return wireMockConfiguration;
        }
}
