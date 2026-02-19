package com.pug.identity.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.domain.vos.Cpf;
import com.pug.shared.domain.DomainError;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.utils.StringUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * User entity aggregate.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class User extends DomainError {
  UUID id;
  Cpf cpf;
  String name;
  OffsetDateTime createdAt;
  OffsetDateTime updatedAt;

  @Builder(toBuilder = true)
  private User(UUID id, Cpf cpf, String name, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
    this.id = id;
    this.cpf = cpf;
    this.name = name;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  /**
   * Factory for new users.
   *
   * @param cpf  person's CPF
   * @param name person's name
   * @return new User instance (may contain errors)
   */
  public static User factory(Cpf cpf, String name) {
    var created = OffsetDateTime.now();

    User user =
            User.builder()
                    .id(UuidCreator.getTimeOrderedEpoch())
                    .cpf(cpf)
                    .name(StringUtils.trim(name))
                    .createdAt(created)
                    .updatedAt(created)
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
    User updated = toBuilder().name(trimmed).updatedAt(OffsetDateTime.now()).build();
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
    User updated = toBuilder().cpf(newCpf).updatedAt(OffsetDateTime.now()).build();
    updated.collectValidationProblems();
    return updated;
  }

  /**
   * Validates the User instance.
   */
  private void collectValidationProblems() {
    validateIdField(id);
    validateStringField(name, 150L, "name");
    validateAuditedFields(createdAt, updatedAt);

    if (cpf == null) {
      addError(new AppValidationException.Problem(IdentityErrorCodes.INVALID_CPF_BLANK));
    } else if (cpf.hasErrors()) {
      addErrors(cpf.getProblems());
    }
  }
}
