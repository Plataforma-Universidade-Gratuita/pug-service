package com.pug.identity.domain;

import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.time.TimeProvider;
import lombok.Builder;
import lombok.Getter;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Admin entity aggregate.
 */
@Getter
public class Admin {
  private final UUID accountId;
  private final OffsetDateTime grantedAt;

  /**
   * Private constructor for Admin.
   *
   * @param accountId the ID of the Account associated with the Admin
   * @param grantedAt timestamp when admin rights were granted
   */
  @Builder(toBuilder = true)
  private Admin(UUID accountId, OffsetDateTime grantedAt) {
    this.accountId = accountId;
    this.grantedAt = grantedAt;
  }

  /**
   * Factory for new Admin.
   *
   * @param accountId the ID of the Account associated with the Admin
   * @param time      time provider
   * @return new Admin instance
   * @throws AppValidationException if initial validation fails.
   */
  public static Admin createNew(UUID accountId, TimeProvider time) {
    var granted = OffsetDateTime.now(time.clock());
    Admin admin = Admin.builder().accountId(accountId).grantedAt(granted).build();

    List<AppValidationException.Problem> problems = admin.collectValidationProblems(time.clock());
    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }
    return admin;
  }

  /**
   * Collects all validation problems for the Admin instance.
   *
   * <p>Checks that accountId is not null and grantedAt is not in the future.
   *
   * @param clock The clock to use for time-based validations.
   * @return A list of {@code AppValidationException.Problem} if any validation fails; an empty list otherwise.
   */
  private List<AppValidationException.Problem> collectValidationProblems(Clock clock) {
    List<AppValidationException.Problem> problems = new ArrayList<>();

    if (accountId == null) {
      problems.add(new AppValidationException.Problem(IdentityErrorCodes.INVALID_ACCOUNT_BLANK, "accountId"));
    }
    if (grantedAt == null) {
      problems.add(new AppValidationException.Problem(IdentityErrorCodes.INVALID_GRANTED_AT_BLANK, "grantedAt"));
    } else if (grantedAt.isAfter(OffsetDateTime.now(clock))) {
      problems.add(new AppValidationException.Problem(IdentityErrorCodes.INVALID_GRANTED_AT_FUTURE, "grantedAt"));
    }

    return problems;
  }

  /**
   * Convenience method to collect validation problems using the system UTC clock.
   *
   * @return A list of {@code AppValidationException.Problem} if any validation fails; an empty list otherwise.
   */
  private List<AppValidationException.Problem> collectValidationProblems() {
    return collectValidationProblems(Clock.systemUTC());
  }
}