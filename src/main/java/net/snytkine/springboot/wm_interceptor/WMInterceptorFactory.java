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
import net.snytkine.springboot.wm_interceptor.model.WireMockProperties;
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
   * @param wiremockConfiguration the configuration properties for WireMock
   * @param properties the WireMock properties object containing configuration. the WireMock
   *     configuration instance
   * @return a configured WireMockInterceptor instance
   */
  public WMInterceptor wmInterceptor(
      WireMockConfiguration wireMockConfiguration, WireMockProperties properties) {
    return new WMInterceptor(wireMockConfiguration, properties);
  }
}
