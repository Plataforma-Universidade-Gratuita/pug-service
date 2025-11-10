package com.pug.academic.domain.vos;

import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.shared.exceptions.AppValidationException;
import org.jetbrains.annotations.NotNull;

/** Value Object representing an Academic Registration. */
public record AcademicRegistration(String registration) {
  /**
   * Constructs an AcademicRegistration after validating the input.
   *
   * @param registration the registration string
   * @throws AppValidationException if the registration is invalid
   */
  public AcademicRegistration {
    if (registration == null) {
      throw new AppValidationException(AcademicErrorCodes.INVALID_REGISTRATION);
    }
    String r = registration.trim();
    if (r.isBlank()) {
      throw new AppValidationException(AcademicErrorCodes.INVALID_REGISTRATION);
    }
    if (r.length() > 15) {
      throw new AppValidationException(AcademicErrorCodes.INVALID_REGISTRATION_TOOLONG);
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
