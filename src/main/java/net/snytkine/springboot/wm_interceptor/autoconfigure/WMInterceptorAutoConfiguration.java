package net.snytkine.springboot.wm_interceptor.autoconfigure;

import net.snytkine.springboot.wm_interceptor.WMInterceptorFactory;
import net.snytkine.springboot.wm_interceptor.WireMockConfigurationFactory;
import net.snytkine.springboot.wm_interceptor.model.WireMockProperties;
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
@Import({WireMockConfigurationFactory.class, WMInterceptorFactory.class})
/** Auto-configuration class for setting up WireMock interceptor. */
public class WMInterceptorAutoConfiguration {}
