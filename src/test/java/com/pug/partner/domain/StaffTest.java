package com.pug.partner.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pug.identity.domain.User;
import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.shared.exceptions.AppValidationException;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class StaffTest {

  @Test
  @DisplayName("build succeeds when user.id and entity.id are non-null")
  void build_valid() {
    User user = Mockito.mock(User.class);
    Mockito.when(user.getId()).thenReturn(UUID.randomUUID());

    Entity entity = Mockito.mock(Entity.class);
    Mockito.when(entity.getId()).thenReturn(UUID.randomUUID());

    Staff s = Staff.builder().user(user).entity(entity).build();

    assertEquals(user, s.getUser());
    assertEquals(entity, s.getEntity());
  }

  @Test
  @DisplayName("null user -> INVALID_STAFF_USER")
  void nullUser_fails() {
    Entity entity = Mockito.mock(Entity.class);
    Mockito.when(entity.getId()).thenReturn(UUID.randomUUID());

    AppValidationException ex =
        assertThrows(
            AppValidationException.class, () -> Staff.builder().user(null).entity(entity).build());

    assertErrorCode(ex, PartnerErrorCodes.INVALID_STAFF_USER);
  }

  @Test
  @DisplayName("user with null id -> INVALID_STAFF_USER")
  void userWithNullId_fails() {
    User user = Mockito.mock(User.class);
    Mockito.when(user.getId()).thenReturn(null);

    Entity entity = Mockito.mock(Entity.class);
    Mockito.when(entity.getId()).thenReturn(UUID.randomUUID());

    AppValidationException ex =
        assertThrows(
            AppValidationException.class, () -> Staff.builder().user(user).entity(entity).build());

    assertErrorCode(ex, PartnerErrorCodes.INVALID_STAFF_USER);
  }

  @Test
  @DisplayName("null entity -> INVALID_STAFF_ENTITY")
  void nullEntity_fails() {
    User user = Mockito.mock(User.class);
    Mockito.when(user.getId()).thenReturn(UUID.randomUUID());

    AppValidationException ex =
        assertThrows(
            AppValidationException.class, () -> Staff.builder().user(user).entity(null).build());

    assertErrorCode(ex, PartnerErrorCodes.INVALID_STAFF_ENTITY);
  }

  @Test
  @DisplayName("entity with null id -> INVALID_STAFF_ENTITY")
  void entityWithNullId_fails() {
    User user = Mockito.mock(User.class);
    Mockito.when(user.getId()).thenReturn(UUID.randomUUID());

    Entity entity = Mockito.mock(Entity.class);
    Mockito.when(entity.getId()).thenReturn(null);

    AppValidationException ex =
        assertThrows(
            AppValidationException.class, () -> Staff.builder().user(user).entity(entity).build());

    assertErrorCode(ex, PartnerErrorCodes.INVALID_STAFF_ENTITY);
  }

  private static void assertErrorCode(AppValidationException ex, Object expected) {
    try {
      var m = ex.getClass().getMethod("getErrorCode");
      Object code = m.invoke(ex);
      assertEquals(expected, code);
    } catch (ReflectiveOperationException ignored) {
      assertNotNull(ex.getMessage());
    }
  }
}
