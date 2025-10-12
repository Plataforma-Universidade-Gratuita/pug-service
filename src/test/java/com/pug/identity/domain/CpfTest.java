package com.pug.identity.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pug.shared.domain.exceptions.AppValidationException;
import com.pug.shared.errors.ErrorCodes;
import org.junit.jupiter.api.Test;

class CpfTest {

  private static final String VALID_DIGITS = "93541134780";
  private static final String VALID_MASKED = "935.411.347-80";

  @Test
  void ofAcceptsValidCpfDigitsOrMasked() {
    var a = Cpf.of(VALID_DIGITS);
    var b = Cpf.of(VALID_MASKED);
    assertEquals(VALID_DIGITS, a.getValue());
    assertEquals(VALID_DIGITS, b.getValue());
    assertEquals(a, b);
  }

  @Test
  void ofRejectsInvalidCpf() {
    var ex = assertThrows(AppValidationException.class, () -> Cpf.of("11111111111"));
    assertEquals(ErrorCodes.USER_CPF_INVALID, ex.code());
  }

  @Test
  void maskedFormatsCorrectly() {
    var c = Cpf.of(VALID_DIGITS);
    assertEquals("935.411.347-80", c.masked());
    assertEquals("935.411.347-80", c.toString());
  }

  @Test
  void digitsStripsNonDigitsAndHandlesNull() {
    assertNull(Cpf.digits(null));
    assertEquals(VALID_DIGITS, Cpf.digits(VALID_MASKED));
  }
}
