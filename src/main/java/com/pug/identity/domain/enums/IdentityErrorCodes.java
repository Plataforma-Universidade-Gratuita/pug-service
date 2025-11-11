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
  ADMIN_ALREADY_EXISTS("error.domain.identity.admin.already.exists"),
  ADMIN_NOT_FOUND("error.domain.identity.admin.not.found"),
  INVALID_CPF("error.domain.identity.cpf"),
  INVALID_NAME_BLANK("error.domain.identity.name.blank"),
  INVALID_NAME_TOOLONG("error.domain.identity.name.toolong"),
  INVALID_PERSON("error.domain.identity.person.invalid"),
  INVALID_EMAIL_BLANK("error.domain.identity.email.blank"),
  INVALID_EMAIL_TOOLONG("error.domain.identity.email.toolong"),
  INVALID_EMAIL_FORMAT("error.domain.identity.email.format"),
  INVALID_ACCOUNT_TYPE("error.domain.identity.account.type"),
  INVALID_PASSWORD_HASH_TOOLONG("error.domain.identity.password.hash.toolong"),
  INVALID_CREATED_AT_FUTURE("error.domain.identity.created.at.future"),
  INVALID_ADMIN_USER("error.domain.identity.admin.user.invalid"),
  USER_ALREADY_EXISTS("error.domain.identity.user.already.exists"),
  USER_NOT_FOUND("error.domain.identity.user.not.found"),
  USER_REFERENCED_BY_ADMIN("error.domain.identity.user.referenced.by.admin"),
  USER_REFERENCED_BY_STAFF("error.domain.identity.user.referenced.by.staff"),
  USER_REFERENCED_BY_STUDENT("error.domain.identity.user.referenced.by.student");

  private final String bundleKey;

  IdentityErrorCodes(String bundleKey) {
    this.bundleKey = bundleKey;
  }
}
