package com.pug.geo.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pug.shared.domain.exceptions.AppValidationException;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CityTest {

  @Test
  void builderValidatesNameAndIbge() {
    assertThrows(
        AppValidationException.class,
        () -> City.builder().id(UUID.randomUUID()).name(null).ibgeCode("1234567").build());

    assertThrows(
        AppValidationException.class,
        () ->
            City.builder().id(UUID.randomUUID()).name("A".repeat(101)).ibgeCode("1234567").build());

    assertThrows(
        AppValidationException.class,
        () -> City.builder().id(UUID.randomUUID()).name("Ok").ibgeCode("12A4567").build());

    assertThrows(
        AppValidationException.class,
        () -> City.builder().id(UUID.randomUUID()).name("Ok").ibgeCode("123456").build());
  }
}
