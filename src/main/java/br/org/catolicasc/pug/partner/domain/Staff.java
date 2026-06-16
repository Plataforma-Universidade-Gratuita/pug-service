package br.org.catolicasc.pug.partner.domain;

import br.org.catolicasc.pug.partner.domain.enums.PartnerFieldErrorCodes;
import br.org.catolicasc.pug.shared.domain.DomainError;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/** Immutable Domain Entity representing a Staff member of a Partner Organization. */
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
   * Creates a validated staff aggregate.
   *
   * @param accountId the linked account identifier
   * @param entityId the linked partner entity identifier
   * @return the created and validated aggregate
   */
  public static Staff factory(UUID accountId, UUID entityId) {
    Staff staff = Staff.builder().accountId(accountId).entityId(entityId).build();
    staff.collectValidationProblems();
    return staff;
  }

  /**
   * Moves the staff member to a different partner entity.
   *
   * @param newEntityId the identifier of the target partner entity
   * @return a new {@link Staff} instance reflecting the requested transfer
   */
  public Staff moveToEntity(UUID newEntityId) {
    if (entityId != null && entityId.equals(newEntityId)) {
      return this;
    }
    Staff updated = toBuilder().entityId(newEntityId).build();
    updated.collectValidationProblems();
    return updated;
  }

  private void collectValidationProblems() {
    if (accountId == null) {
      addFieldError(PartnerFieldErrorCodes.INVALID_ACCOUNT_ID_BLANK);
    }
    if (entityId == null) {
      addFieldError(PartnerFieldErrorCodes.INVALID_ENTITY_ID_BLANK);
    }
  }
}
