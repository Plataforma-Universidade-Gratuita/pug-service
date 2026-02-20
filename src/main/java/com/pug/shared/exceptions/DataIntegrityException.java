package com.pug.shared.exceptions;

import com.pug.shared.domain.Problem;
import com.pug.shared.domain.enums.SharedErrorCodes;

/**
 * Exception thrown when an entity cannot be deleted or modified because it is referenced by other
 * entities.
 */
public class DataIntegrityException extends ApplicationException {
  /**
   * Constructs a new ReferencedEntityException with the specified error code and details.
   */
  public DataIntegrityException() {
    super(new Problem(SharedErrorCodes.DATA_INTEGRITY_ERROR));
  }
}
