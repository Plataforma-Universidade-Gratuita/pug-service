package com.pug.partner.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pug.shared.domain.exceptions.AppValidationException;
import org.junit.jupiter.api.Test;

class CnpjTest {

  @Test
  void parsesAndMasksValid() {
    var c = Cnpj.of("11.222.333/0001-81");
    assertEquals("11222333000181", c.getValue());
    assertEquals("11.222.333/0001-81", c.masked());
  }

  @Test
  void rejectsInvalid() {
    assertThrows(AppValidationException.class, () -> Cnpj.of(null));
    assertThrows(AppValidationException.class, () -> Cnpj.of("123"));
    assertThrows(AppValidationException.class, () -> Cnpj.of("00000000000000"));
    assertThrows(AppValidationException.class, () -> Cnpj.of("11222333000182"));
  }
}
