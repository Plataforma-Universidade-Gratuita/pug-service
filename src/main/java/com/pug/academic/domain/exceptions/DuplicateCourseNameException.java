package com.pug.academic.domain.exceptions;

import static com.pug.shared.errors.ErrorCodes.COURSE_DUPLICATE_NAME;

import com.pug.shared.errors.DomainException;

public final class DuplicateCourseNameException extends DomainException {
  public DuplicateCourseNameException(String name) {
    super(COURSE_DUPLICATE_NAME, name);
  }
}
