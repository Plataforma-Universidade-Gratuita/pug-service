package com.pug.shared.errors;

import lombok.Getter;

public class DomainException extends RuntimeException {
  @Getter private final String code;
  private final Object[] args;

  public DomainException(String code, Object... args) {
    super(code);
    this.code = code;
    this.args = (args == null) ? new Object[0] : args.clone();
  }

  public Object[] getArgs() {
    return args.clone();
  }
}
