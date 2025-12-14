package dev.radom.restclient.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class ExternalApiException extends RuntimeException {
  private final HttpStatusCode statusCode;
  private final String responseBody;

  public ExternalApiException(String message, HttpStatusCode statusCode, String responseBody) {
    super(message);
    this.statusCode = statusCode;
    this.responseBody = responseBody;
  }

  public ExternalApiException(String message, HttpStatusCode statusCode) {
    super(message);
    this.statusCode = statusCode;
    this.responseBody = null;
  }

  public ExternalApiException(String message, Throwable cause) {
    super(message, cause);
    this.statusCode = null;
    this.responseBody = null;
  }
}
