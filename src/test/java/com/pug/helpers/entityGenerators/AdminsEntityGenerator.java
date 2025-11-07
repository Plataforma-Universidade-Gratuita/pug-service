// src/test/java/com/pug/helpers/entityGenerators/AdminsEntityGenerator.java
package com.pug.helpers.entityGenerators;

import com.pug.identity.infra.persistence.AdminsEntity;
import com.pug.identity.infra.persistence.UsersEntity;
import java.time.OffsetDateTime;
import java.util.UUID;

/** Helpers to create AdminsEntity rows for tests. */
public class AdminsEntityGenerator {

  /** Minimal row: set FK by id only. */
  public AdminsEntity createRandomAdminsEntity(UUID userId) {
    var ref = UsersEntity.builder().build();
    ref.setId(userId);
    return AdminsEntity.builder().user(ref).grantedAt(OffsetDateTime.now()).build();
  }

  /** Row with a UsersEntity reference (no cascade expected). */
  public AdminsEntity createRandomAdminsEntity(UsersEntity userRef) {
    UUID id = userRef.getId();
    return AdminsEntity.builder().userId(id).user(userRef).grantedAt(OffsetDateTime.now()).build();
  }
}
