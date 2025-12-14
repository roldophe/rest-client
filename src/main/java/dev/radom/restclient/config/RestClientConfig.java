package dev.radom.restclient.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * Configuration class for RestClient setup.
 *
 * <p>This class configures the Spring RestClient bean that will be used throughout the application
 * to make HTTP calls to external APIs. RestClient is the modern, fluent API replacement for
 * RestTemplate in Spring Boot 3+.
 *
 * <h3>Why use this configuration?</h3>
 *
 * <ul>
 *   <li>Centralizes all HTTP client configuration in one place
 *   <li>Makes timeout values configurable via application.properties
 *   <li>Sets default headers for all requests automatically
 *   <li>Provides a reusable RestClient bean across the application
 * </ul>
 *
 * <h3>Benefits:</h3>
 *
 * <ul>
 *   <li>No need to configure RestClient in every service class
 *   <li>Easy to change API endpoints without code changes (via properties)
 *   <li>Timeout protection prevents hanging requests
 *   <li>Default headers reduce boilerplate code in service methods
 * </ul>
 *
 * @author RestClient Implementation
 * @version 1.0
 * @since 2024-12-14
 */
@Configuration
public class RestClientConfig {

  /**
   * Base URL for the external API.
   *
   * <p>Injected from application.properties using the key 'external.api.base-url'. If not
   * specified, defaults to JSONPlaceholder API for testing purposes.
   *
   * <p><b>Why:</b> Allows changing API endpoints without recompiling code. Different environments
   * (dev, staging, prod) can use different URLs.
   *
   * <p><b>Example:</b> https://api.example.com or http://localhost:3000
   */
  @Value("${external.api.base-url:https://jsonplaceholder.typicode.com}")
  private String baseUrl;

  /**
   * Connection timeout in milliseconds.
   *
   * <p>Maximum time to wait when establishing a connection to the external API. Currently not used
   * but available for future implementation.
   *
   * <p><b>Why:</b> Prevents application from hanging if the external API is unreachable.
   *
   * <p><b>Default:</b> 5000ms (5 seconds)
   */
  @Value("${external.api.connect-timeout:5000}")
  private int connectTimeout;

  /**
   * Read timeout in milliseconds.
   *
   * <p>Maximum time to wait for the external API to send data after connection is established. If
   * the API takes longer than this to respond, a timeout exception will be thrown.
   *
   * <p><b>Why:</b> Prevents requests from hanging indefinitely if the API is slow or stuck.
   * Protects application resources and provides better user experience.
   *
   * <p><b>Default:</b> 5000ms (5 seconds)
   */
  @Value("${external.api.read-timeout:5000}")
  private int readTimeout;

  /**
   * Creates and configures the RestClient bean.
   *
   * <p>This is the main HTTP client used throughout the application. It's configured with:
   *
   * <ul>
   *   <li>Base URL - All requests will be relative to this URL
   *   <li>Request Factory - Handles low-level HTTP connection details and timeouts
   *   <li>Default Headers - Automatically added to every request
   * </ul>
   *
   * <h3>Why use RestClient instead of RestTemplate?</h3>
   *
   * <ul>
   *   <li><b>Modern API:</b> RestClient is the successor to RestTemplate (which is in maintenance
   *       mode)
   *   <li><b>Fluent Interface:</b> More readable and chainable method calls
   *   <li><b>Better Error Handling:</b> Improved exception handling and status code checking
   *   <li><b>Type Safety:</b> Better support for generics and type-safe responses
   * </ul>
   *
   * <h3>Benefits of this configuration:</h3>
   *
   * <ul>
   *   <li><b>Base URL:</b> No need to specify full URL in every request, just the endpoint path
   *   <li><b>Default Headers:</b> Content-Type and Accept headers automatically added to all
   *       requests
   *   <li><b>Timeouts:</b> Built-in protection against slow or hanging requests
   *   <li><b>Reusability:</b> Single bean injected wherever needed via @Autowired
   * </ul>
   *
   * <h3>Example Usage:</h3>
   *
   * <pre>
   * // Instead of:
   * restClient.get().uri("https://api.example.com/users/1")
   *
   * // You can write:
   * restClient.get().uri("/users/1")  // Base URL is automatically prepended
   * </pre>
   *
   * @return Configured RestClient instance ready for dependency injection
   */
  @Bean
  public RestClient restClient() {
    return RestClient.builder()
        // Base URL: All requests will be relative to this URL
        // Example: If baseUrl = "https://api.example.com",
        //          then .uri("/users") becomes "https://api.example.com/users"
        .baseUrl(baseUrl)

        // Request Factory: Handles connection pooling, timeouts, and low-level HTTP details
        // Using our custom factory with configured read timeout
        .requestFactory(clientHttpRequestFactory())

        // Default Header: Content-Type tells the server what format we're sending
        // application/json means we're sending JSON data in request body
        .defaultHeader("Content-Type", "application/json")

        // Default Header: Accept tells the server what format we expect back
        // application/json means we want JSON responses
        .defaultHeader("Accept", "application/json")

        // Build the configured RestClient instance
        .build();
  }

  /**
   * Creates the HTTP request factory with timeout configuration.
   *
   * <p>The request factory is responsible for creating the actual HTTP connections to external
   * APIs. It handles low-level details like:
   *
   * <ul>
   *   <li>Opening TCP connections
   *   <li>Managing connection pools
   *   <li>Enforcing timeout limits
   *   <li>Handling SSL/TLS connections
   * </ul>
   *
   * <h3>Why use JdkClientHttpRequestFactory?</h3>
   *
   * <ul>
   *   <li><b>No External Dependencies:</b> Uses Java's built-in HttpClient (Java 11+)
   *   <li><b>Modern Implementation:</b> Leverages the new java.net.http package
   *   <li><b>HTTP/2 Support:</b> Supports modern HTTP protocols
   *   <li><b>Lightweight:</b> No need to add Apache HttpClient or other libraries
   * </ul>
   *
   * <h3>Alternative Factories:</h3>
   *
   * <ul>
   *   <li><b>SimpleClientHttpRequestFactory:</b> Basic implementation, older Java versions
   *   <li><b>HttpComponentsClientHttpRequestFactory:</b> Apache HttpClient, more features but
   *       external dependency
   *   <li><b>JdkClientHttpRequestFactory:</b> Modern Java HttpClient (used here)
   * </ul>
   *
   * <h3>Timeout Benefits:</h3>
   *
   * <ul>
   *   <li><b>Read Timeout:</b> Prevents waiting forever for slow APIs
   *   <li><b>Resource Protection:</b> Frees up threads and connections when timeout occurs
   *   <li><b>Fast Failure:</b> Application fails fast instead of hanging
   *   <li><b>User Experience:</b> Users get error message instead of indefinite loading
   * </ul>
   *
   * <h3>How it works:</h3>
   *
   * <pre>
   * // If external API takes longer than 5 seconds to respond:
   * 1. Read timeout expires (5000ms)
   * 2. ResourceAccessException is thrown
   * 3. GlobalExceptionHandler catches it
   * 4. Returns 503 Service Unavailable to client
   * </pre>
   *
   * @return Configured ClientHttpRequestFactory with timeout settings
   */
  @Bean
  public ClientHttpRequestFactory clientHttpRequestFactory() {
    // Create factory using Java's built-in HttpClient
    JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory();

    // Set read timeout: Maximum time to wait for API response
    // If API doesn't respond within this time, request fails with timeout exception
    // Benefits: Prevents hanging, better resource management, faster error detection
    factory.setReadTimeout(Duration.ofMillis(readTimeout));

    return factory;
  }
}
