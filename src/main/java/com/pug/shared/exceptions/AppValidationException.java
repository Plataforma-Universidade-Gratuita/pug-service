package com.pug.shared.exceptions;

import com.pug.shared.errors.GenericErrorCodes;
import java.util.Map;

/** Exception class for application validation errors. */
public class AppValidationException extends DomainException {
  /**
   * Creates a new AppValidationException with the specified error code.
   *
   * @param code The generic error code associated with this exception.
   */
  public AppValidationException(GenericErrorCodes code) {
    super(code);
  }

  /**
   * Creates a new AppValidationException with the specified error code and cause.
   *
   * @param code The generic error code associated with this exception.
   * @param cause The cause of this exception.
   */
  public AppValidationException(GenericErrorCodes code, Throwable cause) {
    super(code, cause);
  }

  /**
   * Creates a new AppValidationException with the specified error code and additional details.
   *
   * @param code The generic error code associated with this exception.
   * @param details A map of additional details related to this exception.
   */
  public AppValidationException(GenericErrorCodes code, Map<String, Object> details) {
    super(code, details);
  }
}
