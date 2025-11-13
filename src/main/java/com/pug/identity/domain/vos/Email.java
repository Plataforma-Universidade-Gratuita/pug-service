package com.pug.identity.domain.vos;

import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.utils.StringUtils;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;

/**
 * Value object representing an email address.
 *
 * @param value the email address as a String.
 */
public record Email(String value) {
  private static final String SIMPLE_EMAIL_RX = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";

  /**
   * Constructs an Email value object and validates the input.
   *
   * @param value the email address as a String
   * @throws AppValidationException if the email is null, empty, too long, or has an invalid format
   */
  public Email {
    if (StringUtils.isEmpty(value)) {
      throw new AppValidationException(IdentityErrorCodes.INVALID_EMAIL_BLANK);
    }
    String v = value.trim();
    if (v.length() > 254) {
      throw new AppValidationException(IdentityErrorCodes.INVALID_EMAIL_LENGTH);
    }
    if (!v.matches(SIMPLE_EMAIL_RX)) {
      throw new AppValidationException(IdentityErrorCodes.INVALID_EMAIL_FORMAT);
    }
    value = v.toLowerCase(Locale.ROOT);
  }

  /**
   * Returns the string representation of the email.
   *
   * @return the email address as a String
   */
  @Override
  public @NotNull String toString() {
    return value;
  }
}
