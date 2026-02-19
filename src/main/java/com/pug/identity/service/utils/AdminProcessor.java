package com.pug.identity.service.utils;

import com.pug.identity.domain.Admin;

import java.util.UUID;

/**
 * Utility class for processing Admin DTO inputs.
 */
public class AdminProcessor {

  /**
   * Helper method to process DTO input and build a new Admin domain object.
   *
   * @param accountId The ID of the account being granted admin rights.
   * @return The constructed Admin domain object.
   */
  public static Admin processCreateInput(UUID accountId) {
    return Admin.factory(accountId);
  }
}
