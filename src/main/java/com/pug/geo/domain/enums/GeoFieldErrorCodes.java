package com.pug.geo.domain.enums;

import com.pug.shared.domain.enums.GenericFieldErrorCodes;
import lombok.Getter;

/**
 * Enumeration of field-specific validation errors within the Geographic (Geo) domain.
 *
 * <p>This enum implements {@link GenericFieldErrorCodes} to provide a standardized contract for
 * localized error messages mapped to specific domain properties (e.g., "ibgeCode"). These constants
 * are primarily accumulated inside {@link com.pug.shared.domain.DomainError} instances when value
 * objects or entities fail their internal validations.
 */
@Getter
public enum GeoFieldErrorCodes implements GenericFieldErrorCodes {

  /** Indicates that an IBGE code string was provided as null, empty, or whitespace. */
  INVALID_IBGE_CODE_BLANK("error.domain.geo.ibge.code.blank", "ibgeCode"),

  /**
   * Indicates that an IBGE code string does not match the required format (e.g., must be exactly 7
   * numeric digits).
   */
  INVALID_IBGE_CODE_FORMAT("error.domain.geo.ibge.code.invalid", "ibgeCode");

  /**
   * The property key used to resolve the localized error message in the application's resource
   * bundles.
   */
  private final String bundleKey;

  /** The exact name of the domain property or DTO field that failed validation. */
  private final String fieldName;

  /**
   * Constructs a {@code GeoFieldErrorCodes} instance.
   *
   * @param bundleKey the unique i18n key mapping to the resource bundles (e.g., {@code
   *     messages_en_US.properties})
   * @param fieldName the literal name of the field causing the validation error (used heavily for
   *     API error payload mapping)
   */
  GeoFieldErrorCodes(String bundleKey, String fieldName) {
    this.bundleKey = bundleKey;
    this.fieldName = fieldName;
  }
}
