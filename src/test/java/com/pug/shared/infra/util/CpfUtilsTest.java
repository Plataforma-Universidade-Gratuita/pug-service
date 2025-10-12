package com.pug.shared.infra.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class CpfUtilsTest {

  @Test
  void onlyDigitsRemovesNonDigitsAndHandlesNull() {
    assertNull(CpfUtils.onlyDigits(null));
    assertEquals("", CpfUtils.onlyDigits(""));
    assertEquals("93541134780", CpfUtils.onlyDigits("935.411.347-80"));
    assertEquals("123456", CpfUtils.onlyDigits("a1b2c3 4-5.6"));
  }

  @Test
  void sanitizeDelegatesToOnlyDigits() {
    assertEquals("98765432100", CpfUtils.sanitize("987.654.321-00"));
  }
}
