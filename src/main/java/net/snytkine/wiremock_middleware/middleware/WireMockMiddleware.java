package net.snytkine.wiremock_middleware.middleware;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.direct.DirectCallHttpServer;
import com.github.tomakehurst.wiremock.direct.DirectCallHttpServerFactory;
import com.github.tomakehurst.wiremock.http.ContentTypeHeader;
import com.github.tomakehurst.wiremock.http.Cookie;
import com.github.tomakehurst.wiremock.http.FormParameter;
import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.HttpHeaders;
import com.github.tomakehurst.wiremock.http.QueryParameter;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.*;
import net.snytkine.wiremock_middleware.model.WireMockProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.util.UriComponentsBuilder;

public class WireMockMiddleware implements ClientHttpRequestInterceptor {

  private static final Logger log = LoggerFactory.getLogger(WireMockMiddleware.class);

  private final DirectCallHttpServer directCallHttpServer;
  private final WireMockProperties properties;

  public WireMockMiddleware(
      WireMockConfiguration wireMockConfiguration, WireMockProperties properties) {
    this.properties = properties;
    DirectCallHttpServerFactory wireMockServer = new DirectCallHttpServerFactory();
    wireMockConfiguration.httpServerFactory(wireMockServer);
    WireMockServer wm = new WireMockServer(wireMockConfiguration);
    wm.start(); // no-op, not required
    this.directCallHttpServer = wireMockServer.getHttpServer();
  }

  @Override
  public @NonNull ClientHttpResponse intercept(
      @NonNull HttpRequest request,
      @NonNull byte[] body,
      @NonNull ClientHttpRequestExecution execution)
      throws IOException {
    log.info("Entered intercept");
    Request wiremockRequest = new SpringHttpRequestAdapter(request, body);

    com.github.tomakehurst.wiremock.http.Response wiremockResponse =
        directCallHttpServer.stubRequest(wiremockRequest);

    if (wiremockResponse.wasConfigured()) {
      return new WiremockClientHttpResponse(wiremockResponse, properties);
    }

    return execution.execute(request, body);
  }

  private static class SpringHttpRequestAdapter implements Request {
    private final HttpRequest springRequest;
    private final byte[] body;
    private final Map<String, QueryParameter> queryParameters;

    private Map<String, QueryParameter> parseQueryParameters() {
      Map<String, QueryParameter> params = new HashMap<>();
      UriComponentsBuilder.fromUri(springRequest.getURI())
          .build()
          .getQueryParams()
          .forEach((key, values) -> params.put(key, new QueryParameter(key, values)));
      return Collections.unmodifiableMap(params);
    }

    public SpringHttpRequestAdapter(HttpRequest springRequest, byte[] body) {
      this.springRequest = springRequest;
      this.body = body;
      this.queryParameters = parseQueryParameters();
    }

    @Override
    public String getUrl() {
      String url = springRequest.getURI().getPath();
      if (springRequest.getURI().getQuery() != null) {
        url += "?" + springRequest.getURI().getQuery();
      }
      return url;
    }

    @Override
    public String getAbsoluteUrl() {
      return springRequest.getURI().toString();
    }

    @Override
    public RequestMethod getMethod() {
      return RequestMethod.fromString(springRequest.getMethod().name());
    }

    @Override
    public String getScheme() {
      return springRequest.getURI().getScheme();
    }

    @Override
    public String getHost() {
      return springRequest.getURI().getHost();
    }

    @Override
    public int getPort() {
      int port = springRequest.getURI().getPort();
      if (port == -1) {
        if ("http".equals(getScheme())) {
          return 80;
        } else if ("https".equals(getScheme())) {
          return 443;
        }
      }
      return port;
    }

    @Override
    public String getClientIp() {
      return "0.0.0.0"; // Not available in Spring's HttpRequest
    }

    @Override
    @SuppressWarnings("null")
    public @NonNull String getHeader(String key) {
      return java.util.Objects.requireNonNullElse(springRequest.getHeaders().getFirst(key), "");
    }

    @Override
    public ContentTypeHeader contentTypeHeader() {
      MediaType contentType = springRequest.getHeaders().getContentType();
      if (contentType == null) {
        return ContentTypeHeader.absent();
      }
      return new ContentTypeHeader(contentType.toString());
    }

    @Override
    public HttpHeaders getHeaders() {
      List<HttpHeader> httpHeaders = new ArrayList<>();
      springRequest
          .getHeaders()
          .forEach((key, values) -> httpHeaders.add(new HttpHeader(key, values)));
      return new HttpHeaders(httpHeaders);
    }

    @Override
    public boolean containsHeader(String key) {
      return springRequest.getHeaders().containsKey(key);
    }

    @Override
    public Set<String> getAllHeaderKeys() {
      Set<String> res = springRequest.getHeaders().keySet();
      return res;
    }

    @Override
    public QueryParameter queryParameter(String key) {
      return queryParameters.get(key);
    }

    @Override
    public byte[] getBody() {
      return body;
    }

    @Override
    public String getBodyAsString() {
      return new String(body, StandardCharsets.UTF_8);
    }

    @Override
    public String getBodyAsBase64() {
      return Base64.getEncoder().encodeToString(body);
    }

    @Override
    public boolean isMultipart() {
      MediaType contentType = springRequest.getHeaders().getContentType();
      return contentType != null && contentType.toString().startsWith("multipart/form-data");
    }

    @Override
    public Collection<Request.Part> getParts() {
      return Collections.emptyList();
    }

    @Override
    public Request.Part getPart(String name) {
      return null;
    }

    @Override
    public boolean isBrowserProxyRequest() {
      return false;
    }

    @Override
    public Optional<Request> getOriginalRequest() {
      return Optional.empty();
    }

    @Override
    public FormParameter formParameter(String arg0) {
      return null;
    }

    @Override
    public Map<String, FormParameter> formParameters() {
      return null;
    }

    @Override
    public Map<String, Cookie> getCookies() {
      return new HashMap<>();
    }

    @Override
    public String getProtocol() {
      return "https";
    }

    @Override
    @SuppressWarnings("null")
    public @NonNull HttpHeader header(String arg0) {
      String myHeader =
          java.util.Objects.requireNonNullElse(springRequest.getHeaders().getFirst(arg0), "");
      if (!myHeader.isEmpty()) {
        return new HttpHeader(arg0, List.of(myHeader));
      }
      return new HttpHeader(arg0, Collections.emptyList());
    }
  }

  private static class WiremockClientHttpResponse implements ClientHttpResponse {
    private final com.github.tomakehurst.wiremock.http.Response wiremockResponse;
    private WireMockProperties configuration;

    public WiremockClientHttpResponse(
        com.github.tomakehurst.wiremock.http.Response wiremockResponse,
        WireMockProperties configuration) {
      this.wiremockResponse = wiremockResponse;
      this.configuration = configuration;
    }

    @Override
    public @NonNull HttpStatusCode getStatusCode() throws IOException {
      return HttpStatusCode.valueOf(wiremockResponse.getStatus());
    }

    @Override
    @SuppressWarnings("null")
    public @NonNull String getStatusText() throws IOException {
      return java.util.Objects.requireNonNullElse(wiremockResponse.getStatusMessage(), "");
    }

    @Override
    public void close() {
      // no-op
    }

    @Override
    public @NonNull InputStream getBody() throws IOException {
      byte[] b = wiremockResponse.getBody();
      if (b == null) {
        b = new byte[0];
      }
      return new ByteArrayInputStream(b);
    }

    @Override
    public @NonNull org.springframework.http.HttpHeaders getHeaders() {
      org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
      if (wiremockResponse.getHeaders() != null) {
        for (HttpHeader header : wiremockResponse.getHeaders().all()) {
          String key = header.key();
          java.util.List<String> values = header.values();
          if (key != null && values != null) {
            headers.addAll(key, values);
          }
        }
      }
      String mockKey = configuration.getMockResponseHeader();
      if (mockKey != null) {
        headers.set(mockKey, "mocked-response");
      }

      return headers;
    }
  }
}
