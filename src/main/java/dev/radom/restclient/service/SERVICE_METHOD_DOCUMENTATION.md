# ExternalApiService - Method Documentation

This document provides detailed explanations for each method in the ExternalApiService class.

## Table of Contents
1. [getAllResources()](#getallresources)
2. [getResourceById()](#getresourcebyid)
3. [getResourcesByUserId()](#getresourcesbyuserid)
4. [createResource()](#createresource)
5. [updateResource()](#updateresource)
6. [partialUpdateResource()](#partialupdateresource)
7. [deleteResource()](#deleteresource)
8. [getResourceWithHeaders()](#getresourcewithheaders)
9. [submitFormData()](#submitformdata)
10. [getResourceWithStatusHandling()](#getresourcewithstatushandling)
11. [getResourceWithFullResponse()](#getresourcewithfullresponse)

---

## getAllResources()

### Purpose
Retrieves all resources from the external API using a GET request.

### Code Breakdown
```java
public List<ExternalApiResponse> getAllResources() {
    log.info("Fetching all resources from external API");

    return restClient.get()                              // 1
            .uri("/posts")                               // 2
            .retrieve()                                  // 3
            .body(new ParameterizedTypeReference<>() {}); // 4
}
```

### Step-by-Step Explanation
1. **`.get()`** - Initiates a GET HTTP request
2. **`.uri("/posts")`** - Sets the endpoint path (combined with base URL from config)
3. **`.retrieve()`** - Executes the request and prepares to handle the response
4. **`.body(new ParameterizedTypeReference<>() {})`** - Deserializes JSON array to typed List

### Why ParameterizedTypeReference?
Java's type erasure removes generic type information at runtime. For `List<ExternalApiResponse>`, the JVM only knows it's a List but not what's inside it. ParameterizedTypeReference preserves this generic type information.

**Without it:**
```java
.body(List.class) // Returns raw List, loses type safety
```

**With it:**
```java
.body(new ParameterizedTypeReference<List<ExternalApiResponse>>() {})
// Returns List<ExternalApiResponse>, full type safety
```

### Benefits
- **Type Safety:** No ClassCastException at runtime
- **IDE Support:** Better autocomplete and type checking
- **No Casting:** Direct access to ExternalApiResponse methods

---

## getResourceById()

### Purpose
Retrieves a single resource by ID using path variable substitution.

### Code Breakdown
```java
public ExternalApiResponse getResourceById(Long id) {
    log.info("Fetching resource with id: {}", id);

    return restClient.get()
            .uri("/posts/{id}", id)  // Path variable substitution
            .retrieve()
            .body(ExternalApiResponse.class);
}
```

### Path Variable Substitution
The `{id}` placeholder in the URI string is automatically replaced with the actual `id` value.

**Example:**
- Input: `id = 123`
- URI Template: `/posts/{id}`
- Final URL: `https://api.example.com/posts/123`

### Why use `.body(ExternalApiResponse.class)`?
For single objects (not collections), we can use `.class` directly because there's no generic type erasure issue.

### Benefits
- **Clean Syntax:** No manual string concatenation
- **SQL Injection Protection:** Spring handles URL encoding automatically
- **Type Safety:** Strongly-typed return value

---

## getResourcesByUserId()

### Purpose
Demonstrates GET request with query parameters (?userId=1).

### Code Breakdown
```java
public List<ExternalApiResponse> getResourcesByUserId(Long userId) {
    log.info("Fetching resources for userId: {}", userId);

    return restClient.get()
            .uri(uriBuilder -> uriBuilder         // Lambda for complex URI building
                    .path("/posts")               // Base path
                    .queryParam("userId", userId) // Add query parameter
                    .build())                     // Build final URI
            .retrieve()
            .body(new ParameterizedTypeReference<>() {});
}
```

### URI Builder Pattern
The lambda function `uriBuilder -> ...` gives you access to UriBuilder for complex URI construction.

**Generated URL Example:**
```
https://api.example.com/posts?userId=1
```

### Why use UriBuilder?
**Alternative (manual):**
```java
.uri("/posts?userId=" + userId) // DON'T DO THIS
```

**Problems with manual approach:**
- No URL encoding (breaks with special characters)
- Hard to add multiple parameters
- Error-prone string concatenation

**With UriBuilder:**
```java
.uri(builder -> builder
    .path("/posts")
    .queryParam("userId", userId)
    .queryParam("status", "active")  // Easy to add more
    .build())
```

### Benefits
- **Automatic URL Encoding:** Handles special characters (&, =, etc.)
- **Multiple Parameters:** Easy to chain `.queryParam()` calls
- **Null Safety:** Can conditionally add parameters
- **Readable Code:** Intent is clear

---

## createResource()

### Purpose
Creates a new resource using POST request with JSON body.

### Code Breakdown
```java
public ExternalApiResponse createResource(ExternalApiRequest request) {
    log.info("Creating new resource: {}", request);

    return restClient.post()                      // POST method
            .uri("/posts")
            .contentType(MediaType.APPLICATION_JSON) // Set Content-Type header
            .body(request)                        // Java object → JSON automatically
            .retrieve()
            .body(ExternalApiResponse.class);
}
```

### Content-Type Header
```java
.contentType(MediaType.APPLICATION_JSON)
```

**What it does:**
- Tells server we're sending JSON data
- Triggers automatic Java object → JSON serialization
- Sets HTTP header: `Content-Type: application/json`

### Automatic JSON Serialization
Spring automatically converts the `ExternalApiRequest` object to JSON:

**Java Object:**
```java
ExternalApiRequest request = ExternalApiRequest.builder()
    .name("John Doe")
    .email("john@example.com")
    .message("Hello")
    .build();
```

**Becomes JSON (automatically):**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "message": "Hello"
}
```

### Benefits
- **No Manual JSON Creation:** Spring handles serialization
- **Type Safety:** Compile-time checking of request fields
- **Lombok Integration:** @Data, @Builder work seamlessly
- **Clean Code:** No string manipulation or JSON libraries needed

---

## updateResource()

### Purpose
Fully updates an existing resource using PUT request.

### Code Breakdown
```java
public ExternalApiResponse updateResource(Long id, ExternalApiRequest request) {
    log.info("Updating resource with id: {}", id);

    return restClient.put()                      // PUT method for full update
            .uri("/posts/{id}", id)              // Path variable
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .body(ExternalApiResponse.class);
}
```

### PUT vs PATCH
**PUT (this method):**
- Replaces the ENTIRE resource
- All fields must be provided
- Missing fields become null/default

**PATCH (next method):**
- Updates only specified fields
- Partial update
- Other fields remain unchanged

**Example:**
```json
// Original resource
{"id": 1, "name": "John", "email": "john@example.com", "message": "Hello"}

// PUT with {"name": "Jane"}
{"id": 1, "name": "Jane", "email": null, "message": null}  // Other fields cleared

// PATCH with {"name": "Jane"}
{"id": 1, "name": "Jane", "email": "john@example.com", "message": "Hello"} // Unchanged
```

### Benefits
- **Idempotent:** Multiple identical requests have same result
- **Clear Intent:** Full replacement is explicit
- **Standard REST:** Follows REST conventions

---

## partialUpdateResource()

### Purpose
Partially updates a resource using PATCH request (only specified fields).

### Code Breakdown
```java
public ExternalApiResponse partialUpdateResource(Long id, Map<String, Object> updates) {
    log.info("Partially updating resource with id: {}", id);

    return restClient.patch()                    // PATCH method
            .uri("/posts/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .body(updates)                       // Map → JSON
            .retrieve()
            .body(ExternalApiResponse.class);
}
```

### Why Map<String, Object>?
Allows flexible partial updates without creating multiple DTO classes.

**Usage Example:**
```java
// Update only name
Map<String, Object> updates = Map.of("name", "Updated Name");
partialUpdateResource(1L, updates);

// Update multiple fields
Map<String, Object> updates = Map.of(
    "name", "Updated Name",
    "email", "new@example.com"
);
partialUpdateResource(1L, updates);
```

**Becomes JSON:**
```json
// First example
{"name": "Updated Name"}

// Second example
{"name": "Updated Name", "email": "new@example.com"}
```

### Benefits
- **Flexibility:** Update any combination of fields
- **Efficiency:** Only send changed data
- **Backward Compatible:** Works with evolving APIs
- **No DTO Explosion:** Don't need separate DTOs for each update scenario

---

## deleteResource()

### Purpose
Deletes a resource using DELETE request.

### Code Breakdown
```java
public void deleteResource(Long id) {
    log.info("Deleting resource with id: {}", id);

    restClient.delete()
            .uri("/posts/{id}", id)
            .retrieve()
            .toBodilessEntity();  // No response body expected
}
```

### toBodilessEntity()
DELETE requests typically return 204 No Content (no response body).

**Why not `.body()`?**
```java
.body(Void.class) // Awkward
```

**Better:**
```java
.toBodilessEntity() // Explicit intent: no body expected
```

### HTTP Status Codes for DELETE
- **204 No Content:** Success, no body
- **200 OK:** Success with confirmation body
- **404 Not Found:** Resource doesn't exist
- **403 Forbidden:** Not authorized to delete

### Benefits
- **Clear Intent:** Method name shows no body expected
- **Proper Typing:** Returns void, not Void wrapper
- **REST Compliance:** Follows REST conventions

---

## getResourceWithHeaders()

### Purpose
Demonstrates sending custom headers to the external API.

### Code Breakdown
```java
public ExternalApiResponse getResourceWithHeaders(Long id, Map<String, String> headers) {
    log.info("Fetching resource with id: {} and custom headers", id);

    RestClient.RequestHeadersUriSpec<?> spec = restClient.get();  // 1

    headers.forEach(spec::header);  // 2

    return spec.uri("/posts/{id}", id)  // 3
            .retrieve()
            .body(ExternalApiResponse.class);
}
```

### Step-by-Step
1. **Get request spec** - Obtain the request builder
2. **Add headers** - Iterate map and add each header
3. **Complete request** - Set URI and execute

### Header Examples
```java
Map<String, String> headers = Map.of(
    "Authorization", "Bearer token123",
    "X-API-Key", "abc123",
    "X-Custom-Header", "custom-value"
);
```

### Method Reference `spec::header`
**Equivalent to:**
```java
headers.forEach((key, value) -> spec.header(key, value));
```

### Use Cases
- **Authentication:** Bearer tokens, API keys
- **Tracking:** Request IDs, correlation IDs
- **Feature Flags:** Custom headers for A/B testing
- **API Versioning:** Accept headers for version negotiation

### Benefits
- **Flexible:** Any header can be added dynamically
- **Clean Separation:** Headers separate from request logic
- **Testing:** Easy to mock different header scenarios

---

## submitFormData()

### Purpose
Submits form-encoded data (like HTML forms) instead of JSON.

### Code Breakdown
```java
public ExternalApiResponse submitFormData(Map<String, String> formData) {
    log.info("Submitting form data");

    return restClient.post()
            .uri("/posts")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)  // Key difference
            .body(formData)
            .retrieve()
            .body(ExternalApiResponse.class);
}
```

### Content-Type Difference
**JSON (previous methods):**
```java
.contentType(MediaType.APPLICATION_JSON)
// Content-Type: application/json
// Body: {"name":"John","email":"john@example.com"}
```

**Form Encoded (this method):**
```java
.contentType(MediaType.APPLICATION_FORM_URLENCODED)
// Content-Type: application/x-www-form-urlencoded
// Body: name=John&email=john%40example.com
```

### When to Use
- **Legacy APIs:** Older APIs expecting form data
- **File Uploads:** Combined with multipart/form-data
- **OAuth:** Token endpoints often require form encoding
- **Simple Integrations:** Some APIs prefer forms over JSON

### Example Usage
```java
Map<String, String> form = Map.of(
    "username", "john",
    "password", "secret123",
    "grant_type", "password"
);
```

**Sent as:**
```
username=john&password=secret123&grant_type=password
```

### Benefits
- **Compatibility:** Works with non-JSON APIs
- **Standards Compliant:** HTML form standard
- **Automatic Encoding:** Spring handles URL encoding (spaces, special chars)

---

## getResourceWithStatusHandling()

### Purpose
Demonstrates custom handling of specific HTTP status codes.

### Code Breakdown
```java
public ExternalApiResponse getResourceWithStatusHandling(Long id) {
    log.info("Fetching resource with id: {} with status handling", id);

    return restClient.get()
            .uri("/posts/{id}", id)
            .retrieve()
            .onStatus(status -> status.value() == 404, (request, response) -> {
                log.error("Resource not found with id: {}", id);
                throw new RuntimeException("Resource not found");
            })
            .onStatus(status -> status.is5xxServerError(), (request, response) -> {
                log.error("Server error occurred");
                throw new RuntimeException("Server error");
            })
            .body(ExternalApiResponse.class);
}
```

### onStatus() Method
**Signature:**
```java
.onStatus(Predicate<HttpStatus>, BiConsumer<Request, Response>)
```

**Parameters:**
1. **Predicate:** Condition to check status code
2. **BiConsumer:** Action if condition is true

### Custom Status Handlers
**404 Handler:**
```java
.onStatus(
    status -> status.value() == 404,  // If status is 404
    (request, response) -> {
        // Custom handling
        throw new ResourceNotFoundException("Not found");
    }
)
```

**5xx Handler:**
```java
.onStatus(
    status -> status.is5xxServerError(),  // If 500-599
    (request, response) -> {
        // Log and throw
        throw new ExternalApiException("Server error");
    }
)
```

### Multiple Handlers
You can chain multiple `.onStatus()` calls:
```java
.onStatus(status -> status.value() == 401, ...)  // Unauthorized
.onStatus(status -> status.value() == 403, ...)  // Forbidden
.onStatus(status -> status.value() == 429, ...)  // Too Many Requests
.onStatus(status -> status.is5xxServerError(), ...) // Server errors
```

### Benefits
- **Granular Control:** Different actions for different status codes
- **Better Error Messages:** Provide context-specific errors
- **Retry Logic:** Can implement retry for specific codes
- **Monitoring:** Log different errors differently

### Use Cases
- **404:** Throw ResourceNotFoundException
- **401:** Refresh token and retry
- **429:** Implement backoff and retry
- **5xx:** Alert ops team, use circuit breaker

---

## getResourceWithFullResponse()

### Purpose
Demonstrates accessing full HTTP response (status, headers, body) using exchange().

### Code Breakdown
```java
public Map<String, Object> getResourceWithFullResponse(Long id) {
    log.info("Fetching resource with id: {} with full response", id);

    return restClient.get()
            .uri("/posts/{id}", id)
            .exchange((request, response) -> {
                log.info("Response status: {}", response.getStatusCode());
                log.info("Response headers: {}", response.getHeaders());

                ExternalApiResponse body = response.bodyTo(ExternalApiResponse.class);

                return Map.of(
                        "status", response.getStatusCode().value(),
                        "headers", response.getHeaders(),
                        "body", body
                );
            });
}
```

### exchange() vs retrieve()
**retrieve() (all previous methods):**
- Simple: Just get the body
- Automatic error handling
- Less control

**exchange() (this method):**
- Full control: Access everything
- Manual error handling required
- More flexibility

### What's Available in exchange()?
```java
.exchange((request, response) -> {
    // Status
    HttpStatusCode status = response.getStatusCode();
    int statusValue = status.value();  // 200, 404, 500, etc.

    // Headers
    HttpHeaders headers = response.getHeaders();
    String contentType = headers.getContentType();
    List<String> customHeader = headers.get("X-Custom-Header");

    // Body
    ExternalApiResponse body = response.bodyTo(ExternalApiResponse.class);

    // Request info (if needed)
    URI uri = request.getURI();
    HttpMethod method = request.getMethod();

    // Return whatever you want
    return customObject;
});
```

### Use Cases
**Pagination:**
```java
.exchange((req, res) -> {
    List<Item> items = res.bodyTo(new ParameterizedTypeReference<>() {});
    String nextPage = res.getHeaders().getFirst("X-Next-Page");

    return new PagedResponse(items, nextPage);
});
```

**Rate Limiting:**
```java
.exchange((req, res) -> {
    int remaining = Integer.parseInt(
        res.getHeaders().getFirst("X-Rate-Limit-Remaining")
    );

    if (remaining < 10) {
        log.warn("Rate limit low: {}", remaining);
    }

    return res.bodyTo(Data.class);
});
```

**Caching:**
```java
.exchange((req, res) -> {
    String etag = res.getHeaders().getETag();
    String lastModified = res.getHeaders().getFirst("Last-Modified");

    // Store for conditional requests
    cache.put(url, etag, lastModified, body);

    return body;
});
```

### Benefits
- **Full Access:** Every part of HTTP response available
- **Custom Logic:** Implement complex scenarios
- **Header Inspection:** Extract metadata, pagination, rate limits
- **Advanced Features:** Caching, conditional requests, etc.

### When to Use
- Need response headers (pagination, rate limits, etags)
- Custom error handling based on status + headers
- Extracting metadata from response
- Implementing caching strategies

---

## Summary Table

| Method | HTTP Method | Purpose | Key Feature |
|--------|------------|---------|-------------|
| getAllResources() | GET | Get all | ParameterizedTypeReference |
| getResourceById() | GET | Get one | Path variables |
| getResourcesByUserId() | GET | Filter | Query parameters |
| createResource() | POST | Create | JSON body |
| updateResource() | PUT | Full update | Replace all fields |
| partialUpdateResource() | PATCH | Partial update | Update some fields |
| deleteResource() | DELETE | Delete | No response body |
| getResourceWithHeaders() | GET | Custom headers | Dynamic headers |
| submitFormData() | POST | Form data | URL-encoded |
| getResourceWithStatusHandling() | GET | Error handling | onStatus() |
| getResourceWithFullResponse() | GET | Full response | exchange() |

