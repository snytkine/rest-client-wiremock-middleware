package net.snytkine.wiremock_middleware.autoconfigure;

import net.snytkine.wiremock_middleware.middleware.WireMockConfigurationFactory;
import net.snytkine.wiremock_middleware.middleware.WireMockMiddlewareFactory;
import net.snytkine.wiremock_middleware.model.WireMockProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@EnableConfigurationProperties(WireMockProperties.class)
@ConditionalOnProperty(
    prefix = "net.snytkine.rest-client-middleware.wiremock",
    name = "enabled",
    havingValue = "true")
@Import({WireMockConfigurationFactory.class, WireMockMiddlewareFactory.class})
public class WireMockMiddlewareAutoConfiguration {}
