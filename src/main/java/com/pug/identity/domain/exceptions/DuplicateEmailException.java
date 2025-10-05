package com.pug.identity.domain.exceptions;

import static com.pug.shared.errors.ErrorCodes.ROLE_DUPLICATE_EMAIL;

import com.pug.shared.errors.DomainException;

public final class DuplicateEmailException extends DomainException {
  public DuplicateEmailException(String email) {
    super(ROLE_DUPLICATE_EMAIL, email);
  }
}
