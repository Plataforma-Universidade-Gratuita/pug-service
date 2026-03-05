package com.pug.shared.presenter.rest;

/**
 * Standardized structure for representing errors returned by the REST API.
 *
 * <p>This record encapsulates the core details of an error, separating the machine-readable
 * identifier from the human-readable localized message, and optionally including granular context
 * (such as specific field validation failures).
 *
 * @param code The programmatic error identifier (e.g., "VALIDATION_ERROR"), typically derived from
 *     {@link com.pug.shared.domain.enums.GenericCodes#getCode()}. Intended for programmatic
 *     handling by clients.
 * @param message The human-readable, localized error message translated for the end user.
 * @param details An optional structured object containing additional, granular context about the
 *     error (e.g., a list of {@link FieldErrorsResponse} objects).
 */
public record ApiError(String code, String message, Details details) {

  /**
   * Factory method to create an {@code ApiError} instance that includes granular details.
   *
   * <p>Typically used for validation errors (e.g., HTTP 400 or 422) where the client needs to know
   * exactly which fields failed and why.
   *
   * @param code The untranslated, machine-readable error code.
   * @param message The localized, human-readable error message.
   * @param details A {@link Details} object containing specific error context, or {@code null} if
   *     not applicable.
   * @return A newly constructed {@link ApiError} instance.
   */
  public static ApiError of(String code, String message, Details details) {
    return new ApiError(code, message, details);
  }

  /**
   * Factory method to create an {@code ApiError} instance without additional granular details.
   *
   * <p>Useful for general, high-level errors (e.g., "Internal Server Error", "Resource Not Found",
   * or "Unauthorized") where field-specific context is unnecessary.
   *
   * @param code The untranslated, machine-readable error code.
   * @param message The localized, human-readable error message.
   * @return A newly constructed {@link ApiError} instance with {@code null} details.
   */
  public static ApiError of(String code, String message) {
    return new ApiError(code, message, null);
  }
}
