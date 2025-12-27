package net.snytkine.springboot.wiremock_middleware;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import net.snytkine.springboot.wiremock_middleware.model.WireMockProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
/**
 * Configuration class responsible for creating and configuring WireMock interceptors. This class
 * sets up the necessary components for integrating WireMock into the Spring Boot application to
 * enable mocking of external HTTP services during testing and development.
 */
public class WMInterceptorFactory {

  @Bean
  @Order(50)
  /**
   * Creates and configures a WireMockInterceptor bean that handles HTTP request interception and
   * response mocking for integration testing purposes. Bean name will be "wmInterceptor"
   *
   * @param wireMockProperties the configuration properties for WireMock
   * @param wireMockConfiguration the WireMock configuration instance
   * @return a configured WireMockInterceptor instance
   */
  public WMInterceptor wmInterceptor(
      WireMockConfiguration wireMockConfiguration, WireMockProperties properties) {
    return new WMInterceptor(wireMockConfiguration, properties);
  }
}
