package com.pug.identity.domain;

import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.domain.vos.Cpf;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.time.TimeProvider;
import com.pug.shared.utils.StringUtils;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/** User entity aggregate. */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {
  private final UUID id;
  private final Cpf cpf;
  private final String name;
  private final OffsetDateTime createdAt;

  /**
   * Factory for new users.
   *
   * @param cpf person's CPF
   * @param name person's name
   * @param time time provider
   * @return new User instance
   */
  public static User createNew(Cpf cpf, String name, TimeProvider time) {
    var created = OffsetDateTime.now(time.clock());
    var p = new User(null, cpf, StringUtils.trim(name), created);
    p.validateAt(time.clock());
    return p;
  }

  /**
   * Behavior: change the person's name.
   *
   * @param newName new name of the person
   * @return new User instance with changed name
   */
  public User changeName(String newName) {
    User p = this.toBuilder().name(StringUtils.trim(newName)).build();
    p.validate();
    return p;
  }

  /**
   * Behavior: change the person's CPF.
   *
   * @param newCpf new CPF for the person
   * @return new User instance with changed CPF
   */
  public User changeCpf(Cpf newCpf) {
    User p = this.toBuilder().cpf(newCpf).build();
    p.validate();
    return p;
  }

  /**
   * Validates the User instance to ensure all required fields are properly set.
   *
   * <p>Checks that cpf, and name are not null, name is not blank and within length limits and
   * createdAt is not in the future.
   *
   * @throws AppValidationException if any validation fails
   */
  private void validateAt(Clock clock) {
    if (cpf == null) {
      throw new AppValidationException(IdentityErrorCodes.INVALID_CPF_BLANK);
    }
    if (StringUtils.isEmpty(name)) {
      throw new AppValidationException(IdentityErrorCodes.INVALID_NAME_BLANK);
    }
    if (name.length() > 150) {
      throw new AppValidationException(IdentityErrorCodes.INVALID_NAME_LENGTH);
    }
    if (createdAt != null && createdAt.isAfter(OffsetDateTime.now(clock))) {
      throw new AppValidationException(IdentityErrorCodes.INVALID_CREATED_AT_FUTURE);
    }
  }

  /**
   * Validates the User instance using the system UTC clock.
   *
   * @throws AppValidationException if validation fails
   */
  private void validate() {
    validateAt(Clock.systemUTC());
  }

  /**
   * Builder class for User.
   *
   * <p>Overrides the build method to include validation.
   */
  public static class UserBuilder {
    /**
     * Builds the User instance and validates it.
     *
     * @return the constructed and validated User instance
     */
    public User build() {
      User p = new User(id, cpf, StringUtils.trim(name), createdAt);
      p.validate();
      return p;
    }
  }
}
