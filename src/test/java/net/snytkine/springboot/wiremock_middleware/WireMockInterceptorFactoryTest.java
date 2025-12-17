package net.snytkine.springboot.wiremock_middleware;

import static org.junit.jupiter.api.Assertions.*;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import net.snytkine.springboot.wiremock_middleware.model.WireMockProperties;
import org.junit.jupiter.api.Test;

class WireMockInterceptorFactoryTest {

  @Test
  void createsInterceptor() {
    WireMockInterceptorFactory factory = new WireMockInterceptorFactory();

    WireMockConfiguration cfg = new WireMockConfiguration();
    WireMockProperties props = new WireMockProperties();

    WireMockInterceptor interceptor = factory.wireMockInterceptor(cfg, props);
    assertNotNull(interceptor);
  }

  @Test
  void wiresPropertiesIntoInterceptor() throws Exception {
    WireMockInterceptorFactory factory = new WireMockInterceptorFactory();

    WireMockConfiguration cfg = new WireMockConfiguration();
    WireMockProperties props = new WireMockProperties();
    props.setMockResponseHeader("X-FACTORY");

    WireMockInterceptor interceptor = factory.wireMockInterceptor(cfg, props);
    assertNotNull(interceptor);

    // verify the private 'properties' field references the same object
    var field = WireMockInterceptor.class.getDeclaredField("properties");
    field.setAccessible(true);
    Object contained = field.get(interceptor);
    assertSame(props, contained);

    // verify DirectCallHttpServer was created
    var serverField = WireMockInterceptor.class.getDeclaredField("directCallHttpServer");
    serverField.setAccessible(true);
    Object server = serverField.get(interceptor);
    assertNotNull(server);
  }
}
