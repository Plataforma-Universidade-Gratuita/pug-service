package com.pug.shared.exceptions;

import com.pug.shared.errors.GenericErrorCodes;

public class ResourceNotFoundException extends DomainException {
  public ResourceNotFoundException(GenericErrorCodes code) {
    super(code);
  }

  public ResourceNotFoundException(GenericErrorCodes code, Throwable cause) {
    super(code, cause);
  }
}
