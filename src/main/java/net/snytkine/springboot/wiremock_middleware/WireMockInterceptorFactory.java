package net.snytkine.springboot.wiremock_middleware;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import net.snytkine.springboot.wiremock_middleware.model.WireMockProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WireMockInterceptorFactory {

  @Bean
  public WireMockInterceptor wireMockInterceptor(
      WireMockConfiguration wireMockConfiguration, WireMockProperties properties) {
    return new WireMockInterceptor(wireMockConfiguration, properties);
  }
}
