package com.pug.identity.domain;

import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.shared.domain.DomainError;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.time.TimeProvider;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Admin entity aggregate.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
public class Admin extends DomainError {
  UUID accountId;
  OffsetDateTime grantedAt;

  @Builder(toBuilder = true)
  private Admin(UUID accountId, OffsetDateTime grantedAt) {
    this.accountId = accountId;
    this.grantedAt = grantedAt;
  }

  /**
   * Factory for new Admin.
   *
   * @param accountId the ID of the Account associated with the Admin
   * @param time      time provider
   * @return new Admin instance (may contain errors)
   */
  public static Admin factory(UUID accountId, TimeProvider time) {
    var granted = OffsetDateTime.now(time.clock());
    Admin admin = Admin.builder().accountId(accountId).grantedAt(granted).build();

    admin.collectValidationProblems(time.clock());
    return admin;
  }

  /**
   * Validates the Admin instance.
   */
  private void collectValidationProblems(Clock clock) {
    if (accountId == null) {
      addError(new AppValidationException.Problem(IdentityErrorCodes.INVALID_ACCOUNT_BLANK));
    }

    if (grantedAt == null) {
      addError(new AppValidationException.Problem(IdentityErrorCodes.INVALID_GRANTED_AT_BLANK));
    } else if (grantedAt.isAfter(OffsetDateTime.now(clock))) {
      addError(new AppValidationException.Problem(IdentityErrorCodes.INVALID_GRANTED_AT_FUTURE));
    }
  }
}