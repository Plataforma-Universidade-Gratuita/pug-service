package com.pug.shared.domain.enums;

import lombok.Getter;

/**
 * Enum representing error codes used across the application.
 *
 * <p>Each error code is associated with a specific validation failure scenario
 * and has a {@code bundleKey} that results into a located error message</p>
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
