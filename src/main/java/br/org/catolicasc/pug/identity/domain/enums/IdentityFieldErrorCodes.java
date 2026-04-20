package br.org.catolicasc.pug.identity.domain.enums;

import br.org.catolicasc.pug.shared.domain.DomainError;
import br.org.catolicasc.pug.shared.domain.enums.GenericFieldErrorCodes;
import lombok.Getter;

/**
 * Enumeration of field-specific validation errors within the Identity domain.
 *
 * <p>This enum implements {@link GenericFieldErrorCodes} to provide a standardized contract for
 * localized error messages mapped to specific domain properties (e.g., "cpf", "email"). These
 * constants are primarily accumulated inside {@link DomainError} instances
 * when value objects or entities fail their internal validations.
 */
@Getter
public enum IdentityFieldErrorCodes implements GenericFieldErrorCodes {

  /** Indicates that an account ID was provided as null. */
  INVALID_ACCOUNT_ID_BLANK("error.domain.identity.account.id.blank", "accountId"),

  /** Indicates that an account type was provided as null, empty, or whitespace. */
  INVALID_ACCOUNT_TYPE_BLANK("error.domain.identity.account.type.blank", "accountType"),

  /** Indicates that the active status flag for an account was provided as null. */
  INVALID_ACTIVE_FLAG_BLANK("error.domain.identity.account.active.blank", "active"),

  /** Indicates that a CPF string was provided as null, empty, or whitespace. */
  INVALID_CPF_BLANK("error.domain.identity.cpf.blank", "cpf"),

  /**
   * Indicates that a CPF string does not match the required format or failed the internal checksum
   * validation.
   */
  INVALID_CPF_FORMAT("error.domain.identity.cpf.format", "cpf"),

  /** Indicates that an email address was provided as null, empty, or whitespace. */
  INVALID_EMAIL_BLANK("error.domain.identity.email.blank", "email"),

  /** Indicates that an email address does not match the required standard formatting rules. */
  INVALID_EMAIL_FORMAT("error.domain.identity.email.format", "email"),

  /** Indicates that the granted date for an administrator was provided as null. */
  INVALID_GRANTED_AT_BLANK("error.domain.identity.granted.at.blank", "grantedAt"),

  /** Indicates that a password hash was provided as null, empty, or whitespace. */
  INVALID_PASSWORD_HASH_BLANK("error.domain.identity.password.hash.blank", "passwordHash"),

  /** Indicates that a password hash exceeds the maximum allowed length constraints. */
  INVALID_PASSWORD_HASH_TOO_LONG("error.domain.identity.password.hash.too.long", "passwordHash"),

  /** Indicates that a user ID was provided as null. */
  INVALID_USER_ID_BLANK("error.domain.identity.user.id.blank", "userId"),

  /** Indicates that a user ID exceeds the maximum allowed length constraints. */
  INVALID_USER_ID_TOO_LONG("error.domain.identity.user.id.too.long", "userId");

  /**
   * The property key used to resolve the localized error message in the application's resource
   * bundles.
   */
  private final String bundleKey;

  /** The exact name of the domain property or DTO field that failed validation. */
  private final String fieldName;

  /**
   * Constructs a {@code IdentityFieldErrorCodes} instance.
   *
   * @param bundleKey the unique i18n key mapping to the resource bundles (e.g., {@code
   *     messages_en_US.properties})
   * @param fieldName the literal name of the domain property or DTO field that failed validation
   *     (used heavily for API error payload mapping)
   */
  IdentityFieldErrorCodes(String bundleKey, String fieldName) {
    this.bundleKey = bundleKey;
    this.fieldName = fieldName;
  }
}
