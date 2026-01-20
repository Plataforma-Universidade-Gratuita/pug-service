package com.pug.shared.presenter.rest;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents an error returned by the REST API.
 *
 * @param code    The error code (untranslated, for machine/developer consumption).
 * @param message The error message (already translated and formatted for the end user).
 * @param details An optional map of additional details about the error.
 */
public record ApiError(String code, String message, Map<String, Object> details) {

  /**
   * Represents a specific field error in a validation context.
   *
   * @param field   The name of the field that caused the error.
   * @param code    The error code related to the field.
   * @param message The error message related to the field.
   */
  public record FieldError(String field, String code, String message) {
  }

  /**
   * Compact constructor for ApiError.
   * Ensures that the 'details' map is never null and is a defensive copy.
   */
  public ApiError {
    details = (details == null) ? Map.of() : new LinkedHashMap<>(details);
  }

  /**
   * Returns a defensive copy of the details map.
   *
   * @return A new LinkedHashMap containing the error details.
   */
  @Override
  public Map<String, Object> details() {
    return new LinkedHashMap<>(details);
  }

  /**
   * Factory method to create an ApiError instance.
   *
   * @param code    The error code (untranslated, for machine/developer consumption).
   * @param message The error message (already translated and formatted for the end user).
   * @param details An optional map of additional details (can be null).
   * @return An ApiError instance.
   */
  public static ApiError of(String code, String message, Map<String, Object> details) {
    return new ApiError(code, message, details);
  }

  /**
   * Overload for {@code of} without additional details.
   *
   * @param code    The error code.
   * @param message The error message.
   * @return An ApiError instance.
   */
  public static ApiError of(String code, String message) {
    return new ApiError(code, message, null);
  }
}