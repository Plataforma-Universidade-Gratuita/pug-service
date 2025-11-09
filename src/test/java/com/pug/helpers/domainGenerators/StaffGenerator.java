package com.pug.helpers.domainGenerators;

import com.pug.identity.domain.User;
import com.pug.partner.domain.Entity;
import com.pug.partner.domain.Staff;

public final class StaffGenerator {
  private final UserGenerator userGen = new UserGenerator();
  private final EntityGenerator entityGen = new EntityGenerator();

  /** Persisted-like staff: user.id != null, entity.id != null. */
  public Staff createRandomPersistedStaff() {
    User u = userGen.createRandomPersistedUser();
    Entity e = entityGen.createRandomPersistedEntity();
    return Staff.builder().user(u).entity(e).build();
  }
}
