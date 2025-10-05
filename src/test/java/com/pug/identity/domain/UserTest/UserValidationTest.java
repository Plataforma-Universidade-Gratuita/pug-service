package com.pug.identity.domain.UserTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.identity.domain.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class UserValidationTest {
  static Validator validator;
  static Locale original;

  @BeforeAll
  static void boot() {
    original = Locale.getDefault();
    Locale.setDefault(Locale.ENGLISH);
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @AfterAll
  static void tearDown() {
    Locale.setDefault(original);
  }

  @Test
  void validUserPasses() {
    var u = User.builder().cpf("12345678901234").name("Ada Lovelace").build();
    var v = validator.validate(u);
    assertTrue(v.isEmpty());
  }

  @Test
  void cpfCannotBeBlank() {
    var u = User.builder().cpf("   ").name("Ada Lovelace").build();
    var v = validator.validate(u);
    var cpfViolations =
        v.stream().filter(cv -> cv.getPropertyPath().toString().equals("cpf")).toList();
    assertEquals(2, cpfViolations.size());
    var templates = cpfViolations.stream().map(ConstraintViolation::getMessageTemplate).toList();
    assertTrue(templates.contains("{jakarta.validation.constraints.NotBlank.message}"));
    assertTrue(templates.contains("{jakarta.validation.constraints.Size.message}"));
  }

  @Test
  void cpfCannotBeNull() {
    var u = User.builder().cpf(null).name("Ada Lovelace").build();
    var v = validator.validate(u);
    var cv = one(v, "cpf");
    assertEquals("{jakarta.validation.constraints.NotBlank.message}", cv.getMessageTemplate());
  }

  @Test
  void cpfMustBeExactly14Characters() {
    var over = User.builder().cpf("123456789012345").name("Ada").build();
    var vOver = validator.validate(over);
    var cvOver = one(vOver, "cpf");
    assertEquals("{jakarta.validation.constraints.Size.message}", cvOver.getMessageTemplate());

    var under = User.builder().cpf("1234567890123").name("Ada").build();
    var vUnder = validator.validate(under);
    var cvUnder = one(vUnder, "cpf");
    assertEquals("{jakarta.validation.constraints.Size.message}", cvUnder.getMessageTemplate());
  }

  @Test
  void nameCannotBeBlankOrNull() {
    var blank = User.builder().cpf("12345678901234").name(" ").build();
    var vBlank = validator.validate(blank);
    var cvBlank = one(vBlank, "name");
    assertEquals("{jakarta.validation.constraints.NotBlank.message}", cvBlank.getMessageTemplate());

    var nul = User.builder().cpf("12345678901234").name(null).build();
    var vNull = validator.validate(nul);
    var cvNull = one(vNull, "name");
    assertEquals("{jakarta.validation.constraints.NotBlank.message}", cvNull.getMessageTemplate());
  }

  @Test
  void nameMax150Chars() {
    var u = User.builder().cpf("12345678901234").name("x".repeat(151)).build();
    var v = validator.validate(u);
    var cv = one(v, "name");
    assertEquals("{jakarta.validation.constraints.Size.message}", cv.getMessageTemplate());
  }

  private static ConstraintViolation<User> one(Set<ConstraintViolation<User>> v, String prop) {
    List<ConstraintViolation<User>> list =
        v.stream().filter(cv -> cv.getPropertyPath().toString().equals(prop)).toList();
    assertEquals(1, list.size());
    return list.getFirst();
  }
}
