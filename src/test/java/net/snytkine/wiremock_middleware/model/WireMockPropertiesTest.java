package net.snytkine.wiremock_middleware.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class WireMockPropertiesTest {

  @Test
  void defaults() {
    WireMockProperties p = new WireMockProperties();
    assertFalse(p.isEnabled());
    assertEquals(Integer.valueOf(1), p.getContainerThreads());
    assertEquals(Boolean.FALSE, p.getAsynchronousResponseEnabled());
    assertFalse(p.isTemplatingEnabled());
    assertNull(p.getRootDirectory());
    assertNull(p.getMockResponseHeader());
  }

  @Test
  void gettersAndSetters() {
    WireMockProperties p = new WireMockProperties();
    p.setEnabled(true);
    p.setContainerThreads(4);
    p.setAsynchronousResponseEnabled(true);
    p.setAsynchronousResponseThreads(2);
    p.setRootDirectory("/tmp/wm");
    p.setJournalDisabled(true);
    p.setMaxRequestJournalEntries(100);
    p.setGzipDisabled(true);
    p.setDisableOptimizeXmlFactories(true);
    p.setStubCorsEnabled(true);
    p.setStubRequestLoggingDisabled(true);
    p.setMaxTemplateCacheEntries(50L);
    p.setGlobalTemplating(true);
    p.setTemplatingEnabled(true);
    p.setMappingsClassPath("mappings");
    p.setProxyPassThrough(true);
    p.setMockResponseHeader("X-MOCK");

    assertTrue(p.isEnabled());
    assertEquals(Integer.valueOf(4), p.getContainerThreads());
    assertEquals(Boolean.TRUE, p.getAsynchronousResponseEnabled());
    assertEquals(Integer.valueOf(2), p.getAsynchronousResponseThreads());
    assertEquals("/tmp/wm", p.getRootDirectory());
    assertTrue(Boolean.TRUE.equals(p.getJournalDisabled()));
    assertEquals(Integer.valueOf(100), p.getMaxRequestJournalEntries());
    assertTrue(Boolean.TRUE.equals(p.getGzipDisabled()));
    assertTrue(Boolean.TRUE.equals(p.getDisableOptimizeXmlFactories()));
    assertTrue(Boolean.TRUE.equals(p.getStubCorsEnabled()));
    assertTrue(Boolean.TRUE.equals(p.getStubRequestLoggingDisabled()));
    assertEquals(Long.valueOf(50L), p.getMaxTemplateCacheEntries());
    assertTrue(Boolean.TRUE.equals(p.getGlobalTemplating()));
    assertTrue(p.isTemplatingEnabled());
    assertEquals("mappings", p.getMappingsClassPath());
    assertTrue(p.isProxyPassThrough());
    assertEquals("X-MOCK", p.getMockResponseHeader());
  }
}
