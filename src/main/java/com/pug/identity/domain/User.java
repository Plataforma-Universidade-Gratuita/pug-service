package com.pug.identity.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import com.pug.identity.domain.enums.IdentityFieldErrorCodes;
import com.pug.identity.domain.vos.Cpf;
import com.pug.shared.domain.DomainError;
import com.pug.shared.domain.enums.SharedFieldErrorCodes;
import com.pug.shared.domain.vos.AuditInfo;
import com.pug.shared.utils.StringUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

import java.util.UUID;

/**
 * Immutable Domain Entity representing a User within the Identity context.
 * <p>
 * This class acts as an aggregate root containing the user's unique identifier,
 * personal identification data (CPF, name), and audit tracking information.
 * It extends {@link DomainError} to accumulate validation failures across its
 * own fields and to bubble up errors from its nested value objects.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class User extends DomainError {

  /**
   * The unique identifier for the user (UUIDv7).
   */
  UUID id;

  /**
   * The validated CPF Value Object associated with the user.
   */
  Cpf cpf;

  /**
   * The full name of the user.
   */
  String name;

  /**
   * The audit tracking information (creation and update timestamps).
   */
  AuditInfo auditInfo;

  /**
   * Constructs a {@code User} instance.
   *
   * @param id        the unique identifier
   * @param cpf       the user's CPF VO
   * @param name      the user's name
   * @param auditInfo the audit tracking VO
   */
  @Builder(toBuilder = true)
  private User(UUID id, Cpf cpf, String name, AuditInfo auditInfo) {
    this.id = id;
    this.cpf = cpf;
    this.name = name;
    this.auditInfo = auditInfo;
  }

  /**
   * Factory method to create a new {@code User} instance.
   * <p>
   * Automatically generates a time-ordered epoch UUID (UUIDv7) for the identifier,
   * trims the provided name, initializes standard audit tracking information,
   * and performs a full validation of the entity and its contents.
   *
   * @param cpf  the {@link Cpf} value object representing the person's CPF
   * @param name the name of the person
   * @return a newly created and self-validated {@link User} instance
   */
  public static User factory(Cpf cpf, String name) {
    User user =
            User.builder()
                    .id(UuidCreator.getTimeOrderedEpoch())
                    .cpf(cpf)
                    .name(StringUtils.trim(name))
                    .auditInfo(AuditInfo.factory())
                    .build();

    user.collectValidationProblems();
    return user;
  }

  /**
   * Updates the user's name.
   * <p>
   * Since this entity is immutable, this method returns a new {@code User} instance
   * with the updated, trimmed name and a refreshed {@link AuditInfo} timestamp.
   * The new instance is fully re-validated.
   *
   * @param newName the new name to assign to the user
   * @return a new, updated, and validated {@link User} instance, or {@code this} if the name is unchanged
   */
  public User changeName(String newName) {
    String trimmed = StringUtils.trim(newName);
    if (name.equals(trimmed)) {
      return this;
    }
    User updated = toBuilder().name(trimmed).auditInfo(auditInfo.update()).build();
    updated.collectValidationProblems();
    return updated;
  }

  /**
   * Updates the user's CPF.
   * <p>
   * Returns a new, re-validated {@code User} instance with the updated CPF and refreshed
   * {@link AuditInfo} timestamp, maintaining the immutability of the domain entity.
   *
   * @param newCpf the new {@link Cpf} to assign to the user
   * @return a new, updated, and validated {@link User} instance, or {@code this} if the CPF is unchanged
   */
  public User changeCpf(Cpf newCpf) {
    if (cpf.equals(newCpf)) {
      return this;
    }
    User updated = toBuilder().cpf(newCpf).auditInfo(auditInfo.update()).build();
    updated.collectValidationProblems();
    return updated;
  }

  /**
   * Evaluates constraints for the User entity and aggregates any validation problems.
   * <p>
   * Rules applied:
   * <ul>
   *   <li>Validates the UUID (inherited from {@link DomainError})</li>
   *   <li>Ensures the {@code cpf} is not null and bubbles up any internal {@link Cpf} errors</li>
   *   <li>Ensures the {@code name} is not blank and does not exceed 255 characters
   *       (appends {@link IdentityFieldErrorCodes#INVALID_USER_ID_BLANK} or {@link IdentityFieldErrorCodes#INVALID_USER_ID_TOO_LONG})</li>
   *   <li>Ensures the {@code auditInfo} is not null and bubbles up any internal errors</li>
   * </ul>
   */
  private void collectValidationProblems() {
    validateIdField(id);
    if (cpf == null) {
      addFieldError(IdentityFieldErrorCodes.INVALID_CPF_BLANK);
    } else if (cpf.hasFieldErrors()) {
      addFieldErrors(cpf.getFieldErrors());
    }
    if (StringUtils.isEmpty(name)) {
      addFieldError(IdentityFieldErrorCodes.INVALID_USER_ID_BLANK);
    } else if (name.length() > 255) {
      addFieldError(IdentityFieldErrorCodes.INVALID_USER_ID_TOO_LONG);
    }
    if (auditInfo == null) {
      addFieldError(SharedFieldErrorCodes.INVALID_AUDIT_INFO_BLANK);
    } else if (auditInfo.hasFieldErrors()) {
      addFieldErrors(auditInfo.getFieldErrors());
    }
  }
}