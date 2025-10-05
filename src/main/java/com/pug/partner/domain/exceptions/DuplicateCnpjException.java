package com.pug.partner.domain.exceptions;

import static com.pug.shared.errors.ErrorCodes.USER_DUPLICATE_CPF;

import com.pug.shared.errors.DomainException;

public final class DuplicateCnpjException extends DomainException {
  public DuplicateCnpjException(String cnpj) {
    super(USER_DUPLICATE_CPF, cnpj);
  }
}
