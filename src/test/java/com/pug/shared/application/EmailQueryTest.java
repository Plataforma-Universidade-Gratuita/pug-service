package com.pug.shared.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class EmailQueryTest {
  private static ValidatorFactory vf;
  private static Validator validator;

  @BeforeAll
  static void setup() {
    vf = Validation.buildDefaultValidatorFactory();
    validator = vf.getValidator();
  }

  @AfterAll
  static void tearDown() {
    vf.close();
  }

  @Test
  void validEmailPasses() {
    var q = new EmailQuery("user@example.com");
    assertTrue(validator.validate(q).isEmpty());
  }

  @Test
  void maxLength254Accepted() {
    var suffix = "@e.co"; // 5 chars
    int localLen = 254 - suffix.length();
    var email = "a".repeat(localLen) + suffix;
    assertEquals(254, email.length());
    assertTrue(validator.validate(new EmailQuery(email)).isEmpty());
  }

  @Test
  void blankOrNullRejected() {
    assertFalse(validator.validate(new EmailQuery("")).isEmpty());
    assertFalse(validator.validate(new EmailQuery("   ")).isEmpty());
    assertFalse(validator.validate(new EmailQuery(null)).isEmpty());
  }

  @Test
  void invalidFormatRejected() {
    assertFalse(validator.validate(new EmailQuery("not-an-email")).isEmpty());
    assertFalse(validator.validate(new EmailQuery("@example.com")).isEmpty());
    assertFalse(validator.validate(new EmailQuery("local@")).isEmpty());
    assertFalse(validator.validate(new EmailQuery("bad@@example.com")).isEmpty());
  }

  @Test
  void length255Rejected() {
    var suffix = "@e.co";
    int localLen = 255 - suffix.length();
    var email = "a".repeat(localLen) + suffix;
    assertEquals(255, email.length());
    Set violations = validator.validate(new EmailQuery(email));
    assertFalse(violations.isEmpty());
  }
}
