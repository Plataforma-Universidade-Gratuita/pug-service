package com.pug.helpers.entityGenerators;

import com.pug.partner.infra.persistence.StaffEntity;
import java.util.UUID;

public class StaffEntityGenerator {

  /**
   * Helper method to create a random StaffEntity object.
   *
   * @param userId The UUID of the user to associate with the staff entity.
   * @param entityId The UUID of the entity to associate with the staff entity.
   */
  public StaffEntity createRandomStaffEntity(UUID userId, UUID entityId) {
    return StaffEntity.builder().userId(userId).entityId(entityId).build();
  }
}
