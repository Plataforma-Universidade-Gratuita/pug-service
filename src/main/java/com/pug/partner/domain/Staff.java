package com.pug.partner.domain;

import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.shared.exceptions.AppValidationException;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/** Staff entity aggregate. */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Staff {
  private final UUID accountId;
  private final UUID entityId;

  /**
   * Factory method to create a new Staff instance with validation.
   *
   * @param accountId the unique identifier of the account
   * @param entityId the unique identifier of the entity
   * @return a validated Staff instance
   */
  public static Staff createNew(UUID accountId, UUID entityId) {
    Staff s = new Staff(accountId, entityId);
    s.validate();
    return s;
  }

  /**
   * Validates the Staff instance to ensure all required fields are properly set.
   *
   * <p>Checks that accountId and entityId are not null.
   *
   * @throws AppValidationException if validation fails
   */
  private void validate() {
    if (accountId == null) {
      throw new AppValidationException(PartnerErrorCodes.INVALID_STAFF_ACCOUNT_BLANK);
    }
    if (entityId == null) {
      throw new AppValidationException(PartnerErrorCodes.INVALID_STAFF_ENTITY_BLANK);
    }
  }

  /**
   * Builder class for Staff.
   *
   * <p>Overrides the build method to include validation.
   */
  public static class StaffBuilder {
    /**
     * Builds the Staff instance and performs validation.
     *
     * @return a validated Staff instance
     */
    public Staff build() {
      return createNew(accountId, entityId);
    }
  }
}
