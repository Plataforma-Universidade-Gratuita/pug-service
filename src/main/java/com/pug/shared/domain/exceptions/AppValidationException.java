package com.pug.shared.domain.exceptions;

public class AppValidationException extends DomainException {
  public AppValidationException(String code) {
    super(code);
  }
}
