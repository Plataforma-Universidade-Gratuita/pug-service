package com.pug.identity.domain.exceptions;

import static com.pug.shared.errors.ErrorCodes.USER_NOT_FOUND;

import com.pug.shared.errors.DomainException;
import java.util.UUID;

public final class UserNotFoundException extends DomainException {
  public UserNotFoundException(UUID id) {
    super(USER_NOT_FOUND, id);
  }

  public UserNotFoundException(String cpf) {
    super(USER_NOT_FOUND, cpf);
  }
}
