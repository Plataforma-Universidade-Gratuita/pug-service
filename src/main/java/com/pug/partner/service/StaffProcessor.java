package com.pug.partner.service;

import com.pug.partner.domain.Staff;
import java.util.UUID;

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

  /**
   * Helper method to process update input. Since Staff is an immutable association, "updating"
   * usually means changing the link (entityId).
   *
   * @param current The current Staff object.
   * @param newEntityId The new entity ID (can be null if no change).
   * @return A new Staff object with updated fields.
   */
  public static Staff processUpdateInput(Staff current, UUID newEntityId) {
    if (newEntityId == null || newEntityId.equals(current.getEntityId())) {
      return current;
    }
    return Staff.factory(current.getAccountId(), newEntityId);
  }
}
