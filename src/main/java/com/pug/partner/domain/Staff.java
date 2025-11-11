package com.pug.partner.domain;

import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.shared.exceptions.AppValidationException;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Staff entity aggregate.
 */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Staff {
  private final UUID userId;
  private final UUID entityId;

  /**
   * Factory method to create a new Staff instance with validation.
   *
   * @param userId the unique identifier of the user
   * @param entityId the unique identifier of the entity
   * @return a validated Staff instance
   */
  public static Staff createNew(UUID userId, UUID entityId) {
    Staff s = new Staff(userId, entityId);
    s.validate();
    return s;
  }

  /**
   * Validates the Staff instance to ensure all required fields are properly set.
   *
   * <p>Checks that userId and entityId are not null.</p>
   *
   * @throws AppValidationException if validation fails
   */
  private void validate() {
    if (userId == null) {
      throw new AppValidationException(PartnerErrorCodes.INVALID_STAFF_USER);
    }
    if (entityId == null) {
      throw new AppValidationException(PartnerErrorCodes.INVALID_STAFF_ENTITY);
    }
  }

  /**
   * Builder class for Staff.
   * <p>Overrides the build method to include validation.</p>
   */
  public static class StaffBuilder {
    /**
     * Builds the Staff instance and performs validation.
     *
     * @return a validated Staff instance
     */
    public Staff build() {
      return createNew(userId, entityId);
    }
  }
}
