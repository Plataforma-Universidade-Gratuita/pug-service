package com.pug.identity.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pug.helpers.domainGenerators.UserGenerator;
import com.pug.shared.exceptions.AppValidationException;
import org.junit.jupiter.api.Test;

class AdminTest {

  private final UserGenerator userGen = new UserGenerator();

  @Test
  void build_null_user_throws() {
    assertThrows(
        AppValidationException.class,
        () -> Admin.builder().user(null).grantedAt(java.time.OffsetDateTime.now()).build());
  }

  @Test
  void build_user_without_id_throws() {
    var userNoId = userGen.createRandomUser();
    assertThrows(
        AppValidationException.class,
        () -> Admin.builder().user(userNoId).grantedAt(java.time.OffsetDateTime.now()).build());
  }

  @Test
  void build_ok_with_persisted_user() {
    var user = userGen.createRandomPersistedUser();
    var granted = java.time.OffsetDateTime.now().minusMinutes(5);
    var admin = Admin.builder().user(user).grantedAt(granted).build();

    assertNotNull(admin);
    assertEquals(user.getId(), admin.getUser().getId());
    assertEquals(granted, admin.getGrantedAt());
  }
}
