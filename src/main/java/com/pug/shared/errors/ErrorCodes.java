package com.pug.shared.errors;

import lombok.Getter;

@Getter
public enum ErrorCodes implements GenericErrorCodes {
  INTERNAL_ERROR("error.internal"),
  VALIDATION_ERROR("error.validation");

  private final String bundleKey;

  ErrorCodes(String bundleKey) {
    this.bundleKey = bundleKey;
  }
}
