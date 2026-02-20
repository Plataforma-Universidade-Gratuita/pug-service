package com.pug.shared.exceptions;

import com.pug.shared.domain.Problem;
import lombok.Getter;

/**
 * Base class for application-specific exceptions. These exceptions encapsulate an error code and
 * optional details, allowing for consistent API error responses.
 */
public abstract class ApplicationException extends RuntimeException {
  @Getter
  private final Problem problem;

  /**
   * Constructor for ApplicationException with a problem and optional details.
   *
   * @param problem The problem associated with this exception.
   */
  protected ApplicationException(Problem problem) {
    super(problem.getMessageKey());
    this.problem = problem;
  }
}
