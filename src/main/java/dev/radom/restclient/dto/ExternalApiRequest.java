package dev.radom.restclient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for sending requests to external APIs.
 *
 * <p>This class represents the payload sent in POST/PUT/PATCH requests when creating or updating
 * resources in the external API.
 *
 * <h3>Why use DTOs?</h3>
 *
 * <ul>
 *   <li><b>Separation of Concerns:</b> API contract separated from domain models
 *   <li><b>Validation:</b> Can add validation annotations (@NotNull, @Email, etc.)
 *   <li><b>Flexibility:</b> API changes don't affect internal domain model
 *   <li><b>Security:</b> Control exactly what data is exposed/accepted
 * </ul>
 *
 * <h3>Lombok Annotations Explained:</h3>
 *
 * <ul>
 *   <li><b>@Data:</b> Generates getters, setters, toString, equals, and hashCode
 *   <li><b>@Builder:</b> Enables fluent builder pattern
 *       (ExternalApiRequest.builder().name("John").build())
 *   <li><b>@NoArgsConstructor:</b> Creates no-argument constructor (required for JSON
 *       deserialization)
 *   <li><b>@AllArgsConstructor:</b> Creates constructor with all fields (works with @Builder)
 * </ul>
 *
 * <h3>JSON Serialization Example:</h3>
 *
 * <pre>
 * ExternalApiRequest request = ExternalApiRequest.builder()
 *     .name("John Doe")
 *     .email("john@example.com")
 *     .message("Hello World")
 *     .build();
 *
 * // Automatically becomes:
 * {
 *   "name": "John Doe",
 *   "email": "john@example.com",
 *   "message": "Hello World"
 * }
 * </pre>
 *
 * @author RestClient Implementation
 * @version 1.0
 * @since 2024-12-14
 */
@Data // Generates: getters, setters, toString(), equals(), hashCode()
@Builder // Enables: ExternalApiRequest.builder().name("John").build()
@NoArgsConstructor // Required for Jackson JSON deserialization
@AllArgsConstructor // Required for @Builder to work with all fields
public class ExternalApiRequest {

  /**
   * Name of the person or entity.
   *
   * <p>Example: "John Doe"
   *
   * <p>Can add validation: @NotBlank(message = "Name is required")
   */
  private String name;

  /**
   * Email address.
   *
   * <p>Example: "john.doe@example.com"
   *
   * <p>Can add validation: @Email(message = "Invalid email format")
   */
  private String email;

  /**
   * Message or description.
   *
   * <p>Example: "This is a test message"
   *
   * <p>Can add validation: @Size(max = 500, message = "Message too long")
   */
  private String message;
}
