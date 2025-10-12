// src/test/java/com/pug/identity/infra/CpfConverterTest.java
package com.pug.identity.infra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class CpfConverterTest {

  private final CpfConverter conv = new CpfConverter();

  @Test
  void convertToDatabaseColumnStripsNonDigits() {
    assertEquals("93541134780", conv.convertToDatabaseColumn("935.411.347-80"));
  }

  @Test
  void convertNullSafely() {
    assertNull(conv.convertToDatabaseColumn(null));
  }

  @Test
  void convertToEntityAttributeReturnsSame() {
    assertEquals("93541134780", conv.convertToEntityAttribute("93541134780"));
  }
}
