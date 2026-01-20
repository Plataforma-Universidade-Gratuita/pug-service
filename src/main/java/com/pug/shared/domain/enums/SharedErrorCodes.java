package com.pug.shared.domain.enums;

import lombok.Getter;

/**
 * Enum representing common, shared error codes for the entire application.
 * These codes are typically used for general validation failures, internal server errors,
 * or generic constraint violations that are not specific to a particular domain.
 */
@Getter
public enum SharedErrorCodes implements GenericErrorCodes {
  VALIDATION_ERROR("error.validation", null),
  INTERNAL_ERROR("error.internal", null);

  private final String bundleKey;
  private final String fieldName; // Always null for these general errors

  /**
   * Constructor for the SharedErrorCodes enum.
   *
   * @param bundleKey The internationalization resource key associated with the error.
   * @param fieldName The name of the field associated with the error, or null if not field-specific.
   */
  SharedErrorCodes(String bundleKey, String fieldName) {
    this.bundleKey = bundleKey;
    this.fieldName = fieldName;
  }
}