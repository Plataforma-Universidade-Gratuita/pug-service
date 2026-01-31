package com.pug.identity.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.domain.vos.Cpf;
import com.pug.shared.domain.DomainError;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.time.TimeProvider;
import com.pug.shared.utils.StringUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/** User entity aggregate. */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class User extends DomainError {
  UUID id;
  Cpf cpf;
  String name;
  OffsetDateTime createdAt;

  @Builder(toBuilder = true)
  private User(UUID id, Cpf cpf, String name, OffsetDateTime createdAt) {
    this.id = id;
    this.cpf = cpf;
    this.name = name;
    this.createdAt = createdAt;
  }

  /**
   * Factory for new users.
   *
   * @param cpf person's CPF
   * @param name person's name
   * @param time time provider
   * @return new User instance (may contain errors)
   */
  public static User factory(Cpf cpf, String name, TimeProvider time) {
    var created = OffsetDateTime.now(time.clock());

    User user =
        User.builder()
            .id(UuidCreator.getTimeOrderedEpoch())
            .cpf(cpf)
            .name(StringUtils.trim(name))
            .createdAt(created)
            .build();

    user.collectValidationProblems(time.clock());
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
    if (this.name.equals(trimmed)) {
      return this;
    }
    User updated = this.toBuilder().name(trimmed).build();
    updated.collectValidationProblems(Clock.systemUTC());
    return updated;
  }

  /**
   * Behavior: change the person's CPF.
   *
   * @param newCpf new CPF for the person
   * @return new User instance with changed CPF
   */
  public User changeCpf(Cpf newCpf) {
    if (this.cpf.equals(newCpf)) {
      return this;
    }
    User updated = this.toBuilder().cpf(newCpf).build();
    updated.collectValidationProblems(Clock.systemUTC());
    return updated;
  }

  /** Validates the User instance. */
  private void collectValidationProblems(Clock clock) {
    if (id == null) {
      addError(new AppValidationException.Problem(IdentityErrorCodes.INVALID_ID_BLANK));
    }

    if (cpf == null) {
      addError(new AppValidationException.Problem(IdentityErrorCodes.INVALID_CPF_BLANK));
    } else if (cpf.hasErrors()) {
      addErrors(cpf.getProblems());
    }

    if (StringUtils.isEmpty(name)) {
      addError(new AppValidationException.Problem(IdentityErrorCodes.INVALID_NAME_BLANK));
    } else if (name.length() > 150) {
      addError(new AppValidationException.Problem(IdentityErrorCodes.INVALID_NAME_LENGTH));
    }

    if (createdAt == null) {
      addError(new AppValidationException.Problem(IdentityErrorCodes.INVALID_CREATED_AT_BLANK));
    } else if (createdAt.isAfter(OffsetDateTime.now(clock))) {
      addError(new AppValidationException.Problem(IdentityErrorCodes.INVALID_CREATED_AT_FUTURE));
    }
  }
}
