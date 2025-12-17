package net.snytkine.springboot.wiremock_middleware.autoconfigure;

import net.snytkine.springboot.wiremock_middleware.WireMockConfigurationFactory;
import net.snytkine.springboot.wiremock_middleware.WireMockInterceptorFactory;
import net.snytkine.springboot.wiremock_middleware.model.WireMockProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@EnableConfigurationProperties(WireMockProperties.class)
@ConditionalOnProperty(
    prefix = "net.snytkine.rest-client-wiremock-interceptor",
    name = "enabled",
    havingValue = "true")
@Import({WireMockConfigurationFactory.class, WireMockInterceptorFactory.class})
public class WireMockInterceptorAutoConfiguration {}
