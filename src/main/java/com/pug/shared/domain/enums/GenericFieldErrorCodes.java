package com.pug.shared.domain.enums;

/**
 * Interface representing a specific validation error associated with a distinct domain or DTO
 * field.
 *
 * <p>By extending {@link GenericCodes}, this interface inherits the contract for providing an
 * internationalization (i18n) message key. It builds upon it by pairing the message key with a
 * specific field identifier. It is typically implemented by enums representing specific domain
 * field constraints (e.g., "name cannot be blank").
 */
public interface GenericFieldErrorCodes extends GenericCodes {

  /**
   * Retrieves the exact name of the property or field that failed validation.
   *
   * <p>This is heavily used for mapping validation errors back to specific UI form inputs or
   * mapping to JSON attributes in standard API error responses.
   *
   * @return the field name as a {@link String}, or {@code null} if no specific field is associated
   */
  String getFieldName();
}
