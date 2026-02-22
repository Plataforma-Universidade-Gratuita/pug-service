package com.pug.partner.service.utils;

import com.pug.partner.domain.Staff;
import java.util.UUID;

/** Utility class for processing Staff DTO inputs. */
public class StaffProcessor {

  /**
   * Helper method to process input and build a new Staff domain object.
   *
   * @param accountId The ID of the associated account.
   * @param entityId The ID of the associated entity.
   * @return The constructed Staff domain object.
   */
  public static Staff processCreateInput(UUID accountId, UUID entityId) {
    return Staff.factory(accountId, entityId);
  }
}
