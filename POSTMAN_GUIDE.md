# Postman Collection Guide

## How to Import the Collection

### Method 1: Import via File
1. Open Postman
2. Click "Import" button (top left)
3. Select "File" tab
4. Click "Choose Files"
5. Navigate to and select `RestClient-API.postman_collection.json`
6. Click "Import"

### Method 2: Drag and Drop
1. Open Postman
2. Drag the `RestClient-API.postman_collection.json` file into Postman window
3. Collection will be automatically imported

## Collection Overview

The collection contains **15 API requests** organized into 6 folders:

### 1. GET Requests (6 endpoints)
- **Get All Resources** - Retrieves all resources from external API
- **Get Resource by ID** - Fetches a single resource by ID
- **Get Resources by User ID** - Demonstrates query parameters
- **Get Resource with Custom Headers** - Shows custom header handling
- **Get Resource with Status Handling** - Demonstrates status code handling
- **Get Resource with Full Response** - Returns status, headers, and body

### 2. POST Requests (2 endpoints)
- **Create New Resource (JSON)** - Creates resource with JSON body
- **Submit Form Data** - Creates resource with form-encoded data

### 3. PUT Requests (1 endpoint)
- **Update Resource (Full Update)** - Replaces entire resource

### 4. PATCH Requests (2 endpoints)
- **Partial Update Resource** - Updates single field
- **Partial Update - Multiple Fields** - Updates multiple fields

### 5. DELETE Requests (1 endpoint)
- **Delete Resource** - Deletes a resource by ID

### 6. Error Handling Tests (2 endpoints)
- **Get Non-Existent Resource** - Tests 404 handling
- **Invalid Request Body** - Tests 400 error handling

## Using the Collection

### Variables
The collection includes environment variables:
- `baseUrl`: http://localhost:8080
- `apiVersion`: v1

### Before Running Tests
1. Ensure the application is running:
   ```bash
   ./gradlew bootRun
   ```

2. Verify the application is accessible:
   ```bash
   curl http://localhost:8080/api/v1/external/resources/1
   ```

### Running Individual Requests
1. Expand the collection folder
2. Click on any request
3. Click "Send" button
4. View response in the bottom panel

### Running All Requests
1. Click the "..." menu on the collection name
2. Select "Run collection"
3. Click "Run RestClient API Collection"
4. View results in the Collection Runner

## Expected Responses

### GET All Resources
**Status Code:** 200 OK
```json
[
  {
    "id": 1,
    "name": null,
    "email": null,
    "message": null,
    "status": null,
    "createdAt": null
  },
  ...
]
```

### GET Resource by ID
**Status Code:** 200 OK
```json
{
  "id": 1,
  "name": null,
  "email": null,
  "message": null,
  "status": null,
  "createdAt": null
}
```

### POST Create Resource
**Status Code:** 201 Created
```json
{
  "id": 101,
  "name": "John Doe",
  "email": "john.doe@example.com",
  "message": "This is a test message from Postman",
  "status": null,
  "createdAt": null
}
```

### PUT Update Resource
**Status Code:** 200 OK
```json
{
  "id": 1,
  "name": "John Doe Updated",
  "email": "john.updated@example.com",
  "message": "This is an updated message",
  "status": null,
  "createdAt": null
}
```

### PATCH Partial Update
**Status Code:** 200 OK
```json
{
  "id": 1,
  "name": "Partially Updated Name",
  "email": null,
  "message": null,
  "status": null,
  "createdAt": null
}
```

### DELETE Resource
**Status Code:** 204 No Content
(Empty response body)

## Testing Different IDs

You can modify the ID in the URL to test different resources:
- Valid IDs: 1-100 (from JSONPlaceholder API)
- Created IDs: 101+ (newly created resources)
- Invalid ID: 999999 (for error testing)

## Customizing Requests

### Changing Request Body
1. Click on a POST/PUT/PATCH request
2. Go to "Body" tab
3. Modify the JSON content
4. Click "Send"

### Adding/Modifying Headers
1. Go to "Headers" tab
2. Add new key-value pairs
3. Enable/disable headers with checkbox
4. Click "Send"

### Changing Query Parameters
1. Click on a GET request with query params
2. Go to "Params" tab
3. Modify values or add new parameters
4. Click "Send"

## Adding Automated Tests

You can add test scripts to verify responses. Click on "Tests" tab and add:

### Example: Verify Status Code
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});
```

### Example: Verify Response Body
```javascript
pm.test("Response has id field", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('id');
});
```

### Example: Verify Response Time
```javascript
pm.test("Response time is less than 2000ms", function () {
    pm.expect(pm.response.responseTime).to.be.below(2000);
});
```

## Troubleshooting

### Connection Refused Error
- Ensure application is running on port 8080
- Check with: `curl http://localhost:8080/api/v1/external/resources/1`

### 404 Not Found
- Verify the endpoint URL is correct
- Check application logs for routing issues

### Timeout Errors
- Increase timeout in application.properties:
  ```properties
  external.api.read-timeout=10000
  ```

### Invalid JSON Error
- Verify JSON syntax in request body
- Check Content-Type header is `application/json`

## Collection Statistics

- **Total Requests:** 15
- **GET Requests:** 6
- **POST Requests:** 2
- **PUT Requests:** 1
- **PATCH Requests:** 2
- **DELETE Requests:** 1
- **Error Tests:** 2
- **Variables:** 2 (baseUrl, apiVersion)

## Next Steps

1. Import the collection into Postman
2. Run all requests to verify functionality
3. Add custom test scripts for validation
4. Create environment for different environments (dev, staging, prod)
5. Export results for documentation

## Advanced Usage

### Creating Environments
1. Click "Environments" in sidebar
2. Create "Development" environment
3. Add variables:
   - `baseUrl`: http://localhost:8080
   - `externalApiUrl`: https://jsonplaceholder.typicode.com
4. Select environment from dropdown

### Exporting Test Results
1. Run collection
2. Click "Export Results" in Collection Runner
3. Choose format (JSON/HTML)
4. Save report

### Sharing Collection
1. Click "..." on collection
2. Select "Share"
3. Generate public link or invite team members

## API Endpoints Summary

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/external/resources` | Get all resources |
| GET | `/api/v1/external/resources/{id}` | Get resource by ID |
| GET | `/api/v1/external/resources/search?userId={id}` | Search by user ID |
| GET | `/api/v1/external/resources/{id}/with-headers` | Get with custom headers |
| GET | `/api/v1/external/resources/{id}/with-status-handling` | Get with status handling |
| GET | `/api/v1/external/resources/{id}/full-response` | Get full response details |
| POST | `/api/v1/external/resources` | Create new resource (JSON) |
| POST | `/api/v1/external/resources/form` | Create with form data |
| PUT | `/api/v1/external/resources/{id}` | Full update resource |
| PATCH | `/api/v1/external/resources/{id}` | Partial update resource |
| DELETE | `/api/v1/external/resources/{id}` | Delete resource |