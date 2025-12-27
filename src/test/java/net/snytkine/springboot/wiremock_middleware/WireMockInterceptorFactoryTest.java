package net.snytkine.springboot.wiremock_middleware;

import static org.junit.jupiter.api.Assertions.*;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import net.snytkine.springboot.wiremock_middleware.model.WireMockProperties;
import org.junit.jupiter.api.Test;

class WireMockInterceptorFactoryTest {

  @Test
  void createsInterceptor() {
    WMInterceptorFactory factory = new WMInterceptorFactory();

    WireMockConfiguration cfg = new WireMockConfiguration();
    WireMockProperties props = new WireMockProperties();

    WMInterceptor interceptor = factory.wmInterceptor(cfg, props);
    assertNotNull(interceptor);
  }

  @Test
  void wiresPropertiesIntoInterceptor() throws Exception {
    WMInterceptorFactory factory = new WMInterceptorFactory();

    WireMockConfiguration cfg = new WireMockConfiguration();
    WireMockProperties props = new WireMockProperties();
    props.setMockResponseHeader("X-FACTORY");

    WMInterceptor interceptor = factory.wmInterceptor(cfg, props);
    assertNotNull(interceptor);

    // verify the private 'properties' field references the same object
    var field = WMInterceptor.class.getDeclaredField("properties");
    field.setAccessible(true);
    Object contained = field.get(interceptor);
    assertSame(props, contained);

    // verify DirectCallHttpServer was created
    var serverField = WMInterceptor.class.getDeclaredField("directCallHttpServer");
    serverField.setAccessible(true);
    Object server = serverField.get(interceptor);
    assertNotNull(server);
  }
}
