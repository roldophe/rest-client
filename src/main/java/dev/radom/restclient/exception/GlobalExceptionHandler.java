package dev.radom.restclient.exception;

import dev.radom.restclient.dto.ApiErrorResponse;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleResourceNotFoundException(
      ResourceNotFoundException ex, WebRequest request) {
    log.error("Resource not found: {}", ex.getMessage());

    ApiErrorResponse errorResponse =
        ApiErrorResponse.builder()
            .status(HttpStatus.NOT_FOUND.value())
            .error(HttpStatus.NOT_FOUND.getReasonPhrase())
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();

    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ExternalApiException.class)
  public ResponseEntity<ApiErrorResponse> handleExternalApiException(
      ExternalApiException ex, WebRequest request) {
    log.error("External API error: {}", ex.getMessage(), ex);

    HttpStatus status =
        ex.getStatusCode() != null
            ? HttpStatus.valueOf(ex.getStatusCode().value())
            : HttpStatus.INTERNAL_SERVER_ERROR;

    ApiErrorResponse errorResponse =
        ApiErrorResponse.builder()
            .status(status.value())
            .error(status.getReasonPhrase())
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();

    return new ResponseEntity<>(errorResponse, status);
  }

  @ExceptionHandler(HttpClientErrorException.class)
  public ResponseEntity<ApiErrorResponse> handleHttpClientErrorException(
      HttpClientErrorException ex, WebRequest request) {
    log.error("HTTP client error: {} - {}", ex.getStatusCode(), ex.getMessage());

    ApiErrorResponse errorResponse =
        ApiErrorResponse.builder()
            .status(ex.getStatusCode().value())
            .error(ex.getStatusText())
            .message("External API request failed: " + ex.getMessage())
            .timestamp(LocalDateTime.now())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();

    return new ResponseEntity<>(errorResponse, ex.getStatusCode());
  }

  @ExceptionHandler(HttpServerErrorException.class)
  public ResponseEntity<ApiErrorResponse> handleHttpServerErrorException(
      HttpServerErrorException ex, WebRequest request) {
    log.error("HTTP server error: {} - {}", ex.getStatusCode(), ex.getMessage());

    ApiErrorResponse errorResponse =
        ApiErrorResponse.builder()
            .status(ex.getStatusCode().value())
            .error(ex.getStatusText())
            .message("External API server error: " + ex.getMessage())
            .timestamp(LocalDateTime.now())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();

    return new ResponseEntity<>(errorResponse, ex.getStatusCode());
  }

  @ExceptionHandler(ResourceAccessException.class)
  public ResponseEntity<ApiErrorResponse> handleResourceAccessException(
      ResourceAccessException ex, WebRequest request) {
    log.error("Resource access error: {}", ex.getMessage(), ex);

    ApiErrorResponse errorResponse =
        ApiErrorResponse.builder()
            .status(HttpStatus.SERVICE_UNAVAILABLE.value())
            .error(HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase())
            .message("Unable to access external API: " + ex.getMessage())
            .timestamp(LocalDateTime.now())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();

    return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
    log.error("Unexpected error occurred: {}", ex.getMessage(), ex);

    ApiErrorResponse errorResponse =
        ApiErrorResponse.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
            .message("An unexpected error occurred: " + ex.getMessage())
            .timestamp(LocalDateTime.now())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();

    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
