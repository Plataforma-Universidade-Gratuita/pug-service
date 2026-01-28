package com.pug.identity.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.domain.vos.Email;
import com.pug.shared.domain.DomainError;
import com.pug.shared.domain.enums.AccountType;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.time.TimeProvider;
import com.pug.shared.utils.StringUtils;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Account entity aggregate.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
public class Account extends DomainError {
  UUID id;
  UUID userId;
  Email email;
  AccountType accountType;
  String passwordHash;
  OffsetDateTime createdAt;

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
   * @param userId       the ID of the person associated with the Account
   * @param email        Account's email
   * @param type         the account type for the Account
   * @param passwordHash the password of the Account hashed
   * @param time         time provider
   * @return new Account instance (may contain errors)
   */
  public static Account factory(
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

    account.collectValidationProblems(time.clock());
    return account;
  }

  /**
   * Behavior: change the Account's email.
   *
   * @param newEmail new email for the Account
   * @return new Account instance with changed email
   */
  public Account changeEmail(Email newEmail) {
    if (this.email.equals(newEmail)) {
      return this;
    }
    Account updated = this.toBuilder().email(newEmail).build();
    updated.collectValidationProblems(Clock.systemUTC());
    return updated;
  }

  /**
   * Behavior: set the Account's password hash.
   *
   * @param newHash new password hash
   * @return new Account instance with changed password hash
   */
  public Account changePasswordHash(String newHash) {
    if (StringUtils.isEmpty(newHash) && StringUtils.isEmpty(this.passwordHash)) {
      return this;
    }
    if (newHash != null && newHash.equals(this.passwordHash)) {
      return this;
    }
    Account updated = this.toBuilder().passwordHash(newHash).build();
    updated.collectValidationProblems(Clock.systemUTC());
    return updated;
  }

  /**
   * Behavior: Activate/Deactivate logic could go here if needed.
   * For now, just validation.
   */
  private void collectValidationProblems(Clock clock) {
    if (id == null) {
      addError(new AppValidationException.Problem(IdentityErrorCodes.INVALID_ID_BLANK));
    }

    if (userId == null) {
      addError(new AppValidationException.Problem(IdentityErrorCodes.INVALID_USER_ID_BLANK));
    }

    if (email == null) {
      addError(new AppValidationException.Problem(IdentityErrorCodes.INVALID_EMAIL_BLANK));
    } else if (email.hasErrors()) {
      addErrors(email.getProblems());
    }

    if (accountType == null) {
      addError(new AppValidationException.Problem(IdentityErrorCodes.INVALID_ACCOUNT_TYPE_BLANK));
    }

    if (StringUtils.isEmpty(passwordHash)) {
      addError(new AppValidationException.Problem(IdentityErrorCodes.INVALID_PASSWORD_HASH_BLANK));
    } else if (passwordHash.length() > 255) {
      addError(new AppValidationException.Problem(IdentityErrorCodes.INVALID_PASSWORD_HASH_LENGTH));
    }

    if (createdAt == null) {
      addError(new AppValidationException.Problem(IdentityErrorCodes.INVALID_CREATED_AT_BLANK));
    } else if (createdAt.isAfter(OffsetDateTime.now(clock))) {
      addError(new AppValidationException.Problem(IdentityErrorCodes.INVALID_CREATED_AT_FUTURE));
    }
  }
}