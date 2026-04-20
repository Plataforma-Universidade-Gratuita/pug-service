package br.org.catolicasc.pug.partner.domain.enums;

import br.org.catolicasc.pug.shared.domain.DomainError;
import br.org.catolicasc.pug.shared.domain.enums.GenericFieldErrorCodes;
import lombok.Getter;

/**
 * Enumeration of field-specific validation errors within the Partner domain.
 *
 * <p>This enum implements {@link GenericFieldErrorCodes} to provide a standardized contract for
 * localized error messages mapped to specific domain properties (e.g., "cnpj", "address"). These
 * constants are primarily accumulated inside {@link DomainError} instances when value objects or
 * entities fail their internal validations.
 */
@Getter
public enum PartnerFieldErrorCodes implements GenericFieldErrorCodes {

  /** Indicates that an account ID was provided as null. */
  INVALID_ACCOUNT_ID_BLANK("error.domain.partner.accountId.blank", "accountId"),

  /** Indicates that a physical address was provided as null, empty, or whitespace. */
  INVALID_ADDRESS_BLANK("error.domain.partner.address.blank", "address"),

  /** Indicates that a physical address exceeds the maximum allowed length constraints. */
  INVALID_ADDRESS_TOO_LONG("error.domain.partner.address.tooLong", "address"),

  /** Indicates that a city ID was provided as null. */
  INVALID_CITY_ID_BLANK("error.domain.partner.cityId.blank", "cityId"),

  /** Indicates that a CNPJ string was provided as null, empty, or whitespace. */
  INVALID_CNPJ_BLANK("error.domain.partner.cnpj.blank", "cnpj"),

  /**
   * Indicates that a CNPJ string does not match the required format or failed the internal
   * mathematical checksum validation.
   */
  INVALID_CNPJ_FORMAT("error.domain.partner.cnpj.format", "cnpj"),

  /** Indicates that a partner entity ID was provided as null. */
  INVALID_ENTITY_ID_BLANK("error.domain.partner.entityId.blank", "entityId");

  /**
   * The property key used to resolve the localized error message in the application's resource
   * bundles.
   */
  private final String bundleKey;

  /** The exact name of the domain property or DTO field that failed validation. */
  private final String fieldName;

  /**
   * Constructs a {@code PartnerFieldErrorCodes} instance.
   *
   * @param bundleKey the unique i18n key mapping to the resource bundles (e.g., {@code
   *     messages_en_US.properties})
   * @param fieldName the literal name of the domain property or DTO field that failed validation
   *     (used heavily for API error payload mapping)
   */
  PartnerFieldErrorCodes(String bundleKey, String fieldName) {
    this.bundleKey = bundleKey;
    this.fieldName = fieldName;
  }
}
