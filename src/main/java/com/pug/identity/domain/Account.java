package com.pug.identity.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.domain.vos.Email;
import com.pug.shared.domain.DomainError;
import com.pug.shared.domain.Problem;
import com.pug.shared.domain.enums.AccountType;
import com.pug.shared.domain.enums.SharedErrorCodes;
import com.pug.shared.domain.vos.AuditInfo;
import com.pug.shared.utils.StringUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/** Account entityId aggregate. */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Account extends DomainError {
  UUID id;
  UUID userId;
  Email email;
  AccountType accountType;
  String passwordHash;
  AuditInfo auditInfo;

  @Builder(toBuilder = true)
  private Account(
      UUID id,
      UUID userId,
      Email email,
      AccountType accountType,
      String passwordHash,
      AuditInfo auditInfo) {
    this.id = id;
    this.userId = userId;
    this.email = email;
    this.accountType = accountType;
    this.passwordHash = passwordHash;
    this.auditInfo = auditInfo;
  }

  /**
   * Factory for new Account.
   *
   * @param userId the ID of the person associated with the Account
   * @param email Account's email
   * @param type the account type for the Account
   * @param passwordHash the password of the Account hashed
   * @return new Account instance (may contain errors)
   */
  public static Account factory(UUID userId, Email email, AccountType type, String passwordHash) {
    Account account =
        Account.builder()
            .id(UuidCreator.getTimeOrderedEpoch())
            .userId(userId)
            .email(email)
            .accountType(type)
            .passwordHash(passwordHash)
            .auditInfo(AuditInfo.factory())
            .build();

    account.collectValidationProblems();
    return account;
  }

  /**
   * Behavior: change the Account's email.
   *
   * @param newEmail new email for the Account
   * @return new Account instance with changed email
   */
  public Account changeEmail(Email newEmail) {
    if (email.equals(newEmail)) {
      return this;
    }
    Account updated = toBuilder().email(newEmail).auditInfo(auditInfo.update()).build();
    updated.collectValidationProblems();
    return updated;
  }

  /**
   * Behavior: set the Account's password hash.
   *
   * @param newHash new password hash
   * @return new Account instance with changed password hash
   */
  public Account changePasswordHash(String newHash) {
    if (StringUtils.isEmpty(newHash) && StringUtils.isEmpty(passwordHash)) {
      return this;
    }
    if (newHash != null && newHash.equals(passwordHash)) {
      return this;
    }
    Account updated = toBuilder().passwordHash(newHash).auditInfo(auditInfo.update()).build();
    updated.collectValidationProblems();
    return updated;
  }

  /** Behavior: Activate/Deactivate logic could go here if needed. For now, just validation. */
  private void collectValidationProblems() {
    validateIdField(id);
    validateForeignKeyField(userId, "userId");
    validateStringField(passwordHash, 255L, "passwordHash");
    if (accountType == null) {
      addError(new Problem(IdentityErrorCodes.INVALID_ACCOUNT_TYPE_BLANK));
    }
    if (email == null) {
      addError(new Problem(IdentityErrorCodes.INVALID_EMAIL_BLANK));
    } else if (email.hasErrors()) {
      addErrors(email.getProblems());
    }
    if (auditInfo == null) {
      addError(new Problem(SharedErrorCodes.INVALID_AUDIT_INFO_BLANK));
    } else if (auditInfo.hasErrors()) {
      addErrors(auditInfo.getProblems());
    }
  }
}
