package dev.radom.restclient.service;

import dev.radom.restclient.dto.ExternalApiRequest;
import dev.radom.restclient.dto.ExternalApiResponse;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * Service class for making HTTP calls to external APIs using Spring's RestClient.
 *
 * <p>This service demonstrates comprehensive RestClient usage patterns including:
 *
 * <ul>
 *   <li>GET requests (simple, with path variables, with query parameters)
 *   <li>POST requests (JSON body, form data)
 *   <li>PUT requests (full resource updates)
 *   <li>PATCH requests (partial resource updates)
 *   <li>DELETE requests
 *   <li>Custom header handling
 *   <li>Status code handling
 *   <li>Full response access (status, headers, body)
 * </ul>
 *
 * <h3>Why use a separate service layer?</h3>
 *
 * <ul>
 *   <li><b>Separation of Concerns:</b> Business logic separated from HTTP/presentation layer
 *   <li><b>Reusability:</b> Service methods can be called from multiple controllers
 *   <li><b>Testability:</b> Easier to unit test business logic independently
 *   <li><b>Maintainability:</b> Changes to API calls don't affect controller code
 * </ul>
 *
 * <h3>Logging with @Slf4j:</h3>
 *
 * <p>Lombok's @Slf4j annotation automatically creates a log field, enabling logging of all API
 * calls for debugging, monitoring, and audit purposes.
 *
 * <h3>Dependency Injection with @RequiredArgsConstructor:</h3>
 *
 * <p>Lombok's @RequiredArgsConstructor creates a constructor for final fields, enabling clean
 * constructor injection of RestClient without boilerplate code.
 *
 * @author RestClient Implementation
 * @version 1.0
 * @since 2024-12-14
 */
@Slf4j // Lombok: Creates private static final Logger log =
// LoggerFactory.getLogger(ExternalApiService.class);
@Service // Spring: Marks this as a service component for dependency injection
@RequiredArgsConstructor // Lombok: Creates constructor with final fields (RestClient restClient)
public class ExternalApiService {

  /**
   * RestClient instance injected via constructor.
   *
   * <p>Configured in RestClientConfig with base URL, timeouts, and default headers. The 'final'
   * keyword ensures immutability and enables constructor injection.
   *
   * <p><b>Why final?</b> Prevents reassignment and enables @RequiredArgsConstructor to include it
   * in the generated constructor.
   */
  private final RestClient restClient;

  /**
   * Retrieves all resources from the external API using GET request.
   *
   * <p><b>HTTP Method:</b> GET
   *
   * <p><b>Endpoint:</b> /posts
   *
   * <p><b>Returns:</b> List of all resources
   *
   * <h3>Why use ParameterizedTypeReference?</h3>
   *
   * <p>When returning generic types like {@code List<ExternalApiResponse>}, Java's type erasure
   * makes it impossible to determine the actual type at runtime. ParameterizedTypeReference
   * preserves the generic type information, allowing RestClient to properly deserialize the JSON
   * array into a typed list.
   *
   * <h3>Benefits:</h3>
   *
   * <ul>
   *   <li><b>Type Safety:</b> Returns strongly-typed List instead of raw list
   *   <li><b>No Casting:</b> No need for manual type casting after retrieval
   *   <li><b>Compile-time Checking:</b> IDE can provide better autocomplete and error detection
   * </ul>
   *
   * <h3>Example Response:</h3>
   *
   * <pre>
   * [
   *   {"id": 1, "name": "John", "email": "john@example.com", ...},
   *   {"id": 2, "name": "Jane", "email": "jane@example.com", ...}
   * ]
   * </pre>
   *
   * @return List of ExternalApiResponse objects containing all resources
   * @throws org.springframework.web.client.HttpClientErrorException if API returns 4xx error
   * @throws org.springframework.web.client.HttpServerErrorException if API returns 5xx error
   * @throws org.springframework.web.client.ResourceAccessException if connection fails or times out
   */
  public List<ExternalApiResponse> getAllResources() {
    // Log the operation for debugging and audit purposes
    log.info("Fetching all resources from external API");

    // Fluent API chain:
    return restClient
        .get() // 1. Start GET request
        .uri("/posts") // 2. Set URI (baseUrl + /posts)
        .retrieve() // 3. Execute request and get response
        .body(new ParameterizedTypeReference<>() {}); // 4. Deserialize JSON array to
    // List<ExternalApiResponse>
  }

  /** GET request - Retrieve a single resource by ID */
  public ExternalApiResponse getResourceById(Long id) {
    log.info("Fetching resource with id: {}", id);

    return restClient.get().uri("/posts/{id}", id).retrieve().body(ExternalApiResponse.class);
  }

  /** GET request with query parameters */
  public List<ExternalApiResponse> getResourcesByUserId(Long userId) {
    log.info("Fetching resources for userId: {}", userId);

    return restClient
        .get()
        .uri(uriBuilder -> uriBuilder.path("/posts").queryParam("userId", userId).build())
        .retrieve()
        .body(new ParameterizedTypeReference<>() {});
  }

  /** POST request - Create a new resource */
  public ExternalApiResponse createResource(ExternalApiRequest request) {
    log.info("Creating new resource: {}", request);

    return restClient
        .post()
        .uri("/posts")
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .retrieve()
        .body(ExternalApiResponse.class);
  }

  /** PUT request - Update an existing resource */
  public ExternalApiResponse updateResource(Long id, ExternalApiRequest request) {
    log.info("Updating resource with id: {}", id);

    return restClient
        .put()
        .uri("/posts/{id}", id)
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .retrieve()
        .body(ExternalApiResponse.class);
  }

  /** PATCH request - Partially update a resource */
  public ExternalApiResponse partialUpdateResource(Long id, Map<String, Object> updates) {
    log.info("Partially updating resource with id: {}", id);

    return restClient
        .patch()
        .uri("/posts/{id}", id)
        .contentType(MediaType.APPLICATION_JSON)
        .body(updates)
        .retrieve()
        .body(ExternalApiResponse.class);
  }

  /** DELETE request - Delete a resource */
  public void deleteResource(Long id) {
    log.info("Deleting resource with id: {}", id);

    restClient.delete().uri("/posts/{id}", id).retrieve().toBodilessEntity();
  }

  /** GET request with custom headers */
  public ExternalApiResponse getResourceWithHeaders(Long id, Map<String, String> headers) {
    log.info("Fetching resource with id: {} and custom headers", id);

    RestClient.RequestHeadersUriSpec<?> spec = restClient.get();

    headers.forEach(spec::header);

    return spec.uri("/posts/{id}", id).retrieve().body(ExternalApiResponse.class);
  }

  /** POST request with form data */
  public ExternalApiResponse submitFormData(Map<String, String> formData) {
    log.info("Submitting form data");

    return restClient
        .post()
        .uri("/posts")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(formData)
        .retrieve()
        .body(ExternalApiResponse.class);
  }

  /** GET request with response status handling */
  public ExternalApiResponse getResourceWithStatusHandling(Long id) {
    log.info("Fetching resource with id: {} with status handling", id);

    return restClient
        .get()
        .uri("/posts/{id}", id)
        .retrieve()
        .onStatus(
            status -> status.value() == 404,
            (request, response) -> {
              log.error("Resource not found with id: {}", id);
              throw new RuntimeException("Resource not found");
            })
        .onStatus(
            status -> status.is5xxServerError(),
            (request, response) -> {
              log.error("Server error occurred");
              throw new RuntimeException("Server error");
            })
        .body(ExternalApiResponse.class);
  }

  /** GET request with exchange for full response access */
  public Map<String, Object> getResourceWithFullResponse(Long id) {
    log.info("Fetching resource with id: {} with full response", id);

    return restClient
        .get()
        .uri("/posts/{id}", id)
        .exchange(
            (request, response) -> {
              log.info("Response status: {}", response.getStatusCode());
              log.info("Response headers: {}", response.getHeaders());

              ExternalApiResponse body = response.bodyTo(ExternalApiResponse.class);

              return Map.of(
                  "status", response.getStatusCode().value(),
                  "headers", response.getHeaders(),
                  "body", body);
            });
  }
}
