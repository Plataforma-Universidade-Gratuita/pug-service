package com.pug.academic.domain.vos;

import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
   * @throws AppValidationException if the registration is null, blank, or too long.
   *                                This exception may contain multiple validation problems.
   */
  public AcademicRegistration {
    List<AppValidationException.Problem> problems = new ArrayList<>();
    String trimmedRegistration = null;

    if (StringUtils.isEmpty(registration)) {
      problems.add(new AppValidationException.Problem(AcademicErrorCodes.INVALID_REGISTRATION_BLANK, "registration"));
    } else {
      trimmedRegistration = registration.trim();

      if (trimmedRegistration.length() > 15) {
        problems.add(new AppValidationException.Problem(AcademicErrorCodes.INVALID_REGISTRATION_LENGTH, "registration"));
      }
    }

    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }

    registration = trimmedRegistration;
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