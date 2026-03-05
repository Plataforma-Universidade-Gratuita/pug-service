package com.pug.shared.presenter.rest;

import java.util.List;

/**
 * Represents a collection of validation errors associated with a specific field.
 *
 * <p>Grouping errors by field provides a cleaner and more structured API response, especially when
 * a single input field violates multiple business rules simultaneously.
 *
 * @param field The exact name of the property or field that caused the validation errors.
 * @param errors A list of specific error details (code and localized message) associated with this
 *     field.
 */
public record FieldErrorsResponse(String field, List<FieldErrorDetail> errors) {

  /**
   * Represents the specific error code and its corresponding localized message.
   *
   * @param code The raw string identifier of the error (e.g., "INVALID_NAME_BLANK").
   * @param message The localized, human-readable error message translated via resource bundles.
   */
  public record FieldErrorDetail(String code, String message) {}
}
