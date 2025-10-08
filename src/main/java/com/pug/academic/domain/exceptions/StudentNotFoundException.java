package com.pug.academic.domain.exceptions;

import static com.pug.shared.errors.ErrorCodes.STUDENT_NOT_FOUND;

import com.pug.shared.errors.DomainException;
import java.util.UUID;

public final class StudentNotFoundException extends DomainException {
  public StudentNotFoundException(UUID id) {
    super(STUDENT_NOT_FOUND, id);
  }

  public StudentNotFoundException(String academicRegistration) {
    super(STUDENT_NOT_FOUND, academicRegistration);
  }
}
