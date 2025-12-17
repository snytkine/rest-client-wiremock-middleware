package net.snytkine.springboot.wiremock_middleware.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class WireMockPropertiesTest {

  @Test
  void defaults() {
    WireMockProperties p = new WireMockProperties();
    assertFalse(p.isEnabled());
    assertEquals(1, p.getContainerThreads());
    assertFalse(p.getAsynchronousResponseEnabled());
    assertNull(p.getAsynchronousResponseThreads());
    assertNull(p.getRootDirectory());
    assertNull(p.getJournalDisabled());
    assertNull(p.getMaxRequestJournalEntries());
    assertNull(p.getGzipDisabled());
    assertFalse(p.getDisableOptimizeXmlFactories());
    assertFalse(p.getStubCorsEnabled());
    assertNull(p.getStubRequestLoggingDisabled());
    assertNull(p.getMaxTemplateCacheEntries());
    assertFalse(p.getGlobalTemplating());
    assertFalse(p.isTemplatingEnabled());
    assertNull(p.getMappingsClassPath());
    assertFalse(p.isProxyPassThrough());
    assertNull(p.getMockResponseHeader());
    assertNull(p.getMockResponseHeaderValue());
  }

  @Test
  void settersAndGetters() {
    WireMockProperties p = new WireMockProperties();
    p.setEnabled(true);
    p.setContainerThreads(5);
    p.setAsynchronousResponseEnabled(true);
    p.setAsynchronousResponseThreads(3);
    p.setRootDirectory("/tmp");
    p.setJournalDisabled(true);
    p.setMaxRequestJournalEntries(123);
    p.setGzipDisabled(true);
    p.setDisableOptimizeXmlFactories(true);
    p.setStubCorsEnabled(true);
    p.setStubRequestLoggingDisabled(true);
    p.setMaxTemplateCacheEntries(100L);
    p.setGlobalTemplating(true);
    p.setTemplatingEnabled(true);
    p.setMappingsClassPath("mappings");
    p.setProxyPassThrough(true);
    p.setMockResponseHeader("X-MOCK");
    p.setMockResponseHeaderValue("value");

    assertTrue(p.isEnabled());
    assertEquals(5, p.getContainerThreads());
    assertTrue(p.getAsynchronousResponseEnabled());
    assertEquals(3, p.getAsynchronousResponseThreads());
    assertEquals("/tmp", p.getRootDirectory());
    assertTrue(p.getJournalDisabled());
    assertEquals(123, p.getMaxRequestJournalEntries());
    assertTrue(p.getGzipDisabled());
    assertTrue(p.getDisableOptimizeXmlFactories());
    assertTrue(p.getStubCorsEnabled());
    assertTrue(p.getStubRequestLoggingDisabled());
    assertEquals(100L, p.getMaxTemplateCacheEntries());
    assertTrue(p.getGlobalTemplating());
    assertTrue(p.isTemplatingEnabled());
    assertEquals("mappings", p.getMappingsClassPath());
    assertTrue(p.isProxyPassThrough());
    assertEquals("X-MOCK", p.getMockResponseHeader());
    assertEquals("value", p.getMockResponseHeaderValue());
  }

  @Test
  void equalsHashCodeAndToString() {
    WireMockProperties a = new WireMockProperties();
    WireMockProperties b = new WireMockProperties();

    a.setMockResponseHeader("h");
    a.setMockResponseHeaderValue("v");

    b.setMockResponseHeader("h");
    b.setMockResponseHeaderValue("v");

    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());
    String s = a.toString();
    assertTrue(s.contains("mockResponseHeader=h") || s.contains("mockResponseHeader= h"));
    assertTrue(s.contains("mockResponseHeaderValue=v") || s.contains("mockResponseHeaderValue= v"));
  }
}
