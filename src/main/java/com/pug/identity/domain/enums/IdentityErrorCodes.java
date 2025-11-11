package com.pug.identity.domain.enums;

import com.pug.shared.domain.enums.GenericErrorCodes;
import lombok.Getter;

/**
 * Enum representing error codes specific to the identity domain.
 *
 * <p>Each error code is associated with a specific validation failure scenario
 * and has a {@code bundleKey} that results into a located error message</p>
 */
@Getter
public enum IdentityErrorCodes implements GenericErrorCodes {
  INVALID_CPF("error.domain.identity.cpf"),
  INVALID_USER_NAME_BLANK("error.domain.identity.user.name.blank"),
  INVALID_USER_NAME_TOOLONG("error.domain.identity.user.name.toolong"),
  INVALID_EMAIL_BLANK("error.domain.identity.email.blank"),
  INVALID_EMAIL_TOOLONG("error.domain.identity.email.toolong"),
  INVALID_EMAIL_FORMAT("error.domain.identity.email.format"),
  INVALID_ACCOUNT_TYPE("error.domain.identity.account.type"),
  INVALID_PASSWORD_HASH_TOOLONG("error.domain.identity.password.hash.toolong"),
  INVALID_CREATED_AT_FUTURE("error.domain.identity.created.at.future"),
  USER_ALREADY_EXISTS("error.domain.identity.user.already.exists"),
  USER_NOT_FOUND("error.domain.identity.user.not.found"),
  INVALID_ADMIN_USER("error.domain.identity.admin.user.invalid"),
  ADMIN_ALREADY_EXISTS("error.domain.identity.admin.already.exists"),
  ADMIN_NOT_FOUND("error.domain.identity.admin.not.found");

  private final String bundleKey;

  IdentityErrorCodes(String bundleKey) {
    this.bundleKey = bundleKey;
  }
}
