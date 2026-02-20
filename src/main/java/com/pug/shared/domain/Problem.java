package com.pug.shared.domain;

import com.pug.shared.domain.enums.GenericErrorCodes;
import java.io.Serializable;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an individual validation problem.
 *
 * @param code The error code (implements GenericErrorCodes).
 */
public record Problem(GenericErrorCodes code, String customFieldName) implements Serializable {

  /**
   * Constructor for a problem with only an error code.
   *
   * @param code The error code (implements GenericErrorCodes).
   */
  public Problem(GenericErrorCodes code) {
    this(code, null);
  }

  /**
   * Returns the final field name to be used for this problem, which is either the custom field name
   * if provided, or the default field name from the error code.
   *
   * @return the final field name as a String
   */
  public String getFinalFieldName() {
    return customFieldName() != null ? customFieldName() : code().getFieldName();
  }

  /**
   * Returns the error code as a string, which is the name of the enum constant.
   *
   * @return the error code as a String
   */
  public String getErrorCode() {
    return ((Enum<?>) code()).name();
  }

  /**
   * Returns the message key for internationalization, which is obtained from the error code's
   * bundle key.
   *
   * @return the message key as a String
   */
  public String getMessageKey() {
    return code().getBundleKey();
  }

  /**
   * Returns a string representation of the problem, including the error code and optional custom
   * field name.
   *
   * @return a string representation of the problem
   */
  @Override
  public @NotNull String toString() {
    return "Problem{"
        + "code="
        + code
        + (customFieldName != null ? ", customFieldName='" + customFieldName + '\'' : "")
        + '}';
  }
}
