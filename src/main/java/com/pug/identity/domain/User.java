package com.pug.identity.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.domain.vos.Cpf;
import com.pug.shared.domain.DomainError;
import com.pug.shared.domain.Problem;
import com.pug.shared.domain.enums.SharedErrorCodes;
import com.pug.shared.domain.vos.AuditInfo;
import com.pug.shared.utils.StringUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/** User entityId aggregate. */
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
   * Factory for new users.
   *
   * @param cpf person's CPF
   * @param name person's name
   * @return new User instance (may contain errors)
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
   * Behavior: change the person's name.
   *
   * @param newName new name of the person
   * @return new User instance with changed name
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
   * Behavior: change the person's CPF.
   *
   * @param newCpf new CPF for the person
   * @return new User instance with changed CPF
   */
  public User changeCpf(Cpf newCpf) {
    if (cpf.equals(newCpf)) {
      return this;
    }
    User updated = toBuilder().cpf(newCpf).auditInfo(auditInfo.update()).build();
    updated.collectValidationProblems();
    return updated;
  }

  /** Validates the User instance. */
  private void collectValidationProblems() {
    validateIdField(id);
    validateStringField(name, 150L, "name");
    if (cpf == null) {
      addError(new Problem(IdentityErrorCodes.INVALID_CPF_BLANK));
    } else if (cpf.hasErrors()) {
      addErrors(cpf.getProblems());
    }
    if (auditInfo == null) {
      addError(new Problem(SharedErrorCodes.INVALID_AUDIT_INFO_BLANK));
    } else if (auditInfo.hasErrors()) {
      addErrors(auditInfo.getProblems());
    }
  }
}
