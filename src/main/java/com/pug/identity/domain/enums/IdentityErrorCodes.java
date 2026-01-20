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
  INVALID_ID_BLANK("error.domain.identity.id.blank", "id"),
  INVALID_USER_ID_BLANK("error.domain.identity.user.id.blank", "userId"),
  INVALID_CPF_BLANK("error.domain.identity.cpf.blank", "cpf"),
  INVALID_CPF_FORMAT("error.domain.identity.cpf.format", "cpf"),
  INVALID_CPF_LENGTH("error.domain.identity.cpf.length", "cpf"),
  INVALID_NAME_BLANK("error.domain.identity.account.name.blank", "name"),
  INVALID_NAME_LENGTH("error.domain.identity.account.name.toolong", "name"),
  INVALID_EMAIL_BLANK("error.domain.identity.email.blank", "email"),
  INVALID_EMAIL_FORMAT("error.domain.identity.email.format", "email"),
  INVALID_EMAIL_LENGTH("error.domain.identity.email.toolong", "email"),
  INVALID_ACCOUNT_TYPE_BLANK("error.domain.identity.account.type.blank", "accountType"),
  INVALID_PASSWORD_HASH_BLANK("error.domain.identity.password.hash.blank", "passwordHash"),
  INVALID_PASSWORD_HASH_LENGTH("error.domain.identity.password.hash.toolong", "passwordHash"),
  INVALID_ACTIVE_BLANK("error.domain.identity.active.null", "active"),
  INVALID_CREATED_AT_BLANK("error.domain.identity.created.at.blank", "createdAt"),
  INVALID_CREATED_AT_FUTURE("error.domain.identity.created.at.future", "createdAt"),
  INVALID_ACCOUNT_BLANK("error.domain.identity.account.blank", "accountId"),
  INVALID_GRANTED_AT_BLANK("error.domain.identity.granted.at.blank", "grantedAt"),
  INVALID_GRANTED_AT_FUTURE("error.domain.identity.granted.at.future", "grantedAt"),


  ACCOUNT_ALREADY_EXISTS("error.domain.identity.account.already.exists", null),
  ACCOUNT_NOT_FOUND("error.domain.identity.account.not.found", null),
  ADMIN_ALREADY_EXISTS("error.domain.identity.admin.already.exists", null),
  ADMIN_NOT_FOUND("error.domain.identity.admin.not.found", null),
  INVALID_ADMIN_ACCOUNT_INVALID("error.domain.identity.admin.account.invalid", null),
  USER_ALREADY_EXISTS("error.domain.identity.user.already.exists", null),
  USER_NOT_FOUND("error.domain.identity.user.not.found", null),
  INVALID_USER_BLANK("error.domain.identity.user.blank", null),

  ACCOUNT_STILL_REFERENCED_BY_ADMIN("error.domain.identity.account.still.referenced.by.admin", null),
  ACCOUNT_STILL_REFERENCED_BY_STAFF("error.domain.identity.account.still.referenced.by.staff", null),
  ACCOUNT_STILL_REFERENCED_BY_STUDENT("error.domain.identity.account.still.referenced.by.student", null),
  USER_STILL_REFERENCED("error.domain.identity.user.still.referenced", null);


  private final String bundleKey;
  private final String fieldName;

  /**
   * Constructor for the IdentityErrorCodes enum.
   *
   * @param bundleKey The internationalization resource key associated with the error.
   * @param fieldName The name of the field associated with the error, or null if not field-specific.
   */
  IdentityErrorCodes(String bundleKey, String fieldName) {
    this.bundleKey = bundleKey;
    this.fieldName = fieldName;
  }
}