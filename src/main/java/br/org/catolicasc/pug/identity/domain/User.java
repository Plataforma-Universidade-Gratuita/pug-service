/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.identity.domain;

import br.org.catolicasc.pug.identity.domain.enums.IdentityFieldErrorCodes;
import br.org.catolicasc.pug.identity.domain.vos.Cpf;
import br.org.catolicasc.pug.shared.domain.DomainError;
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
 * Immutable Domain Entity representing a User within the Identity context.
 *
 * <p>This class acts as an aggregate root containing the user's unique identifier, personal
 * identification data (CPF, name), and audit tracking information. It extends {@link DomainError}
 * to accumulate validation failures across its own fields and to bubble up errors from its nested
 * value objects.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class User extends DomainError {

  UUID id;

  Cpf cpf;

  String name;

  AuditInfo auditInfo;

  @Builder(toBuilder = true)
  private User(UUID id, Cpf cpf, String name, AuditInfo auditInfo) {
    this.id = id;
    this.cpf = cpf;
    this.name = name;
    this.auditInfo = auditInfo;
  }

  /**
   * Factory method to create a new {@code User} instance.
   *
   * <p>Automatically generates a time-ordered epoch UUID (UUIDv7) for the identifier, trims the
   * provided name, initializes standard audit tracking information, and performs a full validation
   * of the entity and its contents.
   *
   * @param cpf the {@link Cpf} value object representing the person's CPF
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
   *
   * <p>Since this entity is immutable, this method returns a new {@code User} instance with the
   * updated, trimmed name and a refreshed {@link AuditInfo} timestamp. The new instance is fully
   * re-validated.
   *
   * @param newName the new name to assign to the user
   * @return a new, updated, and validated {@link User} instance, or {@code this} if the name is
   *     unchanged
   */
  public User rename(String newName) {
    String trimmed = StringUtils.trim(newName);
    if (name.equals(trimmed)) {
      return this;
    }
    User updated = toBuilder().name(trimmed).auditInfo(auditInfo.update()).build();
    updated.collectValidationProblems();
    return updated;
  }

  private void collectValidationProblems() {
    validateIdField(id);
    if (cpf == null) {
      addFieldError(IdentityFieldErrorCodes.INVALID_CPF_BLANK);
    } else if (cpf.hasFieldErrors()) {
      addFieldErrors(cpf.getFieldErrors());
    }
    if (StringUtils.isEmpty(name)) {
      addFieldError(IdentityFieldErrorCodes.INVALID_USER_ID_BLANK);
    } else if (name.length() > 150) {
      addFieldError(IdentityFieldErrorCodes.INVALID_USER_ID_TOO_LONG);
    }
    if (auditInfo == null) {
      addFieldError(SharedFieldErrorCodes.INVALID_AUDIT_INFO_BLANK);
    } else if (auditInfo.hasFieldErrors()) {
      addFieldErrors(auditInfo.getFieldErrors());
    }
  }
}
