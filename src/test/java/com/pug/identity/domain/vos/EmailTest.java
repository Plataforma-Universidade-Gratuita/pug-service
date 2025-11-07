package com.pug.identity.domain.vos;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pug.shared.exceptions.AppValidationException;
import org.junit.jupiter.api.Test;

public class EmailTest {

  @Test
  void valid_email_is_normalized_lowercase() {
    Email e = new Email("John.Doe@Example.COM ");
    assertEquals("john.doe@example.com", e.toString());
  }

  @Test
  void equal_after_normalization() {
    Email a = new Email("A@B.com");
    Email b = new Email("a@b.COM");
    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());
  }

  @Test
  void blank_throws() {
    assertThrows(AppValidationException.class, () -> new Email("  "));
  }

  @Test
  void invalid_format_throws() {
    assertThrows(AppValidationException.class, () -> new Email("no-at-symbol.com"));
    assertThrows(AppValidationException.class, () -> new Email("user@nodot"));
  }

  @Test
  void too_long_throws() {
    String local = "a".repeat(245);
    String tooLong = local + "@x.io";
    assertDoesNotThrow(() -> new Email(tooLong));
    String over = "a".repeat(250) + "@x.io";
    assertThrows(AppValidationException.class, () -> new Email(over));
  }
}
