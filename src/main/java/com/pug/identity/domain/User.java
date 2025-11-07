package com.pug.identity.domain;

import com.pug.identity.domain.errors.IdentityErrorCodes;
import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.domain.vos.Email;
import com.pug.shared.domain.enums.AccountType;
import com.pug.shared.exceptions.AppValidationException;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/** Domain entity representing a User with validation logic. */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {

  private final UUID id;
  private final Cpf cpf;
  private final String name;
  private final Email email;
  private final AccountType accountType;
  private final String passwordHash;
  private final Boolean active;
  private final OffsetDateTime createdAt;

  /**
   * Validates the User object's fields according to business rules.
   *
   * @throws AppValidationException if any validation fails.
   */
  private void validate() {
    if (cpf == null) {
      throw new AppValidationException(IdentityErrorCodes.INVALID_CPF);
    }
    if (email == null) {
      throw new AppValidationException(IdentityErrorCodes.INVALID_EMAIL_BLANK);
    }

    if (name == null || name.isBlank()) {
      throw new AppValidationException(IdentityErrorCodes.INVALID_USER_NAME_BLANK);
    }
    if (name.length() > 150) {
      throw new AppValidationException(IdentityErrorCodes.INVALID_USER_NAME_TOOLONG);
    }

    if (accountType == null) {
      throw new AppValidationException(IdentityErrorCodes.INVALID_ACCOUNT_TYPE);
    }

    if (passwordHash != null && passwordHash.length() > 255) {
      throw new AppValidationException(IdentityErrorCodes.INVALID_PASSWORD_HASH_TOOLONG);
    }

    if (active == null) {
      throw new AppValidationException(IdentityErrorCodes.INVALID_ACTIVE_NULL);
    }

    if (createdAt != null && createdAt.isAfter(OffsetDateTime.now())) {
      throw new AppValidationException(IdentityErrorCodes.INVALID_CREATED_AT_FUTURE);
    }
  }

  /** Custom builder to include validation upon building the User object. */
  public static class UserBuilder {
    /**
     * Builds the User object and validates it.
     *
     * @return Validated User object.
     */
    public User build() {
      User u = new User(id, cpf, name, email, accountType, passwordHash, active, createdAt);
      u.validate();
      return u;
    }
  }
}
