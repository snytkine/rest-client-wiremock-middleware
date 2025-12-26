# WireMock Middleware for Spring Boot

## Project Overview

This is a Spring Boot auto-configuration module that provides WireMock integration for testing HTTP clients. It allows developers to intercept outgoing HTTP requests from Spring's RestTemplate and mock responses using WireMock stubs, without requiring actual network calls. This enables fast, reliable testing of external service integrations.

### Key Technologies Used
- **Spring Boot**: Core framework for dependency injection and auto-configuration
- **WireMock**: HTTP mocking library for creating stub responses  
- **Java 8+**: Language and standard libraries
- **Lombok**: For reducing boilerplate code in model classes

### Architecture
The middleware works as a `ClientHttpRequestInterceptor` that sits between Spring's RestTemplate and external HTTP services. During intercept, requests are matched against WireMock stubs; if a match is found, the mock response is returned; otherwise, the request continues to the actual service.

## Getting Started

### Prerequisites
- Java 8 or higher
- Maven or Gradle build system  
- Spring Boot application

### Installation
Add this dependency to your `pom.xml`:
```xml
<dependency>
    <groupId>net.snytkine.springboot</groupId>
    <artifactId>wiremock-middleware</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Basic Usage
Enable the middleware in your `application.properties` or `application.yml`:
```yaml
net.snytkine.rest-client-wiremock-interceptor.enabled: true
```

The interceptor will automatically be registered with the Spring context and will intercept all HTTP requests made through RestTemplate.

### Running Tests
To run tests, use one of the following commands:
- For Maven:
  ```bash
  ./mvnw test
  ```
- For Gradle:
  ```bash
  ./gradlew test
  ```

## Configuration Properties

You can customize the behavior of the middleware using properties in `application.properties` or `application.yml`. Here are some common properties:

```yaml
net.snytkine.rest-client-wiremock-interceptor:
  enabled: true
  mock-response-header: X-Mock-Response
  mock-response-header-value: wiremock-middleware
  container-threads: 4
  root-directory: /tmp/wiremock
  templating-enabled: true
  proxy-pass-through: false
```

### Optional Dependency: WireMock Faker Extension

This project can use an optional dependency `wiremock-faker-extension` to add extra functionality that can be used inside the mapping files. To include this extension, add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.github.tomakehurst</groupId>
    <artifactId>wiremock-faker-extension</artifactId>
    <version>1.0.0</version>
</dependency>
```

With this extension, you can use Faker to generate random data in your mapping files.

### Example Mapping File

Here's an example of a WireMock mapping file (`example.json`):

```json
{
  "request": {
    "method": "GET",
    "urlPath": "/api/example"
  },
  "response": {
    "status": 200,
    "body": "{\"message\": \"Hello, World!\"}",
    "headers": {
      "Content-Type": "application/json"
    }
  }
}
```

### Troubleshooting
- **Interceptor not working**: Ensure `enabled=true` is set in properties and the interceptor bean is created.
- **No mock responses**: Verify that mappings exist and are correctly formatted.
- **Performance issues**: Adjust `containerThreads` for multi-threaded scenarios.

By following these steps, you can effectively use this package to intercept and mock HTTP requests in your Spring Boot application.