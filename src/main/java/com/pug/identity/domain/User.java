package com.pug.identity.domain;

import com.pug.shared.domain.exceptions.AppValidationException;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(of = "id")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public final class User {
  private final UUID id;
  private final Cpf cpf;
  private final String name;

  private void validate() {
    if (cpf == null) throw new AppValidationException(IdentityErrorCodes.IDENTITY_CPF_REQUIRED);
    if (name == null || name.isBlank())
      throw new AppValidationException(IdentityErrorCodes.IDENTITY_NAME_REQUIRED);
    if (name.length() > 150)
      throw new AppValidationException(IdentityErrorCodes.IDENTITY_NAME_TOO_LONG);
  }

  public static class UserBuilder {
    public User build() {
      User u = new User(id, cpf, name);
      u.validate();
      return u;
    }
  }
}
