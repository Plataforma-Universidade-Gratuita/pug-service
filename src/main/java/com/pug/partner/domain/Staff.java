package com.pug.partner.domain;

import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.shared.domain.DomainError;
import com.pug.shared.exceptions.AppValidationException;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/** Staff entity aggregate. */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
public class Staff extends DomainError {
  UUID accountId;
  UUID entityId;

  @Builder(toBuilder = true)
  private Staff(UUID accountId, UUID entityId) {
    this.accountId = accountId;
    this.entityId = entityId;
  }

  /**
   * Factory method to create a new Staff instance.
   *
   * @param accountId the unique identifier of the account
   * @param entityId the unique identifier of the entity
   * @return a Staff instance (may contain errors)
   */
  public static Staff factory(UUID accountId, UUID entityId) {
    Staff staff = Staff.builder().accountId(accountId).entityId(entityId).build();

    staff.collectValidationProblems();
    return staff;
  }

  /** Collects all validation problems for the Staff instance. */
  private void collectValidationProblems() {
    if (accountId == null) {
      addError(new Problem(PartnerErrorCodes.INVALID_STAFF_ACCOUNT_BLANK));
    }
    if (entityId == null) {
      addError(new Problem(PartnerErrorCodes.INVALID_STAFF_ENTITY_BLANK));
    }
  }
}
