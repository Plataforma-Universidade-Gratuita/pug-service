package com.pug.partner.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pug.shared.domain.exceptions.AppValidationException;
import org.junit.jupiter.api.Test;

class AddressTest {

  @Test
  void ofTrimsAndNulls() {
    assertNull(Address.of(null));
    assertNull(Address.of("   "));
    var a = Address.of("  Rua X  ");
    assertEquals("Rua X", a.toString());
  }

  @Test
  void tooLongThrows() {
    String longStr = "A".repeat(255);
    assertThrows(AppValidationException.class, () -> Address.of(longStr));
  }
}
