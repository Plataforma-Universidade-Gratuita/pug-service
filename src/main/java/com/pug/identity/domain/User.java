package com.pug.identity.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.domain.vos.Cpf;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.time.TimeProvider;
import com.pug.shared.utils.StringUtils;
import lombok.Builder;
import lombok.Getter;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * User entity aggregate.
 */
@Getter
public class User {
  private final UUID id;
  private final Cpf cpf;
  private final String name;
  private final OffsetDateTime createdAt;

  /**
   * Private constructor for User.
   *
   * @param id        the unique identifier of the User
   * @param cpf       person's CPF
   * @param name      person's name
   * @param createdAt timestamp when the User was created
   */
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
   * @param cpf  person's CPF
   * @param name person's name
   * @param time time provider
   * @return new User instance
   * @throws AppValidationException if initial validation fails.
   */
  public static User createNew(Cpf cpf, String name, TimeProvider time) {
    var created = OffsetDateTime.now(time.clock());

    User user =
            User.builder()
                    .id(UuidCreator.getTimeOrderedEpoch())
                    .cpf(cpf)
                    .name(StringUtils.trim(name))
                    .createdAt(created)
                    .build();

    List<AppValidationException.Problem> problems = user.collectValidationProblems(time.clock());
    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }
    return user;
  }

  /**
   * Behavior: change the person's name.
   *
   * @param newName new name of the person
   * @return new User instance with changed name
   * @throws AppValidationException if validation fails.
   */
  public User changeName(String newName) {
    User updatedUser = this.toBuilder().name(StringUtils.trim(newName)).build();
    List<AppValidationException.Problem> problems = updatedUser.collectValidationProblems(Clock.systemUTC());
    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }
    return updatedUser;
  }

  /**
   * Behavior: change the person's CPF.
   *
   * @param newCpf new CPF for the person
   * @return new User instance with changed CPF
   * @throws AppValidationException if validation fails.
   */
  public User changeCpf(Cpf newCpf) {
    User updatedUser = this.toBuilder().cpf(newCpf).build();
    List<AppValidationException.Problem> problems = updatedUser.collectValidationProblems(Clock.systemUTC());
    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }
    return updatedUser;
  }

  /**
   * Collects all validation problems for the User instance.
   *
   * <p>Checks that id, cpf, and name are not null, name is not blank and within length limits,
   * and createdAt is not in the future.
   *
   * @param clock The clock to use for time-based validations.
   * @return A list of {@code AppValidationException.Problem} if any validation fails; an empty list otherwise.
   */
  private List<AppValidationException.Problem> collectValidationProblems(Clock clock) {
    List<AppValidationException.Problem> problems = new ArrayList<>();

    if (id == null) {
      problems.add(new AppValidationException.Problem(IdentityErrorCodes.INVALID_ID_BLANK, "id"));
    }
    if (cpf == null) {
      problems.add(new AppValidationException.Problem(IdentityErrorCodes.INVALID_CPF_BLANK, "cpf"));
    }
    if (StringUtils.isEmpty(name)) {
      problems.add(new AppValidationException.Problem(IdentityErrorCodes.INVALID_NAME_BLANK, "name"));
    } else if (name.length() > 150) {
      problems.add(new AppValidationException.Problem(IdentityErrorCodes.INVALID_NAME_LENGTH, "name"));
    }
    if (createdAt == null) {
      problems.add(new AppValidationException.Problem(IdentityErrorCodes.INVALID_CREATED_AT_BLANK, "createdAt"));
    } else if (createdAt.isAfter(OffsetDateTime.now(clock))) {
      problems.add(new AppValidationException.Problem(IdentityErrorCodes.INVALID_CREATED_AT_FUTURE, "createdAt"));
    }

    return problems;
  }

  /**
   * Convenience method to collect validation problems using the system UTC clock.
   *
   * @return A list of {@code AppValidationException.Problem} if any validation fails; an empty list otherwise.
   */
  private List<AppValidationException.Problem> collectValidationProblems() {
    return collectValidationProblems(Clock.systemUTC());
  }
}