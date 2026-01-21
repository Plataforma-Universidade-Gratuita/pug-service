package com.pug.shared.exceptions;

import com.pug.shared.domain.enums.GenericErrorCodes;
import java.util.Collections;
import java.util.List;
import lombok.Getter;

/** Exception for application validation errors, which can include multiple problems. */
@Getter
public class AppValidationException extends RuntimeException {

  /**
   * Represents an individual validation problem.
   *
   * @param code The error code (implements GenericErrorCodes).
   * @param fieldName The name of the field associated with the error (can be null).
   */
  public record Problem(GenericErrorCodes code, String fieldName) {}

  private final List<Problem> problems;

  /**
   * Constructor for multiple validation problems.
   *
   * @param problems The list of validation problems.
   */
  public AppValidationException(List<Problem> problems) {
    super("Application validation failed.");
    if (problems == null || problems.isEmpty()) {
      throw new IllegalArgumentException(
          "AppValidationException must contain at least one problem.");
    }
    this.problems = Collections.unmodifiableList(problems);
  }

  /**
   * Convenience constructor for a single validation problem.
   *
   * @param code The error code (implements GenericErrorCodes).
   * @param fieldName The name of the field associated with the error (can be null).
   */
  public AppValidationException(GenericErrorCodes code, String fieldName) {
    this(List.of(new Problem(code, fieldName)));
  }
}
