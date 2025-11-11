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
  INVALID_CPF_BLANK("identity.error.cpf.blank"),
  INVALID_CPF_LENGTH("identity.error.cpf.length"),
  INVALID_CPF_FORMAT("identity.error.cpf.format"),
  INVALID_EMAIL_BLANK("identity.error.email.blank"),
  INVALID_EMAIL_LENGTH("identity.error.email.length"),
  INVALID_EMAIL_FORMAT("identity.error.email.format"),
  INVALID_USER_BLANK("identity.error.user.blank"),
  INVALID_ACCOUNT_TYPE_BLANK("identity.error.account.type.blank"),
  INVALID_PASSWORD_HASH_LENGTH("identity.error.password.length"),
  INVALID_CREATED_AT_FUTURE("identity.error.created.at.future"),
  INVALID_ACCOUNT_BLANK("identity.error.account.blank"),
  INVALID_NAME_BLANK("identity.error.name.blank"),
  INVALID_NAME_LENGTH("identity.error.name.length"),
  USER_NOT_FOUND("identity.error.user.not.found"),
  USER_ALREADY_EXISTS("identity.error.user.already.exists"),
  USER_STILL_REFERENCED("identity.error.user.still.referenced");

  private final String bundleKey;

  IdentityErrorCodes(String bundleKey) {
    this.bundleKey = bundleKey;
  }
}
