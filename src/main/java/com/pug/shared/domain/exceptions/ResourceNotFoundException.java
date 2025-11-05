package com.pug.shared.domain.exceptions;

public class ResourceNotFoundException extends DomainException {
  public ResourceNotFoundException(String code) {
    super(code);
  }

  public ResourceNotFoundException(String code, Throwable cause) {
    super(code, cause);
  }
}
