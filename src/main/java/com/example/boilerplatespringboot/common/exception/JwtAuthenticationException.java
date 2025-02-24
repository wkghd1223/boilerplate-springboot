package com.example.boilerplatespringboot.common.exception;

import com.example.boilerplatespringboot.common.enums.ApiStatus;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class JwtAuthenticationException extends AuthenticationException {

  private static final long serialVersionUID = 174726374856728L;
  ApiStatus status;
  String message;

  public JwtAuthenticationException(ApiStatus status, Throwable t) {
    this(status, status.getMessage(), t);
  }

  public JwtAuthenticationException(ApiStatus status, String message, Throwable t) {
    super(status.getMessage(), t);
    this.status = status;
    this.message = message;
  }
}
