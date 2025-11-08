package com.pug.identity.domain.vos;

import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.shared.exceptions.AppValidationException;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;

/** Email value object with canonicalization. */
public record Email(String value) {
  private static final String SIMPLE_EMAIL_RX = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";

  /**
   * Constructs an Email value object and validates the input.
   *
   * @param value the email address as a String.
   */
  public Email {
    if (value == null) {
      throw new AppValidationException(IdentityErrorCodes.INVALID_EMAIL_BLANK);
    }
    String v = value.trim();
    if (v.isEmpty()) {
      throw new AppValidationException(IdentityErrorCodes.INVALID_EMAIL_BLANK);
    }
    if (v.length() > 254) {
      throw new AppValidationException(IdentityErrorCodes.INVALID_EMAIL_TOOLONG);
    }
    if (!v.matches(SIMPLE_EMAIL_RX)) {
      throw new AppValidationException(IdentityErrorCodes.INVALID_EMAIL_FORMAT);
    }
    value = v.toLowerCase(Locale.ROOT);
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
