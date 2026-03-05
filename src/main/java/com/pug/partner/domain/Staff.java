package com.pug.partner.domain;

import com.pug.partner.domain.enums.PartnerFieldErrorCodes;
import com.pug.shared.domain.DomainError;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/**
 * Immutable Domain Entity representing a Staff member of a Partner Organization.
 *
 * <p>This class maps a specific authentication account directly to a partner {@link Entity}. It
 * serves as an aggregate for managing employment or organizational affiliations within the partner
 * domain. It extends {@link DomainError} to accumulate validation failures.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
public class Staff extends DomainError {

  /** The unique identifier of the linked authentication account. */
  UUID accountId;

  /** The unique identifier of the linked partner entity. */
  UUID entityId;

  /**
   * Constructs a {@code Staff} instance.
   *
   * @param accountId the unique identifier of the account
   * @param entityId the unique identifier of the partner entity
   */
  @Builder(toBuilder = true)
  private Staff(UUID accountId, UUID entityId) {
    this.accountId = accountId;
    this.entityId = entityId;
  }

  /**
   * Factory method to create a new {@code Staff} instance.
   *
   * <p>The instance is created and immediately self-validated. Any validation failures are
   * accumulated internally and can be retrieved via {@link #getFieldErrors()}.
   *
   * @param accountId the unique identifier of the account
   * @param entityId the unique identifier of the partner entity
   * @return a self-validated {@link Staff} instance
   */
  public static Staff factory(UUID accountId, UUID entityId) {
    Staff staff = Staff.builder().accountId(accountId).entityId(entityId).build();

    staff.collectValidationProblems();
    return staff;
  }

  /**
   * Evaluates constraints for the Staff entity and aggregates any validation problems.
   *
   * <p>Rules applied:
   *
   * <ul>
   *   <li>Ensures the {@code accountId} is not null (appends {@link
   *       PartnerFieldErrorCodes#INVALID_ACCOUNT_ID_BLANK})
   *   <li>Ensures the {@code entityId} is not null (appends {@link
   *       PartnerFieldErrorCodes#INVALID_ENTITY_ID_BLANK})
   * </ul>
   */
  private void collectValidationProblems() {
    if (accountId == null) {
      addFieldError(PartnerFieldErrorCodes.INVALID_ACCOUNT_ID_BLANK);
    }
    if (entityId == null) {
      addFieldError(PartnerFieldErrorCodes.INVALID_ENTITY_ID_BLANK);
    }
  }
}
