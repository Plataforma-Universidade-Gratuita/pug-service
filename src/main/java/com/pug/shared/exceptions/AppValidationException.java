package com.pug.shared.exceptions;

import com.pug.shared.domain.enums.GenericErrorCodes;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import lombok.Getter;

/** Exception for application validation errors, which can include multiple problems. */
@Getter
public final class AppValidationException extends RuntimeException {

  @Serial private static final long serialVersionUID = 1L;

  /**
   * Represents an individual validation problem.
   *
   * @param code The error code (implements GenericErrorCodes).
   */
  public record Problem(GenericErrorCodes code) implements Serializable {}

  @SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
  private final transient List<Problem> problems;

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
   */
  public AppValidationException(GenericErrorCodes code) {
    this(List.of(new Problem(code)));
  }
}
