package com.pug.academic.domain.exceptions;

import static com.pug.shared.errors.ErrorCodes.FIELD_OF_STUDY_NOT_FOUND;

import com.pug.shared.errors.DomainException;
import java.util.UUID;

public final class FieldOfStudyNotFoundException extends DomainException {
  public FieldOfStudyNotFoundException(UUID id) {
    super(FIELD_OF_STUDY_NOT_FOUND, id);
  }
}
