package com.pug.academic.domain.exceptions;

import static com.pug.shared.errors.ErrorCodes.COURSE_DUPLICATE_NAME;

import com.pug.shared.errors.DomainException;

public final class DuplicateAcademicRegistrationException extends DomainException {
  public DuplicateAcademicRegistrationException(String registration) {
    super(COURSE_DUPLICATE_NAME, registration);
  }
}
