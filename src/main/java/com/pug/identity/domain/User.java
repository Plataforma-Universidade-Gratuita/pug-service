package com.pug.identity.domain;

import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.domain.vos.Email;
import com.pug.shared.domain.enums.AccountType;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.utils.StringUtils;
import com.pug.shared.time.TimeProvider;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * User entity aggregate.
 */
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

  /**
   * Factory for new users.
   *
   * @param cpf user's CPF
   * @param name user's name
   * @param email user's email
   * @param type the account type for the user
   * @param passwordHash the password of the user hashed
   * @param time time provider
   * @return new User instance
   */
  public static User createNew(
      Cpf cpf, String name, Email email, AccountType type, String passwordHash, TimeProvider time) {
    var created = OffsetDateTime.now(time.clock());
    var u = new User(null, cpf, StringUtils.trim(name), email, type, passwordHash, created);
    u.validateAt(time.clock());
    return u;
  }

  /**
   * Behavior: change the user's name.
   *
   * @param newName new name of the user
   * @return new User instance with changed name
   */
  public User changeName(String newName) {
    User u = this.toBuilder().name(StringUtils.trim(newName)).build();
    u.validate();
    return u;
  }

  /**
   * Behavior: change the user's email.
   *
   * @param newEmail new email for the user
   * @return new User instance with changed email
   */
  public User changeEmail(Email newEmail) {
    User u = this.toBuilder().email(newEmail).build();
    u.validate();
    return u;
  }

  /**
   * Behavior: set the user's password hash.
   *
   * @param newHash new password hash
   * @return new User instance with changed password hash
   */
  public User setPasswordHash(String newHash) {
    User u = this.toBuilder().passwordHash(newHash).build();
    u.validate();
    return u;
  }

  /**
   * Validates the User instance to ensure all required fields are properly set.
   *
   * <p>Checks that cpf, email, name, accountType are not null, name is not blank and within length limits,
   * passwordHash is within length limits if provided, and createdAt is not in the future.</p>
   *
   * @throws AppValidationException if any validation fails
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
   * @throws AppValidationException if validation fails
   */
  private void validate() {
    validateAt(Clock.systemUTC());
  }

  /**
   * Builder class for User.
   * <p>Overrides the build method to include validation.</p>
   */
  public static class UserBuilder {
    /**
     * Builds the User instance and validates it.
     *
     * @return the constructed and validated User instance
     */
    public User build() {
      User u =
          new User(id, cpf, StringUtils.trim(name), email, accountType, passwordHash, createdAt);
      u.validate();
      return u;
    }
  }
}
