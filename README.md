# Spring Boot RestClient Implementation

A complete implementation of Spring Boot RestClient for making HTTP calls to external APIs with comprehensive error handling, logging, and multiple HTTP method support.

## 🚀 Features

- ✅ Full CRUD operations (GET, POST, PUT, PATCH, DELETE)
- ✅ Query parameters and path variables support
- ✅ Custom headers handling
- ✅ Form-encoded data submission
- ✅ Response status code handling
- ✅ Global exception handling
- ✅ Configurable timeouts
- ✅ Comprehensive logging
- ✅ Postman collection included

## 📁 Project Structure

```
src/main/java/dev/radom/restclient/
├── config/
│   └── RestClientConfig.java          # RestClient bean configuration
├── controller/
│   └── ExternalApiController.java     # REST API endpoints
├── service/
│   └── ExternalApiService.java        # Business logic with RestClient
├── dto/
│   ├── ExternalApiRequest.java        # Request DTO
│   ├── ExternalApiResponse.java       # Response DTO
│   └── ApiErrorResponse.java          # Error response DTO
└── exception/
    ├── ExternalApiException.java      # Custom API exception
    ├── ResourceNotFoundException.java # 404 exception
    └── GlobalExceptionHandler.java    # Global exception handler
```

## 🛠️ Technologies

- **Java 17**
- **Spring Boot 4.0.0**
- **Spring Web MVC**
- **Lombok**
- **Gradle**

## ⚙️ Configuration

Edit `src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=8080

# External API Configuration
external.api.base-url=https://jsonplaceholder.typicode.com
external.api.connect-timeout=5000
external.api.read-timeout=5000

# Logging
logging.level.dev.radom.restclient=DEBUG
```

## 🏃 Running the Application

### Build the project
```bash
./gradlew clean build
```

### Run the application
```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

## 📚 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/external/resources` | Get all resources |
| GET | `/api/v1/external/resources/{id}` | Get resource by ID |
| GET | `/api/v1/external/resources/search?userId={id}` | Search by user ID |
| POST | `/api/v1/external/resources` | Create new resource |
| PUT | `/api/v1/external/resources/{id}` | Full update |
| PATCH | `/api/v1/external/resources/{id}` | Partial update |
| DELETE | `/api/v1/external/resources/{id}` | Delete resource |

## 🧪 Testing

### Using cURL

**GET Request:**
```bash
curl http://localhost:8080/api/v1/external/resources/1
```

**POST Request:**
```bash
curl -X POST http://localhost:8080/api/v1/external/resources \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com","message":"Test"}'
```

**PUT Request:**
```bash
curl -X PUT http://localhost:8080/api/v1/external/resources/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Updated","email":"updated@example.com","message":"Updated"}'
```

**PATCH Request:**
```bash
curl -X PATCH http://localhost:8080/api/v1/external/resources/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Partially Updated"}'
```

**DELETE Request:**
```bash
curl -X DELETE http://localhost:8080/api/v1/external/resources/1
```

### Using Postman

1. Import `RestClient-API.postman_collection.json` into Postman
2. The collection contains 15 pre-configured requests
3. See `POSTMAN_GUIDE.md` for detailed instructions

### Using HTTP Client

Open `test-endpoints.http` in IntelliJ IDEA and run requests directly.

## 📖 Documentation

- **`REST_CLIENT_GUIDE.md`** - Complete implementation guide with code examples
- **`POSTMAN_GUIDE.md`** - Postman collection usage guide
- **`test-endpoints.http`** - HTTP client test file

## 🔧 Key Implementation Details

### RestClient Configuration
```java
@Bean
public RestClient restClient() {
    return RestClient.builder()
            .baseUrl(baseUrl)
            .requestFactory(clientHttpRequestFactory())
            .defaultHeader("Content-Type", "application/json")
            .defaultHeader("Accept", "application/json")
            .build();
}
```

### Example Service Method
```java
public ExternalApiResponse getResourceById(Long id) {
    return restClient.get()
            .uri("/posts/{id}", id)
            .retrieve()
            .body(ExternalApiResponse.class);
}
```

### Error Handling
Global exception handler catches:
- `ResourceNotFoundException` → 404
- `ExternalApiException` → Custom API errors
- `HttpClientErrorException` → 4xx errors
- `HttpServerErrorException` → 5xx errors
- `ResourceAccessException` → Timeout/network errors

## 🌟 Features Demonstrated

1. **GET with Path Variables**
   ```java
   restClient.get().uri("/posts/{id}", id)
   ```

2. **GET with Query Parameters**
   ```java
   restClient.get().uri(uriBuilder -> uriBuilder
       .path("/posts")
       .queryParam("userId", userId)
       .build())
   ```

3. **POST with JSON Body**
   ```java
   restClient.post()
       .uri("/posts")
       .contentType(MediaType.APPLICATION_JSON)
       .body(request)
   ```

4. **Custom Status Handling**
   ```java
   restClient.get()
       .uri("/posts/{id}", id)
       .retrieve()
       .onStatus(status -> status.value() == 404, ...)
   ```

5. **Full Response Access**
   ```java
   restClient.get()
       .uri("/posts/{id}", id)
       .exchange((request, response) -> {
           // Access status, headers, body
       })
   ```

## 📝 Example Response

**Success Response:**
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "message": "Test message",
  "status": null,
  "createdAt": null
}
```

**Error Response:**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Resource not found with id: 999",
  "timestamp": "2024-12-14T23:00:00",
  "path": "/api/v1/external/resources/999"
}
```

## 🚧 Next Steps / Enhancements

- [ ] Add authentication (OAuth2, JWT)
- [ ] Implement retry logic with exponential backoff
- [ ] Add circuit breaker pattern (Resilience4j)
- [ ] Implement request/response interceptors
- [ ] Add caching for GET requests
- [ ] Create integration tests
- [ ] Add API rate limiting
- [ ] Implement request/response validation

## 📄 License

This project is for educational and demonstration purposes.

## 🤝 Contributing

Feel free to fork and modify this project for your needs.

---

**Built with Spring Boot 4.0.0 and Java 17**# rest-client
