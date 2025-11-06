package com.pug.shared.errors;

import lombok.Getter;

/**
 * Enumeration of generic error codes used across the application.
 */
@Getter
public enum ErrorCodes implements GenericErrorCodes {
  INTERNAL_ERROR("error.internal"),
  VALIDATION_ERROR("error.validation");

  private final String bundleKey;

  ErrorCodes(String bundleKey) {
    this.bundleKey = bundleKey;
  }
}
