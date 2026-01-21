package com.pug.identity.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.domain.vos.Email;
import com.pug.shared.domain.enums.AccountType;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.time.TimeProvider;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

/** Account entity aggregate. */
@Getter
public class Account {
  private final UUID id;
  private final UUID userId;
  private final Email email;
  private final AccountType accountType;
  private final String passwordHash;
  private final OffsetDateTime createdAt;

  /**
   * Private constructor for Account.
   *
   * @param id the unique identifier of the Account
   * @param userId the ID of the person associated with the Account
   * @param email Account's email
   * @param accountType the account type for the Account
   * @param passwordHash the password of the Account hashed
   * @param createdAt timestamp when the Account was created
   */
  @Builder(toBuilder = true)
  private Account(
      UUID id,
      UUID userId,
      Email email,
      AccountType accountType,
      String passwordHash,
      OffsetDateTime createdAt) {
    this.id = id;
    this.userId = userId;
    this.email = email;
    this.accountType = accountType;
    this.passwordHash = passwordHash;
    this.createdAt = createdAt;
  }

  /**
   * Factory for new Account.
   *
   * @param userId the ID of the person associated with the Account
   * @param email Account's email
   * @param type the account type for the Account
   * @param passwordHash the password of the Account hashed
   * @param time time provider
   * @return new Account instance
   * @throws AppValidationException if initial validation fails.
   */
  public static Account createNew(
      UUID userId, Email email, AccountType type, String passwordHash, TimeProvider time) {
    var created = OffsetDateTime.now(time.clock());

    Account account =
        Account.builder()
            .id(UuidCreator.getTimeOrderedEpoch())
            .userId(userId)
            .email(email)
            .accountType(type)
            .passwordHash(passwordHash)
            .createdAt(created)
            .build();

    List<AppValidationException.Problem> problems = account.collectValidationProblems(time.clock());
    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }
    return account;
  }

  /**
   * Behavior: change the Account's email.
   *
   * @param newEmail new email for the Account
   * @return new Account instance with changed email
   * @throws AppValidationException if validation fails.
   */
  public Account changeEmail(Email newEmail) {
    Account updatedAccount = this.toBuilder().email(newEmail).build();
    List<AppValidationException.Problem> problems =
        updatedAccount.collectValidationProblems(Clock.systemUTC());
    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }
    return updatedAccount;
  }

  /**
   * Behavior: set the Account's password hash.
   *
   * @param newHash new password hash
   * @return new Account instance with changed password hash
   * @throws AppValidationException if validation fails.
   */
  public Account setPasswordHash(String newHash) {
    Account updatedAccount = this.toBuilder().passwordHash(newHash).build();
    List<AppValidationException.Problem> problems =
        updatedAccount.collectValidationProblems(Clock.systemUTC());
    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }
    return updatedAccount;
  }

  /**
   * Collects all validation problems for the Account instance.
   *
   * <p>Checks that personId, email, and accountType are not null, passwordHash is within length
   * limits if provided, and createdAt is not in the future.
   *
   * @param clock The clock to use for time-based validations.
   * @return A list of {@code AppValidationException.Problem} if any validation fails; an empty list
   *     otherwise.
   */
  private List<AppValidationException.Problem> collectValidationProblems(Clock clock) {
    List<AppValidationException.Problem> problems = new ArrayList<>();

    if (id == null) {
      problems.add(new AppValidationException.Problem(IdentityErrorCodes.INVALID_ID_BLANK, "id"));
    }
    if (userId == null) {
      problems.add(
          new AppValidationException.Problem(IdentityErrorCodes.INVALID_USER_ID_BLANK, "userId"));
    }
    if (email == null) {
      problems.add(
          new AppValidationException.Problem(IdentityErrorCodes.INVALID_EMAIL_BLANK, "email"));
    }
    if (accountType == null) {
      problems.add(
          new AppValidationException.Problem(
              IdentityErrorCodes.INVALID_ACCOUNT_TYPE_BLANK, "accountType"));
    }
    if (passwordHash == null || passwordHash.isBlank()) {
      problems.add(
          new AppValidationException.Problem(
              IdentityErrorCodes.INVALID_PASSWORD_HASH_BLANK, "passwordHash"));
    } else if (passwordHash.length() > 255) {
      problems.add(
          new AppValidationException.Problem(
              IdentityErrorCodes.INVALID_PASSWORD_HASH_LENGTH, "passwordHash"));
    }
    if (createdAt == null) {
      problems.add(
          new AppValidationException.Problem(
              IdentityErrorCodes.INVALID_CREATED_AT_BLANK, "createdAt"));
    } else if (createdAt.isAfter(OffsetDateTime.now(clock))) {
      problems.add(
          new AppValidationException.Problem(
              IdentityErrorCodes.INVALID_CREATED_AT_FUTURE, "createdAt"));
    }

    return problems;
  }

  /**
   * Convenience method to collect validation problems using the system UTC clock.
   *
   * @return A list of {@code AppValidationException.Problem} if any validation fails; an empty list
   *     otherwise.
   */
  private List<AppValidationException.Problem> collectValidationProblems() {
    return collectValidationProblems(Clock.systemUTC());
  }
}
