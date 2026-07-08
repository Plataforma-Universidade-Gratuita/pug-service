package br.org.catolicasc.pug.shared.presenter.rest;

import java.util.Collections;
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

  public FieldErrorsResponse {
    errors = (errors != null) ? List.copyOf(errors) : null;
  }

  /**
   * Returns an immutable view of the field-level validation details.
   *
   * @return an unmodifiable list containing the validation details, or {@code null} when no details
   *     were supplied
   */
  public List<FieldErrorDetail> errors() {
    return errors != null ? Collections.unmodifiableList(errors) : null;
  }

  /**
   * Represents the specific error code and its corresponding localized message.
   *
   * @param code The raw string identifier of the error (e.g., "INVALID_NAME_BLANK").
   * @param message The localized, human-readable error message translated via resource bundles.
   */
  public record FieldErrorDetail(String code, String message) {}
}
