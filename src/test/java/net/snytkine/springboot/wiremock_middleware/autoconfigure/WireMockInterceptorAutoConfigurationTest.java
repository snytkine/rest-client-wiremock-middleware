package net.snytkine.springboot.wiremock_middleware.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import net.snytkine.springboot.wiremock_middleware.WMInterceptor;
import net.snytkine.springboot.wiremock_middleware.WMInterceptorFactory;
import net.snytkine.springboot.wiremock_middleware.WireMockConfigurationFactory;
import net.snytkine.springboot.wiremock_middleware.model.WireMockProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class WireMockInterceptorAutoConfigurationTest {

  private final ApplicationContextRunner runner =
      new ApplicationContextRunner()
          .withConfiguration(
              AutoConfigurations.of(
                  net.snytkine.springboot.wiremock_middleware.autoconfigure
                      .WMInterceptorAutoConfiguration.class));

  @Test
  void whenPropertyEnabled_thenBeansCreated() {
    runner
        .withPropertyValues("net.snytkine.rest-client-wiremock-interceptor.enabled=true")
        .run(
            (context) -> {
              assertThat(context).hasSingleBean(WireMockProperties.class);
              assertThat(context).hasSingleBean(WireMockConfigurationFactory.class);
              assertThat(context).hasSingleBean(WMInterceptorFactory.class);
              assertThat(context).hasSingleBean(WMInterceptor.class);
            });
  }

  @Test
  void whenPropertyMissing_thenBeansNotCreated() {
    runner.run((context) -> assertThat(context).doesNotHaveBean(WMInterceptor.class));
  }
}
