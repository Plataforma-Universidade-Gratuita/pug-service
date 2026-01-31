package com.pug.identity.service.utils;

import com.pug.identity.domain.Admin;
import com.pug.shared.time.TimeProvider;
import java.util.UUID;

/** Utility class for processing Admin DTO inputs. */
public class AdminProcessor {

  /**
   * Helper method to process DTO input and build a new Admin domain object.
   *
   * @param accountId The ID of the account being granted admin rights.
   * @param timeProvider The time provider for creation timestamp.
   * @return The constructed Admin domain object.
   */
  public static Admin processCreateInput(UUID accountId, TimeProvider timeProvider) {
    return Admin.factory(accountId, timeProvider);
  }
}
