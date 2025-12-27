package net.snytkine.springboot.wiremock_middleware;

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
import lombok.extern.slf4j.Slf4j;
import net.snytkine.springboot.wiremock_middleware.model.WireMockProperties;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * HTTP request interceptor that integrates WireMock mocking capabilities into Spring's
 * RestTemplate.
 *
 * <p>This interceptor intercepts outgoing HTTP requests and attempts to match them against
 * configured WireMock stubs. If a matching stub is found, the mocked response is returned;
 * otherwise, the request is passed through to the actual HTTP server.
 *
 * <p>Key Features:
 *
 * <ul>
 *   <li>Transparent request interception at the Spring ClientHttpRequestInterceptor level
 *   <li>Automatic WireMock stub matching without modifying request execution flow
 *   <li>Optional response header injection to identify mock responses
 *   <li>Seamless conversion between Spring and WireMock request/response formats
 * </ul>
 *
 * <p>Usage: The interceptor is instantiated with a {@link WireMockConfiguration} and {@link
 * WireMockProperties}. It automatically initializes a WireMock server with a direct call HTTP
 * server factory, allowing in-process stub matching without network I/O.
 *
 * <p>Mock Identification: When a mock response is returned, an optional header can be added to
 * identify the response as originating from the mock middleware. This is controlled by the {@code
 * mockResponseHeader} and {@code mockResponseHeaderValue} properties.
 *
 * @see org.springframework.http.client.ClientHttpRequestInterceptor
 * @see com.github.tomakehurst.wiremock.core.WireMockConfiguration
 * @see WireMockProperties
 */
@Slf4j
public class WMInterceptor implements ClientHttpRequestInterceptor {

  private final DirectCallHttpServer directCallHttpServer;
  private final WireMockProperties properties;

  /**
   * Constructs a new {@code WireMockInterceptor} with the specified WireMock configuration and
   * properties.
   *
   * <p>This constructor initializes a {@link DirectCallHttpServer} to enable in-process request
   * interception and matching against WireMock stubs. It also configures the interceptor with
   * properties such as mock response headers and other configuration options.
   *
   * @param config the WireMock configuration used to set up the underlying WireMock server
   * @param properties the configuration properties for the WireMock interceptor, including mock
   *     response header settings
   * @throws IllegalArgumentException if either {@code config} or {@code properties} is null
   */
  public WMInterceptor(WireMockConfiguration wireMockConfiguration, WireMockProperties properties) {
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
    log.trace("Entered intercept");
    Request wiremockRequest = new SpringHttpRequestAdapter(request, body);

    com.github.tomakehurst.wiremock.http.Response wiremockResponse =
        directCallHttpServer.stubRequest(wiremockRequest);

    if (wiremockResponse.wasConfigured()) {
      log.trace("Returning mock response");
      var ret = new WiremockClientHttpResponse(wiremockResponse);
      String mockKey = properties.getMockResponseHeader();
      String mockHeaderValue =
          java.util.Objects.requireNonNullElse(
              properties.getMockResponseHeaderValue(), "mock-middleware");
      if (mockKey != null) {
        log.trace("Adding mock header {}={}", mockKey, mockHeaderValue);
        ret.setHeader(mockKey, mockHeaderValue);
      }
      return ret;
    }

    log.trace("Returning real response");
    return execution.execute(request, body);
  }

  /**
   * Adapts a Spring {@link HttpRequest} to a WireMock {@link Request} for compatibility.
   *
   * <p>This adapter translates Spring's HTTP request representation into WireMock's internal
   * request model, enabling seamless integration with WireMock's stub matching logic.
   *
   * <p>It handles conversion of:
   *
   * <ul>
   *   <li>HTTP method
   *   <li>URI and query parameters
   *   <li>Headers
   *   <li>Body content
   * </ul>
   */
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

    /**
     * Constructs a new SpringHttpRequestAdapter to convert a Spring HttpRequest into a WireMock
     * Request.
     *
     * @param springRequest the Spring HttpRequest to be adapted
     * @param body the body of the request as a byte array
     */
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

  /**
   * Adapter implementation of {@link ClientHttpResponse} that wraps a WireMock HTTP response.
   *
   * <p>This class converts WireMock's response format to Spring's {@link ClientHttpResponse}
   * interface, allowing WireMock responses to be used seamlessly within Spring's HTTP client
   * framework.
   *
   * <p>The adapter handles:
   *
   * <ul>
   *   <li>HTTP status codes and status messages
   *   <li>Response headers conversion from WireMock format to Spring HttpHeaders
   *   <li>Response body as an InputStream
   * </ul>
   *
   * <p>Headers from the WireMock response are copied into a Spring {@link HttpHeaders} instance
   * during construction. Additional headers can be set using {@link #setHeader(String, String)}.
   *
   * <p>The response body is wrapped in a {@link ByteArrayInputStream} for compatibility with the
   * {@link ClientHttpResponse} contract. If the WireMock response body is null, an empty byte array
   * is used instead.
   *
   * @see org.springframework.http.client.ClientHttpResponse
   * @see com.github.tomakehurst.wiremock.http.Response
   */
  private static class WiremockClientHttpResponse implements ClientHttpResponse {
    private final com.github.tomakehurst.wiremock.http.Response wiremockResponse;

    @NonNull private org.springframework.http.HttpHeaders ownHeaders;

    public void setHeader(@NonNull String key, String value) {
      this.ownHeaders.set(key, value);
    }

    public WiremockClientHttpResponse(
        com.github.tomakehurst.wiremock.http.Response wiremockResponse) {
      this.wiremockResponse = wiremockResponse;
      this.ownHeaders = new org.springframework.http.HttpHeaders();
      if (wiremockResponse.getHeaders() != null) {
        for (HttpHeader header : wiremockResponse.getHeaders().all()) {
          String key = header.key();
          java.util.List<String> values = header.values();
          if (key != null && values != null) {
            ownHeaders.addAll(key, values);
          }
        }
      }
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
      return ownHeaders;
    }
  }
}
