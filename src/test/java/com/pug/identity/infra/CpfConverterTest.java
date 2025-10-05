package com.pug.identity.infra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class CpfConverterTest {
  private final CpfConverter cvt = new CpfConverter();

  @Test
  void stripsMask() {
    assertEquals("93541134780", cvt.convertToDatabaseColumn("935.411.347-80"));
  }

  @Test
  void keepsDigits() {
    assertEquals("12345678901", cvt.convertToDatabaseColumn("12345678901"));
  }

  @Test
  void nullPassesThrough() {
    assertNull(cvt.convertToDatabaseColumn(null));
  }

  @Test
  void removesNonDigits() {
    assertEquals("12345678901", cvt.convertToDatabaseColumn(" 123-456.789-01 "));
  }

  @Test
  void returnsNullWhenDatabaseIsNull() {
    assertNull(cvt.convertToEntityAttribute(null));
  }

  @Test
  void returnsTheSameValueWhenDatabaseIsNotNull() {
    assertEquals("12345678901", cvt.convertToEntityAttribute("12345678901"));
  }
}
