package com.pug.partner.domain;

import com.pug.identity.domain.User;
import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.shared.exceptions.AppValidationException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/** Domain model representing a Staff member associated with a User and an Entity. */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Staff {
  private final User user;
  private final Entity entity;

  /**
   * Validates the Staff instance to ensure all required fields are properly set.
   *
   * @throws AppValidationException if validation fails.
   */
  private void validate() {
    if (user == null || user.getId() == null) {
      throw new AppValidationException(PartnerErrorCodes.INVALID_STAFF_USER);
    }
    if (entity == null || entity.getId() == null) {
      throw new AppValidationException(PartnerErrorCodes.INVALID_STAFF_ENTITY);
    }
  }

  /** Builder class for constructing Staff instances with validation. */
  public static class StaffBuilder {
    /**
     * Builds the Staff instance and performs validation.
     *
     * @return a validated Staff instance.
     * @throws AppValidationException if validation fails.
     */
    public Staff build() {
      Staff s = new Staff(user, entity);
      s.validate();
      return s;
    }
  }
}
