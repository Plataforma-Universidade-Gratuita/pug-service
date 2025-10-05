package com.pug.partner.infra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class CnpjConverterTest {
  private final CnpjConverter cvt = new CnpjConverter();

  @Test
  void stripsMask() {
    assertEquals("11222333000181", cvt.convertToDatabaseColumn("11.222.333/0001-81"));
  }

  @Test
  void keepsDigits() {
    assertEquals("11222333000181", cvt.convertToDatabaseColumn("11222333000181"));
  }

  @Test
  void nullPassesThrough() {
    assertNull(cvt.convertToDatabaseColumn(null));
  }

  @Test
  void removesNonDigits() {
    assertEquals("11222333000181", cvt.convertToDatabaseColumn(" 11.222/333-0001 81 "));
  }

  @Test
  void returnsNullWhenDatabaseIsNull() {
    assertNull(cvt.convertToEntityAttribute(null));
  }

  @Test
  void returnsTheSameValueWhenDatabaseIsNotNull() {
    assertEquals("11222333000181", cvt.convertToEntityAttribute("11222333000181"));
  }
}
