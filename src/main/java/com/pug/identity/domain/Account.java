package com.pug.identity.domain;

import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.domain.vos.Email;
import com.pug.shared.domain.enums.AccountType;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.time.TimeProvider;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/** Account entity aggregate. */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Account {
  private final UUID id;
  private final UUID userId;
  private final Email email;
  private final AccountType accountType;
  private final String passwordHash;
  private final OffsetDateTime createdAt;

  /**
   * Factory for new Account.
   *
   * @param userId the ID of the person associated with the Account
   * @param email Account's email
   * @param type the account type for the Account
   * @param passwordHash the password of the Account hashed
   * @param time time provider
   * @return new Account instance
   */
  public static Account createNew(
      UUID userId, Email email, AccountType type, String passwordHash, TimeProvider time) {
    var created = OffsetDateTime.now(time.clock());
    var u = new Account(null, userId, email, type, passwordHash, created);
    u.validateAt(time.clock());
    return u;
  }

  /**
   * Behavior: change the Account's email.
   *
   * @param newEmail new email for the Account
   * @return new Account instance with changed email
   */
  public Account changeEmail(Email newEmail) {
    Account u = this.toBuilder().email(newEmail).build();
    u.validate();
    return u;
  }

  /**
   * Behavior: set the Account's password hash.
   *
   * @param newHash new password hash
   * @return new Account instance with changed password hash
   */
  public Account setPasswordHash(String newHash) {
    Account u = this.toBuilder().passwordHash(newHash).build();
    u.validate();
    return u;
  }

  /**
   * Validates the Account instance to ensure all required fields are properly set.
   *
   * <p>Checks that personId, email, and accountType are not null, passwordHash is within length
   * limits if provided, and createdAt is not in the future.
   *
   * @throws AppValidationException if any validation fails
   */
  private void validateAt(Clock clock) {
    if (userId == null) {
      throw new AppValidationException(IdentityErrorCodes.INVALID_USER_BLANK);
    }
    if (email == null) {
      throw new AppValidationException(IdentityErrorCodes.INVALID_EMAIL_BLANK);
    }
    if (accountType == null) {
      throw new AppValidationException(IdentityErrorCodes.INVALID_ACCOUNT_TYPE_BLANK);
    }
    if (passwordHash.length() > 255) {
      throw new AppValidationException(IdentityErrorCodes.INVALID_PASSWORD_HASH_LENGTH);
    }
    if (createdAt != null && createdAt.isAfter(OffsetDateTime.now(clock))) {
      throw new AppValidationException(IdentityErrorCodes.INVALID_CREATED_AT_FUTURE);
    }
  }

  /**
   * Validates the Account instance using the system UTC clock.
   *
   * @throws AppValidationException if validation fails
   */
  private void validate() {
    validateAt(Clock.systemUTC());
  }

  /**
   * Builder class for Account.
   *
   * <p>Overrides the build method to include validation.
   */
  public static class AccountBuilder {
    /**
     * Builds the Account instance and validates it.
     *
     * @return the constructed and validated Account instance
     */
    public Account build() {
      Account u = new Account(id, userId, email, accountType, passwordHash, createdAt);
      u.validate();
      return u;
    }
  }
}
