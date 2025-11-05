package com.pug.shared.domain.exceptions;

public class DuplicateResourceException extends DomainException {
  public DuplicateResourceException(String code) {
    super(code);
  }

  public DuplicateResourceException(String code, Throwable cause) {
    super(code, cause);
  }
}
