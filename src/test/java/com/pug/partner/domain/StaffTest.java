package com.pug.partner.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.shared.domain.exceptions.AppValidationException;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class StaffTest {

  @Test
  void buildsTrimsAndCanonicalizesEmail() {
    var s =
        Staff.newActive()
            .id(UUID.randomUUID())
            .userId(UUID.randomUUID())
            .email("  USER@Example.COM ")
            .entityId(UUID.randomUUID())
            .build();
    assertEquals("USER@Example.COM", s.getEmail());
    assertEquals("user@example.com", s.canonicalEmail());
    assertTrue(s.isActive());
    assertFalse(s.deactivate().isActive());
  }

  @Test
  void validations() {
    assertThrows(
        AppValidationException.class,
        () ->
            Staff.builder()
                .id(UUID.randomUUID())
                .email("a@b.com")
                .entityId(UUID.randomUUID())
                .build(),
        "User required");
    assertThrows(
        AppValidationException.class,
        () ->
            Staff.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .entityId(UUID.randomUUID())
                .build(),
        "Email required");
    assertThrows(
        AppValidationException.class,
        () ->
            Staff.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .email("x@x")
                .entityId(UUID.randomUUID())
                .build(),
        "Email basic validator will fail at runtime if used");
    assertThrows(
        AppValidationException.class,
        () ->
            Staff.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .email("a".repeat(255) + "@x.com")
                .entityId(UUID.randomUUID())
                .build(),
        "Email too long");
  }
}
