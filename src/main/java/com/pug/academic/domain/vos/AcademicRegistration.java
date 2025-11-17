package com.pug.academic.domain.vos;

import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Value Object representing an Academic Registration.
 *
 * @param registration the registration string
 */
public record AcademicRegistration(String registration) {
  /**
   * Constructs an AcademicRegistration after validating the input.
   *
   * @param registration the registration string
   * @throws AppValidationException if the registration is null, blank, or too long
   */
  public AcademicRegistration {
    if (StringUtils.isEmpty(registration)) {
      throw new AppValidationException(AcademicErrorCodes.INVALID_REGISTRATION_BLANK);
    }
    String r = registration.trim();
    if (r.length() > 15) {
      throw new AppValidationException(AcademicErrorCodes.INVALID_REGISTRATION_LENGTH);
    }
    registration = r;
  }

  /**
   * Returns the string representation of the Academic Registration.
   *
   * @return the registration string
   */
  @Override
  public @NotNull String toString() {
    return registration;
  }
}
