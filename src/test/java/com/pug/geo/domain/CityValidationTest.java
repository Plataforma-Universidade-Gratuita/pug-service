package com.pug.geo.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CityValidationTest {
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
  void validCityPasses() {
    var c = City.builder().name("Florianópolis").ibgeCode("4205407").build();
    var v = validator.validate(c);
    assertTrue(v.isEmpty());
  }

  @Test
  void nameCannotBeBlankOrNull() {
    var blank = City.builder().name("  ").ibgeCode("4205407").build();
    var vBlank = validator.validate(blank);
    var cvBlank = one(vBlank, "name");
    assertEquals("{jakarta.validation.constraints.NotBlank.message}", cvBlank.getMessageTemplate());

    var nul = City.builder().name(null).ibgeCode("4205407").build();
    var vNull = validator.validate(nul);
    var cvNull = one(vNull, "name");
    assertEquals("{jakarta.validation.constraints.NotBlank.message}", cvNull.getMessageTemplate());
  }

  @Test
  void nameMax100Chars() {
    var c = City.builder().name("x".repeat(101)).ibgeCode("4205407").build();
    var v = validator.validate(c);
    var cv = one(v, "name");
    assertEquals("{jakarta.validation.constraints.Size.message}", cv.getMessageTemplate());
  }

  @Test
  void ibgeCodeCannotBeBlankOrNull() {
    var blank = City.builder().name("Test").ibgeCode(" ").build();
    var vBlank = validator.validate(blank);
    var templatesBlank = vBlank.stream().map(ConstraintViolation::getMessageTemplate).toList();
    assertTrue(templatesBlank.contains("{jakarta.validation.constraints.NotBlank.message}"));
    assertTrue(templatesBlank.contains("{jakarta.validation.constraints.Size.message}"));
    assertTrue(templatesBlank.contains("{jakarta.validation.constraints.Pattern.message}"));

    var nul = City.builder().name("Test").ibgeCode(null).build();
    var vNull = validator.validate(nul);
    var cvNull =
        vNull.stream()
            .filter(cv -> cv.getPropertyPath().toString().equals("ibgeCode"))
            .findFirst()
            .orElseThrow();
    assertEquals("{jakarta.validation.constraints.NotBlank.message}", cvNull.getMessageTemplate());
  }

  @Test
  void ibgeCodeMustHave7Digits() {
    var nonDigits = City.builder().name("X").ibgeCode("42A5407").build();
    var v1 = validator.validate(nonDigits);
    var cv1 = one(v1, "ibgeCode");
    assertEquals("{jakarta.validation.constraints.Pattern.message}", cv1.getMessageTemplate());

    var shortLen = City.builder().name("X").ibgeCode("123456").build(); // 6
    var v2 = validator.validate(shortLen);
    var templates2 = v2.stream().map(ConstraintViolation::getMessageTemplate).toList();
    assertTrue(templates2.contains("{jakarta.validation.constraints.Size.message}"));
    assertTrue(templates2.contains("{jakarta.validation.constraints.Pattern.message}"));

    var longLen = City.builder().name("X").ibgeCode("12345678").build(); // 8
    var v3 = validator.validate(longLen);
    var templates3 = v3.stream().map(ConstraintViolation::getMessageTemplate).toList();
    assertTrue(templates3.contains("{jakarta.validation.constraints.Size.message}"));
    assertTrue(templates3.contains("{jakarta.validation.constraints.Pattern.message}"));
  }

  private static ConstraintViolation<City> one(Set<ConstraintViolation<City>> v, String prop) {
    List<ConstraintViolation<City>> list =
        v.stream().filter(cv -> cv.getPropertyPath().toString().equals(prop)).toList();
    assertEquals(1, list.size());
    return list.getFirst();
  }
}
