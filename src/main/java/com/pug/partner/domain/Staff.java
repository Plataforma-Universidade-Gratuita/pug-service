package com.pug.partner.domain;

import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.shared.exceptions.AppValidationException;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Staff entity aggregate.
 */
@Getter
public class Staff {
  private final UUID accountId;
  private final UUID entityId;

  @Builder(toBuilder = true)
  private Staff(UUID accountId, UUID entityId) {
    this.accountId = accountId;
    this.entityId = entityId;
  }

  /**
   * Factory method to create a new Staff instance.
   *
   * @param accountId the unique identifier of the account
   * @param entityId  the unique identifier of the entity
   * @return a validated Staff instance
   * @throws AppValidationException if initial validation fails.
   */
  public static Staff createNew(UUID accountId, UUID entityId) {
    Staff staff = Staff.builder().accountId(accountId).entityId(entityId).build();

    List<AppValidationException.Problem> problems = staff.collectValidationProblems();
    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }
    return staff;
  }

  /**
   * Collects all validation problems for the Staff instance.
   *
   * <p>Checks that accountId and entityId are not null.
   *
   * @return A list of {@code AppValidationException.Problem} if any validation fails; an empty list otherwise.
   */
  public List<AppValidationException.Problem> collectValidationProblems() {
    List<AppValidationException.Problem> problems = new ArrayList<>();

    if (accountId == null) {
      problems.add(new AppValidationException.Problem(PartnerErrorCodes.INVALID_STAFF_ACCOUNT_BLANK, "accountId"));
    }
    if (entityId == null) {
      problems.add(new AppValidationException.Problem(PartnerErrorCodes.INVALID_STAFF_ENTITY_BLANK, "entityId"));
    }

    return problems;
  }
}