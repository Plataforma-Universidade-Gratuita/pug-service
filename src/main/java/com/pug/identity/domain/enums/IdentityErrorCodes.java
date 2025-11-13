package com.pug.identity.domain.enums;

import com.pug.shared.domain.enums.GenericErrorCodes;
import lombok.Getter;

/**
 * Enum representing error codes specific to the identity domain.
 *
 * <p>Each error code is associated with a specific validation failure scenario and has a {@code
 * bundleKey} that results into a located error message
 */
@Getter
public enum IdentityErrorCodes implements GenericErrorCodes {
  ACCOUNT_ALREADY_EXISTS("identity.error.account.already.exists"),
  ACCOUNT_NOT_FOUND("identity.error.account.not.found"),
  ACCOUNT_STILL_REFERENCED_BY_ADMIN("identity.error.account.still.referenced.by.admin"),
  ACCOUNT_STILL_REFERENCED_BY_STAFF("identity.error.account.still.referenced.by.staff"),
  ACCOUNT_STILL_REFERENCED_BY_STUDENT("identity.error.account.still.referenced.by.student"),
  ADMIN_NOT_FOUND("identity.error.admin.not.found"),
  INVALID_ACCOUNT_BLANK("identity.error.account.blank"),
  INVALID_ACCOUNT_TYPE_BLANK("identity.error.account.type.blank"),
  INVALID_CPF_BLANK("identity.error.cpf.blank"),
  INVALID_CPF_FORMAT("identity.error.cpf.format"),
  INVALID_CPF_LENGTH("identity.error.cpf.length"),
  INVALID_CREATED_AT_FUTURE("identity.error.created.at.future"),
  INVALID_EMAIL_BLANK("identity.error.email.blank"),
  INVALID_EMAIL_FORMAT("identity.error.email.format"),
  INVALID_EMAIL_LENGTH("identity.error.email.length"),
  INVALID_NAME_BLANK("identity.error.name.blank"),
  INVALID_NAME_LENGTH("identity.error.name.length"),
  INVALID_PASSWORD_HASH_LENGTH("identity.error.password.length"),
  INVALID_USER_BLANK("identity.error.user.blank"),
  USER_ALREADY_EXISTS("identity.error.user.already.exists"),
  USER_NOT_FOUND("identity.error.user.not.found"),
  USER_STILL_REFERENCED("identity.error.user.still.referenced");

  private final String bundleKey;

  IdentityErrorCodes(String bundleKey) {
    this.bundleKey = bundleKey;
  }
}
