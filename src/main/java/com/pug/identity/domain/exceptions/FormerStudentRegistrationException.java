package com.pug.identity.domain.exceptions;

import static com.pug.shared.errors.ErrorCodes.USER_ALREADY_REGISTERED_AS_FORMER_STUDENT;

import com.pug.identity.domain.User;
import com.pug.shared.errors.DomainException;

public final class FormerStudentRegistrationException extends DomainException {
  public FormerStudentRegistrationException(User user) {
    super(USER_ALREADY_REGISTERED_AS_FORMER_STUDENT, user);
  }
}
