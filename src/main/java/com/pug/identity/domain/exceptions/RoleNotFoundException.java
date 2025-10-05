package com.pug.identity.domain.exceptions;

import static com.pug.shared.errors.ErrorCodes.ROLE_NOT_FOUND;

import com.pug.shared.errors.DomainException;
import java.util.UUID;

public final class RoleNotFoundException extends DomainException {
  public RoleNotFoundException(UUID id) {
    super(ROLE_NOT_FOUND, id);
  }

  public RoleNotFoundException(String email) {
    super(ROLE_NOT_FOUND, email);
  }
}
