package com.pug.partner.domain.exceptions;

import static com.pug.shared.errors.ErrorCodes.STAFF_DUPLICATE_USER_ROLE_ID;

import com.pug.shared.errors.DomainException;
import java.util.UUID;

public final class DuplicateStaffException extends DomainException {
  public DuplicateStaffException(UUID userRoleId) {
    super(STAFF_DUPLICATE_USER_ROLE_ID, userRoleId);
  }
}
