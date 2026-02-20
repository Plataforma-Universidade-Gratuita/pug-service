package com.pug.shared.exceptions;

import com.pug.shared.domain.Problem;

/**
 * Exception thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends ApplicationException {
  /**
   * Constructs a new ResourceNotFoundException with a specific error code and details.
   *
   * @param problem the problem details to include in the exception.
   */
  public ResourceNotFoundException(Problem problem) {
    super(problem);
  }
}
