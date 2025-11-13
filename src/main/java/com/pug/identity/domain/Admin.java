package com.pug.identity.domain;

import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.time.TimeProvider;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/** Admin entity aggregate. */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Admin {
  private final UUID accountId;
  private final OffsetDateTime grantedAt;

  /**
   * Factory for new Admin.
   *
   * @param accountId the ID of the Account associated with the Admin
   * @param time time provider
   * @return new Admin instance
   */
  public static Admin createNew(UUID accountId, TimeProvider time) {
    var granted = OffsetDateTime.now(time.clock());
    var a = new Admin(accountId, granted);
    a.validate();
    return a;
  }

  /**
   * Validates the Admin instance to ensure all required fields are properly set.
   *
   * <p>Checks that accountId is not null.
   *
   * @throws AppValidationException if any validation fails
   */
  private void validate() {
    if (accountId == null) {
      throw new AppValidationException(IdentityErrorCodes.INVALID_ACCOUNT_BLANK);
    }
  }

  /**
   * Builder class for Admin.
   *
   * <p>Overrides the build method to include validation.
   */
  public static class AdminBuilder {
    /**
     * Builds the Admin instance and performs validation.
     *
     * @return a validated Admin instance
     */
    public Admin build() {
      Admin a = new Admin(accountId, grantedAt);
      a.validate();
      return a;
    }
  }
}
