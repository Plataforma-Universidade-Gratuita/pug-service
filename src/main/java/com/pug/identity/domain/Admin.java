package com.pug.identity.domain;

import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.shared.domain.DomainError;
import com.pug.shared.exceptions.AppValidationException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Admin entity aggregate.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
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
   * @return new Admin instance (may contain errors)
   */
  public static Admin factory(UUID accountId) {
    Admin admin = Admin.builder().accountId(accountId).grantedAt(OffsetDateTime.now()).build();
    admin.collectValidationProblems();
    return admin;
  }

  /**
   * Validates the Admin instance.
   */
  private void collectValidationProblems() {
    validateForeignKeyField(accountId, "accountId");

    if (grantedAt == null) {
      addError(new AppValidationException.Problem(IdentityErrorCodes.INVALID_GRANTED_AT_BLANK));
    }
  }
}
