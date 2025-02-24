package com.example.boilerplatespringboot.common.exception;

import com.example.boilerplatespringboot.common.enums.ApiStatus;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * The type Api exception.
 */
@Getter
public class ApiException extends RuntimeException {

  private static final long serialVersionUID = -1179299781904521091L;
  private HttpStatus httpStatus;
  private ApiStatus status;
  private String message;

  /**
   * Instantiates a new Api exception.
   *
   * @param httpStatus the http status
   * @param apiStatus  the api status
   * @param message    the message
   */
  public ApiException(HttpStatus httpStatus, ApiStatus apiStatus, String message) {
    super();
    this.httpStatus = httpStatus;
    this.status = apiStatus;
    this.message = message;
  }

  /**
   * Instantiates a new Api exception.
   *
   * @param httpStatus the http status
   * @param apiStatus  the api status
   */
  public ApiException(HttpStatus httpStatus, ApiStatus apiStatus) {
    super();
    this.httpStatus = httpStatus;
    this.status = apiStatus;
    this.message = apiStatus.getMessage();
  }

  /**
   * Instantiates a new Api exception.
   *
   * @param httpStatus the http status
   * @param message    the message
   */
  public ApiException(HttpStatus httpStatus, String message) {
    super();
    this.httpStatus = httpStatus;
    this.status = ApiStatus.INTERNAL_SERVER_ERROR;
    this.message = message;
  }

  /**
   * Instantiates a new Api exception.
   *
   * @param apiStatus the api status
   * @param message   the message
   */
  public ApiException(ApiStatus apiStatus, String message) {
    super();
    this.httpStatus = apiStatus.getStatus();
    this.status = apiStatus;
    this.message = message;
  }

  /**
   * Instantiates a new Api exception.
   *
   * @param apiStatus the api status
   */
  public ApiException(ApiStatus apiStatus) {
    super();
    this.httpStatus = apiStatus.getStatus();
    this.status = apiStatus;
    this.message = apiStatus.getMessage();
  }

  /**
   * Instantiates a new Api exception.
   *
   * @param message the message
   */
  public ApiException(String message) {
    super();
    this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    this.status = ApiStatus.INTERNAL_SERVER_ERROR;
    this.message = message;
  }
}