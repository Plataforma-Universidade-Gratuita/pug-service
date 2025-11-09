package com.pug.partner.domain.vos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.shared.exceptions.AppValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CnpjTest {

  private static final String[][] VALID = {
    {"04.252.011/0001-10", "04252011000110"},
    {"40.688.134/0001-61", "40688134000161"},
    {"33.000.167/0001-01", "33000167000101"},
    {"11.222.333/0001-81", "11222333000181"}
  };

  private static final String[] INVALID = {
    null, "", "   ", "abc", "123", "123456789012345", "00.000.000/0000-00", "12.345.678/9012-34"
  };

  @Test
  @DisplayName("sanitize removes non-digits and returns null for null")
  void sanitize_basic() {
    assertNull(Cnpj.sanitize(null));
    assertEquals("04252011000110", Cnpj.sanitize("04.252.011/0001-10"));
    assertEquals("40688134000161", Cnpj.sanitize(" 40 688 134 0001 61 "));
    assertEquals("", Cnpj.sanitize(""));
    assertEquals("123456", Cnpj.sanitize("a1b2c3.4-5/6"));
  }

  @Test
  @DisplayName("valid inputs construct and normalize to digits")
  void construct_valid() {
    for (String[] pair : VALID) {
      String formatted = pair[0];
      String digits = pair[1];

      Cnpj a = new Cnpj(formatted);
      assertEquals(digits, a.value());
      assertEquals(digits, a.toString());
      assertEquals(formatted, a.formatted());

      Cnpj b = new Cnpj(digits);
      assertEquals(digits, b.value());
      assertEquals(formatted, b.formatted());

      assertEquals(a, b);
      assertEquals(a.hashCode(), b.hashCode());
    }
  }

  @Test
  @DisplayName("invalid inputs throw AppValidationException")
  void construct_invalid() {
    for (String bad : INVALID) {
      AppValidationException ex =
          assertThrows(AppValidationException.class, () -> new Cnpj(bad), "case: " + bad);
      try {
        var method = ex.getClass().getMethod("getErrorCode");
        Object code = method.invoke(ex);
        assertEquals(PartnerErrorCodes.INVALID_CNPJ, code);
      } catch (ReflectiveOperationException ignored) {
        assertNotNull(ex.getMessage());
      }
    }
  }

  @Test
  @DisplayName("formatted returns XX.XXX.XXX/XXXX-XX")
  void formatted_pattern() {
    Cnpj c = new Cnpj("04252011000110");
    assertEquals("04.252.011/0001-10", c.formatted());
  }
}
