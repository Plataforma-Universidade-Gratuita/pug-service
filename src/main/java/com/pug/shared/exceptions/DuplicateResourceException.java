package com.pug.shared.exceptions;

import com.pug.shared.domain.Problem;
import com.pug.shared.domain.enums.GenericErrorCodes;
import com.pug.shared.presenter.rest.Details;

/**
 * Exception thrown when attempting to create or add a resource that already exists.
 */
public class DuplicateResourceException extends ApplicationException {
  /**
   * Constructs a new DuplicateResourceException with the specified error code and details.
   *
   * @param problem the problem containing the error code and details about the duplicate resource error
   */
  public DuplicateResourceException(Problem problem) {
    super(problem);
  }
}
