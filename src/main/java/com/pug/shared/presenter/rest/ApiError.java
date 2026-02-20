package com.pug.shared.presenter.rest;

/**
 * Represents an error returned by the REST API.
 *
 * @param code The error code (untranslated, for machine/developer consumption).
 * @param message The error message (already translated and formatted for the end user).
 * @param details An optional map of additional details about the error.
 */
public record ApiError(String code, String message, Details details) {
  /**
   * Factory method to create an ApiError instance.
   *
   * @param code The error code (untranslated, for machine/developer consumption).
   * @param message The error message (already translated and formatted for the end user).
   * @param details An optional map of additional details (can be null).
   * @return An ApiError instance.
   */
  public static ApiError of(String code, String message, Details details) {
    return new ApiError(code, message, details);
  }

  /**
   * Overload for {@code of} without additional details.
   *
   * @param code The error code.
   * @param message The error message.
   * @return An ApiError instance.
   */
  public static ApiError of(String code, String message) {
    return new ApiError(code, message, null);
  }
}
