# RestClient Implementation Guide

## Overview
This Spring Boot application demonstrates a complete RestClient implementation for calling external APIs with comprehensive features including:
- GET, POST, PUT, PATCH, DELETE operations
- Query parameters and path variables
- Custom headers
- Form data submission
- Error handling and exception management
- Full response access

## Project Structure

```
src/main/java/dev/radom/restclient/
├── config/
│   └── RestClientConfig.java          # RestClient configuration
├── controller/
│   └── ExternalApiController.java     # REST endpoints
├── service/
│   └── ExternalApiService.java        # Business logic with RestClient
├── dto/
│   ├── ExternalApiRequest.java        # Request DTO
│   ├── ExternalApiResponse.java       # Response DTO
│   └── ApiErrorResponse.java          # Error response DTO
└── exception/
    ├── ExternalApiException.java      # Custom API exception
    ├── ResourceNotFoundException.java # Resource not found exception
    └── GlobalExceptionHandler.java    # Global exception handler
```

## Configuration

### application.properties
```properties
# External API Configuration
external.api.base-url=https://jsonplaceholder.typicode.com
external.api.connect-timeout=5000
external.api.read-timeout=5000
```

You can override these values for different environments:
- `external.api.base-url`: Base URL of the external API
- `external.api.connect-timeout`: Connection timeout in milliseconds
- `external.api.read-timeout`: Read timeout in milliseconds

## Features Implemented

### 1. RestClient Configuration (RestClientConfig.java)
- Bean configuration for RestClient
- Configurable base URL and timeouts
- Default headers (Content-Type, Accept)
- Custom HTTP request factory

### 2. Service Layer (ExternalApiService.java)

#### GET Operations
- `getAllResources()` - Retrieve all resources
- `getResourceById(Long id)` - Get single resource by ID
- `getResourcesByUserId(Long userId)` - Get resources with query parameters
- `getResourceWithHeaders(Long id, Map<String, String> headers)` - GET with custom headers
- `getResourceWithStatusHandling(Long id)` - GET with response status handling
- `getResourceWithFullResponse(Long id)` - GET with full response access (status, headers, body)

#### POST Operations
- `createResource(ExternalApiRequest request)` - Create new resource
- `submitFormData(Map<String, String> formData)` - Submit form-encoded data

#### PUT Operation
- `updateResource(Long id, ExternalApiRequest request)` - Full update

#### PATCH Operation
- `partialUpdateResource(Long id, Map<String, Object> updates)` - Partial update

#### DELETE Operation
- `deleteResource(Long id)` - Delete resource

### 3. Controller Layer (ExternalApiController.java)
Exposes RESTful endpoints for all service operations:

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/external/resources` | Get all resources |
| GET | `/api/v1/external/resources/{id}` | Get resource by ID |
| GET | `/api/v1/external/resources/search?userId={userId}` | Search by user ID |
| POST | `/api/v1/external/resources` | Create new resource |
| PUT | `/api/v1/external/resources/{id}` | Update resource |
| PATCH | `/api/v1/external/resources/{id}` | Partial update |
| DELETE | `/api/v1/external/resources/{id}` | Delete resource |
| GET | `/api/v1/external/resources/{id}/with-headers` | Get with custom headers |
| POST | `/api/v1/external/resources/form` | Submit form data |
| GET | `/api/v1/external/resources/{id}/with-status-handling` | Get with status handling |
| GET | `/api/v1/external/resources/{id}/full-response` | Get with full response |

### 4. Exception Handling
- `GlobalExceptionHandler` - Centralized exception handling
- `ExternalApiException` - Custom exception for API errors
- `ResourceNotFoundException` - For 404 scenarios
- Handles HTTP client/server errors, timeouts, and network issues

## Running the Application

### Start the application
```bash
./gradlew bootRun
```

### Build the application
```bash
./gradlew clean build
```

### Run tests
```bash
./gradlew test
```

## Testing the Endpoints

Use the included `test-endpoints.http` file with IntelliJ HTTP Client or similar tools.

### Example: Get All Resources
```http
GET http://localhost:8080/api/v1/external/resources
```

### Example: Create Resource
```http
POST http://localhost:8080/api/v1/external/resources
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "message": "This is a test message"
}
```

### Using cURL

#### GET Request
```bash
curl -X GET http://localhost:8080/api/v1/external/resources
```

#### POST Request
```bash
curl -X POST http://localhost:8080/api/v1/external/resources \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com","message":"Test"}'
```

#### PUT Request
```bash
curl -X PUT http://localhost:8080/api/v1/external/resources/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Updated Name","email":"updated@example.com","message":"Updated"}'
```

#### DELETE Request
```bash
curl -X DELETE http://localhost:8080/api/v1/external/resources/1
```

## Key RestClient Features Demonstrated

### 1. URI Building with Path Variables
```java
restClient.get()
    .uri("/posts/{id}", id)
    .retrieve()
    .body(ExternalApiResponse.class);
```

### 2. Query Parameters
```java
restClient.get()
    .uri(uriBuilder -> uriBuilder
        .path("/posts")
        .queryParam("userId", userId)
        .build())
    .retrieve()
    .body(new ParameterizedTypeReference<>() {});
```

### 3. POST with JSON Body
```java
restClient.post()
    .uri("/posts")
    .contentType(MediaType.APPLICATION_JSON)
    .body(request)
    .retrieve()
    .body(ExternalApiResponse.class);
```

### 4. Custom Headers
```java
RestClient.RequestHeadersUriSpec<?> spec = restClient.get();
headers.forEach(spec::header);
```

### 5. Status Code Handling
```java
restClient.get()
    .uri("/posts/{id}", id)
    .retrieve()
    .onStatus(status -> status.value() == 404, (request, response) -> {
        throw new RuntimeException("Resource not found");
    })
    .body(ExternalApiResponse.class);
```

### 6. Full Response Access
```java
restClient.get()
    .uri("/posts/{id}", id)
    .exchange((request, response) -> {
        // Access status code, headers, and body
        return Map.of(
            "status", response.getStatusCode().value(),
            "headers", response.getHeaders(),
            "body", response.bodyTo(ExternalApiResponse.class)
        );
    });
```

## Error Response Format
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Resource not found with id: 999",
  "timestamp": "2024-12-14T23:00:00",
  "path": "/api/v1/external/resources/999"
}
```

## Dependencies
- Spring Boot 4.0.0
- Spring Web MVC
- Lombok
- H2 Database (for potential data persistence)
- Spring Boot DevTools

## Next Steps
1. Add authentication (OAuth2, API keys, JWT)
2. Implement retry logic with exponential backoff
3. Add circuit breaker pattern (Resilience4j)
4. Implement request/response logging interceptors
5. Add caching for GET requests
6. Create integration tests
7. Add API rate limiting
8. Implement request/response validation

## Troubleshooting

### Connection Timeout
Increase timeout values in `application.properties`:
```properties
external.api.connect-timeout=10000
external.api.read-timeout=10000
```

### SSL Certificate Issues
For development, you might need to configure SSL trust:
```java
// Add to RestClientConfig if needed
```

### Base URL Changes
Update `external.api.base-url` in `application.properties` or use environment variables:
```bash
export EXTERNAL_API_BASE_URL=https://your-api.com
```

## References
- [Spring RestClient Documentation](https://docs.spring.io/spring-framework/reference/integration/rest-clients.html#rest-restclient)
- [JSONPlaceholder API](https://jsonplaceholder.typicode.com/)