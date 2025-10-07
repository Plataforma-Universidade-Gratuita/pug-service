package com.pug.academic.domain.fieldOfStudy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.academic.domain.FieldOfStudy;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class FieldOfStudyValidationTest {
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
  void validEntityPasses() {
    var fos = FieldOfStudy.builder().name("Engineering").build();
    assertTrue(validator.validate(fos).isEmpty());
  }

  @Test
  void nameCannotBeBlankOrNull() {
    var blank = FieldOfStudy.builder().name("  ").build();
    var vBlank = validator.validate(blank);
    var cvBlank = one(vBlank, "name");
    assertEquals("{jakarta.validation.constraints.NotBlank.message}", cvBlank.getMessageTemplate());

    var nul = FieldOfStudy.builder().name(null).build();
    var vNull = validator.validate(nul);
    var cvNull = one(vNull, "name");
    assertEquals("{jakarta.validation.constraints.NotBlank.message}", cvNull.getMessageTemplate());
  }

  @Test
  void nameMax100Chars() {
    var tooLong = FieldOfStudy.builder().name("x".repeat(101)).build();
    var vLong = validator.validate(tooLong);
    var cv = one(vLong, "name");
    assertEquals("{jakarta.validation.constraints.Size.message}", cv.getMessageTemplate());

    var atLimit = FieldOfStudy.builder().name("x".repeat(100)).build();
    assertTrue(validator.validate(atLimit).isEmpty());
  }

  private static ConstraintViolation<FieldOfStudy> one(
      Set<ConstraintViolation<FieldOfStudy>> v, String prop) {
    List<ConstraintViolation<FieldOfStudy>> list =
        v.stream().filter(cv -> cv.getPropertyPath().toString().equals(prop)).toList();
    assertEquals(1, list.size());
    return list.getFirst();
  }
}
