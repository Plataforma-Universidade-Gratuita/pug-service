package com.pug.partner.domain;

import com.pug.shared.domain.DomainError;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/** Staff entityId aggregate. */
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
   * @param entityId the unique identifier of the entityId
   * @return a Staff instance (may contain errors)
   */
  public static Staff factory(UUID accountId, UUID entityId) {
    Staff staff = Staff.builder().accountId(accountId).entityId(entityId).build();

    staff.collectValidationProblems();
    return staff;
  }

  /** Collects all validation problems for the Staff instance. */
  private void collectValidationProblems() {
    validateForeignKeyField(accountId, "accountId");
    validateForeignKeyField(entityId, "entityId");
  }
}
