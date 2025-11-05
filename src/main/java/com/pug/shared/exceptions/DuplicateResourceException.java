package com.pug.shared.exceptions;

import com.pug.shared.errors.GenericErrorCodes;

public class DuplicateResourceException extends DomainException {
  public DuplicateResourceException(GenericErrorCodes code) {
    super(code);
  }

  public DuplicateResourceException(GenericErrorCodes code, Throwable cause) {
    super(code, cause);
  }
}
