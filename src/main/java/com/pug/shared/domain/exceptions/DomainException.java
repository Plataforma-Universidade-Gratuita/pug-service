package com.pug.shared.domain.exceptions;

public abstract class DomainException extends RuntimeException {
  private final String code;

  protected DomainException(String code) {
    super(code);
    this.code = code;
  }

  protected DomainException(String code, Throwable cause) {
    super(code, cause);
    this.code = code;
  }

  public String code() {
    return code;
  }
}
