package com.pug.shared.domain.enums;

import lombok.Getter;

/**
 * Enum representing generic error codes for the application.
 */
@Getter
public enum ErrorCodes implements GenericErrorCodes {
  INTERNAL_ERROR("error.internal", null),
  VALIDATION_ERROR("error.validation", null);

  private final String bundleKey;
  private final String fieldName;

  /**
   * Constructor for the ErrorCodes enum.
   *
   * @param bundleKey The internationalization resource key associated with the error.
   * @param fieldName The name of the field associated with the error, if applicable.
   */
  ErrorCodes(String bundleKey, String fieldName) {
    this.bundleKey = bundleKey;
    this.fieldName = fieldName;
  }
}