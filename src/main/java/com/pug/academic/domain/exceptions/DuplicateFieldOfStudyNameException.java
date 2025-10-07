package com.pug.academic.domain.exceptions;

import static com.pug.shared.errors.ErrorCodes.FIELD_OF_STUDY_DUPLICATE_NAME;

import com.pug.shared.errors.DomainException;

public final class DuplicateFieldOfStudyNameException extends DomainException {
  public DuplicateFieldOfStudyNameException(String name) {
    super(FIELD_OF_STUDY_DUPLICATE_NAME, name);
  }
}
