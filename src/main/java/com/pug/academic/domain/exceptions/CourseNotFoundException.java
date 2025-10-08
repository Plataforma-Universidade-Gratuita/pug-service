package com.pug.academic.domain.exceptions;

import static com.pug.shared.errors.ErrorCodes.COURSE_NOT_FOUND;

import com.pug.shared.errors.DomainException;
import java.util.UUID;

public final class CourseNotFoundException extends DomainException {
  public CourseNotFoundException(UUID id) {
    super(COURSE_NOT_FOUND, id);
  }
}
