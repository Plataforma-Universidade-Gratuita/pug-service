package com.pug.identity.domain.vos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pug.shared.exceptions.AppValidationException;
import org.junit.jupiter.api.Test;

public class CpfTest {

  @Test
  void formatted_inputs_are_sanitized() {
    Cpf c1 = new Cpf("529.982.247-25");
    Cpf c2 = new Cpf("529-982-247.25");
    Cpf c3 = new Cpf("52998224725");
    assertEquals("52998224725", c1.toString());
    assertEquals("52998224725", c2.toString());
    assertEquals("52998224725", c3.toString());
  }

  @Test
  void formatted_method_returns_expected_mask() {
    Cpf c = new Cpf("52998224725");
    assertEquals("529.982.247-25", c.formatted());
  }

  @Test
  void invalid_length_throws() {
    assertThrows(AppValidationException.class, () -> new Cpf("123.456.789-0"));
    assertThrows(AppValidationException.class, () -> new Cpf("1234567890"));
    assertThrows(AppValidationException.class, () -> new Cpf("123456789012"));
  }

  @Test
  void repeated_digits_throws() {
    assertThrows(AppValidationException.class, () -> new Cpf("00000000000"));
    assertThrows(AppValidationException.class, () -> new Cpf("11111111111"));
  }

  @Test
  void bad_check_digits_throws() {
    assertThrows(AppValidationException.class, () -> new Cpf("52998224724"));
  }
}
