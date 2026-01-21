package com.pug.shared.exceptions;

import com.pug.shared.domain.enums.GenericErrorCodes;
import java.util.Map;

/** Exception thrown when attempting to create or add a resource that already exists. */
public class DuplicateResourceException extends ApplicationException {
  /**
   * Constructs a new DuplicateResourceException with the specified error code and details.
   *
   * @param errorCode the generic error code representing the duplication error
   * @param details additional details about the error
   */
  public DuplicateResourceException(GenericErrorCodes errorCode, Map<String, Object> details) {
    super(errorCode, details);
  }

  /**
   * Constructs a new DuplicateResourceException with the specified error code.
   *
   * @param errorCode the generic error code representing the duplication error
   */
  public DuplicateResourceException(GenericErrorCodes errorCode) {
    super(errorCode);
  }
}
