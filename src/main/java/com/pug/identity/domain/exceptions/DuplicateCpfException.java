package com.pug.identity.domain.exceptions;

import static com.pug.shared.errors.ErrorCodes.USER_DUPLICATE_CPF;

import com.pug.shared.errors.DomainException;

public final class DuplicateCpfException extends DomainException {
  public DuplicateCpfException(String cpf) {
    super(USER_DUPLICATE_CPF, cpf);
  }
}
