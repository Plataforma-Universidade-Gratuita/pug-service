package com.pug.shared.exceptions;

import com.pug.shared.errors.GenericErrorCodes;
import java.util.Map;

public class AppValidationException extends DomainException {
  public AppValidationException(GenericErrorCodes code) {
    super(code);
  }

  public AppValidationException(GenericErrorCodes code, Throwable cause) {
    super(code, cause);
  }

  public AppValidationException(GenericErrorCodes code, Map<String, Object> details) {
    super(code, details);
  }
}
