package com.pug.identity.domain.vos;

import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.commons.validator.routines.EmailValidator;
import org.jetbrains.annotations.NotNull;

/**
 * Value object representing an email address.
 *
 * @param value the email address as a String.
 */
public record Email(String value) {

  private static final EmailValidator EMAIL_VALIDATOR = EmailValidator.getInstance();

  /**
   * Constructs an Email value object and validates the input.
   *
   * @param value the email address as a String
   * @throws AppValidationException if the email is null, empty, too long, or has an invalid format.
   *     This exception may contain multiple validation problems.
   */
  public Email {
    List<AppValidationException.Problem> problems = new ArrayList<>();

    if (StringUtils.isEmpty(value)) {
      problems.add(
          new AppValidationException.Problem(IdentityErrorCodes.INVALID_EMAIL_BLANK, "email"));
    } else {
      String trimmedValue = value.trim();

      if (trimmedValue.length() > 254) {
        problems.add(
            new AppValidationException.Problem(IdentityErrorCodes.INVALID_EMAIL_LENGTH, "email"));
      }

      if (!EMAIL_VALIDATOR.isValid(trimmedValue)) {
        problems.add(
            new AppValidationException.Problem(IdentityErrorCodes.INVALID_EMAIL_FORMAT, "email"));
      }

      if (problems.isEmpty()) {
        value = trimmedValue.toLowerCase(Locale.ROOT);
      }
    }

    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }
  }

  /**
   * Returns the string representation of the email.
   *
   * @return the email address as a String.
   */
  @Override
  public @NotNull String toString() {
    return value;
  }
}
