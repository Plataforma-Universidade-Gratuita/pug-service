package com.pug.helpers.entityGenerators;

import com.pug.identity.infra.persistence.AdminsEntity;
import java.time.OffsetDateTime;
import java.util.UUID;

public class AdminsEntityGenerator {

  /**
   * Helper method to create a random AdminsEntity object.
   *
   * @param userId The UUID of the user to associate with the entity.
   */
  public AdminsEntity createRandomAdminsEntity(UUID userId) {
    return AdminsEntity.builder().userId(userId).grantedAt(OffsetDateTime.now()).build();
  }
}
