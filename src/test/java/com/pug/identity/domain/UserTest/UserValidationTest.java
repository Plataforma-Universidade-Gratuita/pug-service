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

  private static final String VALID_CPF = "93541134780";

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
    var u = User.builder().cpf(VALID_CPF).name("Ada Lovelace").build();
    var v = validator.validate(u);
    assertTrue(v.isEmpty());
  }

  @Test
  void cpf_acceptsMaskedAndIsSanitizedByConverter_whenPersisting() {
    var u = User.builder().cpf("935.411.347-80").name("Ada Lovelace").build();
    var v = validator.validate(u);
    assertTrue(v.isEmpty());
  }

  @Test
  void cpfCannotBeBlank() {
    var u = User.builder().cpf("   ").name("Ada Lovelace").build();
    var v = validator.validate(u);
    var templates =
        v.stream()
            .filter(cv -> cv.getPropertyPath().toString().equals("cpf"))
            .map(ConstraintViolation::getMessageTemplate)
            .toList();
    assertTrue(templates.contains("{jakarta.validation.constraints.NotBlank.message}"));
    assertTrue(templates.contains("{org.hibernate.validator.constraints.br.CPF.message}"));
  }

  @Test
  void cpfCannotContainLetters() {
    var u = User.builder().cpf("1234567890a").name("Ada Lovelace").build();
    var v = validator.validate(u);
    var cv = one(v, "cpf");
    assertEquals("{org.hibernate.validator.constraints.br.CPF.message}", cv.getMessageTemplate());
  }

  @Test
  void cpfCannotBeNull() {
    var u = User.builder().cpf(null).name("Ada Lovelace").build();
    var v = validator.validate(u);
    var cv = one(v, "cpf");
    // @CPF allows null; @NotBlank catches it
    assertEquals("{jakarta.validation.constraints.NotBlank.message}", cv.getMessageTemplate());
  }

  @Test
  void cpfInvalidLengthFailsCpfConstraint() {
    var over = User.builder().cpf("123456789012").name("Ada").build(); // 12
    var vOver = validator.validate(over);
    var cvOver = one(vOver, "cpf");
    assertEquals(
        "{org.hibernate.validator.constraints.br.CPF.message}", cvOver.getMessageTemplate());

    var under = User.builder().cpf("1234567890").name("Ada").build(); // 10
    var vUnder = validator.validate(under);
    var cvUnder = one(vUnder, "cpf");
    assertEquals(
        "{org.hibernate.validator.constraints.br.CPF.message}", cvUnder.getMessageTemplate());
  }

  @Test
  void nameCannotBeBlankOrNull() {
    var blank = User.builder().cpf(VALID_CPF).name(" ").build();
    var vBlank = validator.validate(blank);
    var cvBlank = one(vBlank, "name");
    assertEquals("{jakarta.validation.constraints.NotBlank.message}", cvBlank.getMessageTemplate());

    var nul = User.builder().cpf(VALID_CPF).name(null).build();
    var vNull = validator.validate(nul);
    var cvNull = one(vNull, "name");
    assertEquals("{jakarta.validation.constraints.NotBlank.message}", cvNull.getMessageTemplate());
  }

  @Test
  void nameMax150Chars() {
    var u = User.builder().cpf(VALID_CPF).name("x".repeat(151)).build();
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
