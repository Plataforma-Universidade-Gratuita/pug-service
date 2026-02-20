package com.pug.partner.domain.enums;

import com.pug.shared.domain.enums.GenericErrorCodes;
import lombok.Getter;

/**
 * Enum representing error codes specific to the partner domain.
 *
 * <p>Each error code is associated with a specific validation failure scenario and has a {@code
 * bundleKey} that results into a localized error message. It also includes a {@code fieldName}
 * property to identify the specific field related to the error, if applicable.
 */
@Getter
public enum PartnerErrorCodes implements GenericErrorCodes {
  /* Validation Errors */
  INVALID_CNPJ_BLANK("error.domain.partner.cnpj.blank", "cnpj"),
  INVALID_CNPJ_FORMAT("error.domain.partner.cnpj.format", "cnpj"),
  /* Resource Errors */
  ENTITY_ALREADY_EXISTS("error.domain.partner.entity.exists", null),
  ENTITY_NOT_FOUND("error.domain.partner.entity.not.found", null),
  STAFF_ALREADY_EXISTS("error.domain.partner.staff.exists", null),
  STAFF_NOT_FOUND("error.domain.partner.staff.not.found", null);

  private final String bundleKey;
  private final String fieldName;

  /**
   * Constructor for the PartnerErrorCodes enum.
   *
   * @param bundleKey The internationalization resource key associated with the error.
   * @param fieldName The name of the field associated with the error, or null if not
   *     field-specific.
   */
  PartnerErrorCodes(String bundleKey, String fieldName) {
    this.bundleKey = bundleKey;
    this.fieldName = fieldName;
  }
}
