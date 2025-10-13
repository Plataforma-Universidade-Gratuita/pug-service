package com.pug.partner.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.shared.domain.exceptions.AppValidationException;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PartnerEntityTest {

  private static final UUID CITY = UUID.randomUUID();

  @Test
  void buildsAndTogglesActive() {
    var e =
        PartnerEntity.newActive()
            .id(UUID.randomUUID())
            .cnpj(Cnpj.of("11222333000181"))
            .name(" ACME ")
            .cityId(CITY)
            .address(Address.of("Rua X, 123"))
            .build();
    assertTrue(e.isActive());
    assertEquals("ACME", e.getName());
    var off = e.deactivate();
    assertFalse(off.isActive());
    var on = off.activate();
    assertTrue(on.isActive());
  }

  @Test
  void validations() {
    assertThrows(
        AppValidationException.class,
        () -> PartnerEntity.builder().id(UUID.randomUUID()).name("X").cityId(CITY).build(),
        "CNPJ required");
    assertThrows(
        AppValidationException.class,
        () ->
            PartnerEntity.builder()
                .id(UUID.randomUUID())
                .cnpj(Cnpj.of("11222333000181"))
                .cityId(CITY)
                .build(),
        "Name required");
    assertThrows(
        AppValidationException.class,
        () ->
            PartnerEntity.builder()
                .id(UUID.randomUUID())
                .cnpj(Cnpj.of("11222333000181"))
                .name("X")
                .build(),
        "City required");
    assertThrows(
        AppValidationException.class,
        () ->
            PartnerEntity.builder()
                .id(UUID.randomUUID())
                .cnpj(Cnpj.of("11222333000181"))
                .name("A".repeat(151))
                .cityId(CITY)
                .build(),
        "Name too long");
  }
}
