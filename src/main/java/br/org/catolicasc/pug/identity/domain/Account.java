package br.org.catolicasc.pug.identity.domain;

import br.org.catolicasc.pug.identity.domain.enums.IdentityFieldErrorCodes;
import br.org.catolicasc.pug.identity.domain.vos.Email;
import br.org.catolicasc.pug.shared.domain.DomainError;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.domain.enums.SharedFieldErrorCodes;
import br.org.catolicasc.pug.shared.domain.vos.AuditInfo;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import com.github.f4b6a3.uuid.UuidCreator;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/**
 * Immutable Domain Entity representing an authentication Account.
 *
 * <p>This class acts as an aggregate managing the credentials, authorization scopes (via {@link
 * AccountType}), and connection to a specific {@link User} entity. It extends {@link DomainError}
 * to accumulate structural validation failures.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Account extends DomainError {

  /** The unique identifier for the account (UUIDv7). */
  UUID id;

  /** The unique identifier of the {@link User} this account belongs to. */
  UUID userId;

  /** The validated email address used for logging into the account. */
  Email email;

  /** The defined role/type of the account (e.g., ADMIN, FORMER_STUDENT). */
  AccountType accountType;

  /** The securely hashed representation of the user's password. */
  String passwordHash;

  /** The audit tracking information (creation and update timestamps). */
  AuditInfo auditInfo;

  /** Flag indicating whether the account is active or has been deactivated. */
  Boolean active;

  /**
   * Constructs an {@code Account} instance.
   *
   * @param id the unique identifier
   * @param userId the linked user's identifier
   * @param email the account's email VO
   * @param accountType the classification of the account
   * @param passwordHash the hashed password, or {@code null} when setup is intentionally deferred
   * @param auditInfo the audit tracking VO
   */
  @Builder(toBuilder = true)
  private Account(
      UUID id,
      UUID userId,
      Email email,
      AccountType accountType,
      String passwordHash,
      AuditInfo auditInfo,
      Boolean active) {
    this.id = id;
    this.userId = userId;
    this.email = email;
    this.accountType = accountType;
    this.passwordHash = passwordHash;
    this.auditInfo = auditInfo;
    this.active = active;
  }

  /**
   * Factory method to create a new {@code Account} instance.
   *
   * <p>Automatically generates a time-ordered epoch UUID (UUIDv7) for the identifier, initializes
   * standard audit tracking information, and performs a full validation of the entity.
   *
   * @param userId the UUID of the person associated with the Account
   * @param email the {@link Email} value object
   * @param type the {@link AccountType} assigning the role
   * @param passwordHash the securely hashed password string, or {@code null} when the account is
   *     intentionally created without local credentials
   * @return a newly created and self-validated {@link Account} instance
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
            .active(true)
            .build();

    account.collectValidationProblems();
    return account;
  }

  /**
   * Returns a copy of this account marked as inactive.
   *
   * @return the updated account, or this instance when it is already inactive
   */
  public Account deactivate() {
    if (Boolean.FALSE.equals(active)) {
      return this;
    }
    Account updated = toBuilder().active(false).auditInfo(auditInfo.update()).build();
    updated.collectValidationProblems();
    return updated;
  }

  /**
   * Returns a copy of this account marked as active.
   *
   * @return the updated account, or this instance when it is already active
   */
  public Account activate() {
    if (Boolean.TRUE.equals(active)) {
      return this;
    }
    Account updated = toBuilder().active(true).auditInfo(auditInfo.update()).build();
    updated.collectValidationProblems();
    return updated;
  }

  /**
   * Returns a copy of this account with a new email address.
   *
   * @param newEmail the replacement email value object
   * @return the updated account, or this instance when the email is unchanged
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
   * Returns a copy of this account with a new password hash.
   *
   * @param newHash the replacement password hash, or {@code null} to clear deferred credentials
   * @return the updated account, or this instance when the hash is unchanged
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

  /**
   * Aggregates every structural validation rule that defines a valid account aggregate.
   *
   * <p>This method validates identifiers, linked value objects, account type, optional password
   * hash length, audit information, and activation state. Any failure is collected into this
   * aggregate through the inherited field-error accumulator instead of failing fast.
   */
  private void collectValidationProblems() {
    validateIdField(id);
    if (userId == null) {
      addFieldError(IdentityFieldErrorCodes.INVALID_USER_ID_BLANK);
    }
    if (email == null) {
      addFieldError(IdentityFieldErrorCodes.INVALID_EMAIL_BLANK);
    } else if (email.hasFieldErrors()) {
      addFieldErrors(email.getFieldErrors());
    }
    if (accountType == null) {
      addFieldError(IdentityFieldErrorCodes.INVALID_ACCOUNT_TYPE_BLANK);
    }
    if (StringUtils.isNotEmpty(passwordHash) && passwordHash.length() > 255) {
      addFieldError(IdentityFieldErrorCodes.INVALID_PASSWORD_HASH_TOO_LONG);
    }
    if (auditInfo == null) {
      addFieldError(SharedFieldErrorCodes.INVALID_AUDIT_INFO_BLANK);
    } else if (auditInfo.hasFieldErrors()) {
      addFieldErrors(auditInfo.getFieldErrors());
    }
    if (active == null) {
      addFieldError(IdentityFieldErrorCodes.INVALID_ACTIVE_FLAG_BLANK);
    }
  }
}
