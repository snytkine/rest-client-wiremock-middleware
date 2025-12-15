package net.snytkine.wiremock_middleware.middleware;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import net.snytkine.wiremock_middleware.model.WireMockProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WireMockMiddlewareFactory {

  @Bean
  public WireMockMiddleware createWireMockMiddleware(
      WireMockConfiguration wireMockConfiguration, WireMockProperties properties) {
    return new WireMockMiddleware(wireMockConfiguration, properties);
  }
}
