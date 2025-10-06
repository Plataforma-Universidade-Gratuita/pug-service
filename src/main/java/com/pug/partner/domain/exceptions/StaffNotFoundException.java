package com.pug.partner.domain.exceptions;

import static com.pug.shared.errors.ErrorCodes.STAFF_NOT_FOUND;

import com.pug.shared.errors.DomainException;
import java.util.UUID;

public final class StaffNotFoundException extends DomainException {
  public StaffNotFoundException(UUID userRoleId) {
    super(STAFF_NOT_FOUND, userRoleId);
  }
}
