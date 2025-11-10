package com.pug.identity.domain;

import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.domain.vos.Email;
import com.pug.shared.domain.enums.AccountType;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.text.StringUtils;
import com.pug.shared.time.TimeProvider;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/** User aggregate root. */
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
  private final OffsetDateTime createdAt;

  /** Factory for new users. */
  public static User createNew(
      Cpf cpf, String name, Email email, AccountType type, String passwordHash, TimeProvider time) {
    var created = OffsetDateTime.now(time.clock());
    var u = new User(null, cpf, StringUtils.trim(name), email, type, passwordHash, created);
    u.validateAt(time.clock());
    return u;
  }

  /**
   * Behavior: change name.
   *
   * @param newName new name.
   * @return new User instance with changed name.
   */
  public User changeName(String newName) {
    User u = this.toBuilder().name(StringUtils.trim(newName)).build();
    u.validate();
    return u;
  }

  /**
   * Behavior: change email. Uniqueness is enforced at repository/service level.
   *
   * @param newEmail new email.
   * @return new User instance with changed email.
   */
  public User changeEmail(Email newEmail) {
    User u = this.toBuilder().email(newEmail).build();
    u.validate();
    return u;
  }

  /**
   * Behavior: set password hash produced elsewhere (domain does not hash).
   *
   * @param newHash new password hash.
   * @return new User instance with changed password hash.
   */
  public User setPasswordHash(String newHash) {
    User u = this.toBuilder().passwordHash(newHash).build();
    u.validate();
    return u;
  }

  /**
   * Validates the User instance to ensure all required fields are properly set.
   *
   * @throws AppValidationException if validation fails.
   */
  private void validateAt(Clock clock) {
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
    if (createdAt != null && createdAt.isAfter(OffsetDateTime.now(clock))) {
      throw new AppValidationException(IdentityErrorCodes.INVALID_CREATED_AT_FUTURE);
    }
  }

  /**
   * Validates the User instance using the system UTC clock.
   *
   * @throws AppValidationException if validation fails.
   */
  private void validate() {
    validateAt(Clock.systemUTC());
  }

  /** Builder class for User to enforce validation on build. */
  public static class UserBuilder {
    /**
     * Builds the User instance and validates it.
     *
     * @return the constructed and validated User instance.
     * @throws AppValidationException if validation fails.
     */
    public User build() {
      User u =
          new User(id, cpf, StringUtils.trim(name), email, accountType, passwordHash, createdAt);
      u.validate();
      return u;
    }
  }
}
