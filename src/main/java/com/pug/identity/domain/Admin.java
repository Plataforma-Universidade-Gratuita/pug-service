package com.pug.identity.domain;

import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.shared.exceptions.AppValidationException;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Admin entity aggregate.
 */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Admin {
  private final UUID userId;
  private final OffsetDateTime grantedAt;

  /**
   * Validates the Admin instance to ensure all required fields are properly set.
   *
   * <p>Checks that userId is not null.</p>
   *
   * @throws AppValidationException if any validation fails
   */
  private void validate() {
    if (userId == null) {
      throw new AppValidationException(IdentityErrorCodes.INVALID_ADMIN_USER);
    }
  }

  /**
   * Builder class for Admin.
   * <p>Overrides the build method to include validation.</p>
   */
  public static class AdminBuilder {
    /**
     * Builds the Admin instance and performs validation.
     *
     * @return a validated Admin instance
     */
    public Admin build() {
      Admin a = new Admin(userId, grantedAt);
      a.validate();
      return a;
    }
  }
}
