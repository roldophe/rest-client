package dev.radom.restclient.controller;

import dev.radom.restclient.dto.ExternalApiRequest;
import dev.radom.restclient.dto.ExternalApiResponse;
import dev.radom.restclient.service.ExternalApiService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/external")
@RequiredArgsConstructor
public class ExternalApiController {

  private final ExternalApiService externalApiService;

  /** GET all resources Example: GET http://localhost:8080/api/v1/external/resources */
  @GetMapping("/resources")
  public ResponseEntity<List<ExternalApiResponse>> getAllResources() {
    log.info("REST request to get all resources");
    List<ExternalApiResponse> resources = externalApiService.getAllResources();
    return ResponseEntity.ok(resources);
  }

  /** GET resource by ID Example: GET http://localhost:8080/api/v1/external/resources/1 */
  @GetMapping("/resources/{id}")
  public ResponseEntity<ExternalApiResponse> getResourceById(@PathVariable Long id) {
    log.info("REST request to get resource by id: {}", id);
    ExternalApiResponse resource = externalApiService.getResourceById(id);
    return ResponseEntity.ok(resource);
  }

  /**
   * GET resources by user ID (query parameter) Example: GET
   * http://localhost:8080/api/v1/external/resources/search?userId=1
   */
  @GetMapping("/resources/search")
  public ResponseEntity<List<ExternalApiResponse>> getResourcesByUserId(
      @RequestParam(name = "userId") Long userId) {
    log.info("REST request to get resources by userId: {}", userId);
    List<ExternalApiResponse> resources = externalApiService.getResourcesByUserId(userId);
    return ResponseEntity.ok(resources);
  }

  /**
   * POST - Create new resource Example: POST http://localhost:8080/api/v1/external/resources Body:
   * {"name": "John", "email": "john@example.com", "message": "Hello"}
   */
  @PostMapping("/resources")
  public ResponseEntity<ExternalApiResponse> createResource(
      @RequestBody ExternalApiRequest request) {
    log.info("REST request to create resource: {}", request);
    ExternalApiResponse response = externalApiService.createResource(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * PUT - Update existing resource Example: PUT http://localhost:8080/api/v1/external/resources/1
   * Body: {"name": "John Updated", "email": "john.updated@example.com", "message": "Updated"}
   */
  @PutMapping("/resources/{id}")
  public ResponseEntity<ExternalApiResponse> updateResource(
      @PathVariable Long id, @RequestBody ExternalApiRequest request) {
    log.info("REST request to update resource with id: {}", id);
    ExternalApiResponse response = externalApiService.updateResource(id, request);
    return ResponseEntity.ok(response);
  }

  /**
   * PATCH - Partially update resource Example: PATCH
   * http://localhost:8080/api/v1/external/resources/1 Body: {"name": "John Partially Updated"}
   */
  @PatchMapping("/resources/{id}")
  public ResponseEntity<ExternalApiResponse> partialUpdateResource(
      @PathVariable Long id, @RequestBody Map<String, Object> updates) {
    log.info("REST request to partially update resource with id: {}", id);
    ExternalApiResponse response = externalApiService.partialUpdateResource(id, updates);
    return ResponseEntity.ok(response);
  }

  /** DELETE - Delete resource Example: DELETE http://localhost:8080/api/v1/external/resources/1 */
  @DeleteMapping("/resources/{id}")
  public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
    log.info("REST request to delete resource with id: {}", id);
    externalApiService.deleteResource(id);
    return ResponseEntity.noContent().build();
  }

  /**
   * GET resource with custom headers Example: GET
   * http://localhost:8080/api/v1/external/resources/1/with-headers Headers: X-Custom-Header:
   * CustomValue
   */
  @GetMapping("/resources/{id}/with-headers")
  public ResponseEntity<ExternalApiResponse> getResourceWithHeaders(
      @PathVariable Long id, @RequestHeader Map<String, String> headers) {
    log.info("REST request to get resource with id: {} and headers: {}", id, headers);
    ExternalApiResponse response = externalApiService.getResourceWithHeaders(id, headers);
    return ResponseEntity.ok(response);
  }

  /**
   * POST - Submit form data Example: POST http://localhost:8080/api/v1/external/resources/form
   * Content-Type: application/x-www-form-urlencoded Body: name=John&email=john@example.com
   */
  @PostMapping("/resources/form")
  public ResponseEntity<ExternalApiResponse> submitFormData(
      @RequestParam Map<String, String> formData) {
    log.info("REST request to submit form data: {}", formData);
    ExternalApiResponse response = externalApiService.submitFormData(formData);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * GET resource with status handling Example: GET
   * http://localhost:8080/api/v1/external/resources/1/with-status-handling
   */
  @GetMapping("/resources/{id}/with-status-handling")
  public ResponseEntity<ExternalApiResponse> getResourceWithStatusHandling(@PathVariable Long id) {
    log.info("REST request to get resource with id: {} with status handling", id);
    ExternalApiResponse response = externalApiService.getResourceWithStatusHandling(id);
    return ResponseEntity.ok(response);
  }

  /**
   * GET resource with full response Example: GET
   * http://localhost:8080/api/v1/external/resources/1/full-response
   */
  @GetMapping("/resources/{id}/full-response")
  public ResponseEntity<Map<String, Object>> getResourceWithFullResponse(@PathVariable Long id) {
    log.info("REST request to get resource with id: {} with full response", id);
    Map<String, Object> response = externalApiService.getResourceWithFullResponse(id);
    return ResponseEntity.ok(response);
  }
}
