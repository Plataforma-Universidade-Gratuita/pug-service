package com.pug.identity.domain.enums;

import com.pug.shared.domain.enums.GenericErrorCodes;
import lombok.Getter;

/**
 * Enum representing error codes specific to the identity domain.
 *
 * <p>Each error code is associated with a specific validation failure scenario and has a {@code
 * bundleKey} that results into a located error message. It also includes a {@code fieldName}
 * property to identify the specific field related to the error, if applicable.
 */
@Getter
public enum IdentityErrorCodes implements GenericErrorCodes {
  /* Validation Errors */
  INVALID_ACCOUNT_TYPE_BLANK("error.domain.identity.account.type.blank", "accountType"),
  INVALID_CPF_BLANK("error.domain.identity.cpf.blank", "cpf"),
  INVALID_CPF_FORMAT("error.domain.identity.cpf.format", "cpf"),
  INVALID_EMAIL_BLANK("error.domain.identity.email.blank", "email"),
  INVALID_EMAIL_FORMAT("error.domain.identity.email.format", "email"),
  INVALID_GRANTED_AT_BLANK("error.domain.identity.granted.at.blank", "grantedAt"),
  /* Resource Errors */
  ACCOUNT_ALREADY_EXISTS("error.domain.identity.account.already.exists", null),
  ACCOUNT_NOT_FOUND("error.domain.identity.account.not.found", null),
  ADMIN_NOT_FOUND("error.domain.identity.admin.not.found", null),
  USER_ALREADY_EXISTS("error.domain.identity.user.already.exists", null),
  USER_NOT_FOUND("error.domain.identity.user.not.found", null);

  private final String bundleKey;
  private final String fieldName;

  /**
   * Constructor for the IdentityErrorCodes enum.
   *
   * @param bundleKey The internationalization resource key associated with the error.
   * @param fieldName The name of the field associated with the error, or null if not
   *     field-specific.
   */
  IdentityErrorCodes(String bundleKey, String fieldName) {
    this.bundleKey = bundleKey;
    this.fieldName = fieldName;
  }
}
