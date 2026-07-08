package br.org.catolicasc.pug.geo.domain.enums;

import br.org.catolicasc.pug.shared.domain.DomainError;
import br.org.catolicasc.pug.shared.domain.enums.GenericFieldErrorCodes;
import lombok.Getter;

/**
 * Enumeration of field-specific validation errors within the Geographic (Geo) domain.
 *
 * <p>This enum implements {@link GenericFieldErrorCodes} to provide a standardized contract for
 * localized error messages mapped to specific domain properties (e.g., "ibgeCode"). These constants
 * are primarily accumulated inside {@link DomainError} instances when value objects or entities
 * fail their internal validations.
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

  private final String bundleKey;

  private final String fieldName;

  GeoFieldErrorCodes(String bundleKey, String fieldName) {
    this.bundleKey = bundleKey;
    this.fieldName = fieldName;
  }
}
