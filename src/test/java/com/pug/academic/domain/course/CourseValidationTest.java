package com.pug.academic.domain.course;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.academic.domain.Course;
import com.pug.academic.domain.FieldOfStudy;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CourseValidationTest {

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
    var course =
        Course.builder()
            .id(UUID.randomUUID())
            .name("Software Engineering")
            .field(FieldOfStudy.builder().id(UUID.randomUUID()).name("engineering").build())
            .build();
    assertTrue(validator.validate(course).isEmpty());
  }

  @Test
  void nameCannotBeBlankOrNull() {
    var blank =
        Course.builder()
            .name(" ")
            .field(FieldOfStudy.builder().id(UUID.randomUUID()).name("engineering").build())
            .build();
    var vBlank = validator.validate(blank);
    assertEquals(
        "{jakarta.validation.constraints.NotBlank.message}",
        one(vBlank, "name").getMessageTemplate());

    var nul =
        Course.builder()
            .name(null)
            .field(FieldOfStudy.builder().id(UUID.randomUUID()).name("engineering").build())
            .build();
    var vNull = validator.validate(nul);
    assertEquals(
        "{jakarta.validation.constraints.NotBlank.message}",
        one(vNull, "name").getMessageTemplate());
  }

  @Test
  void nameMax120Chars() {
    var tooLong =
        Course.builder()
            .name("x".repeat(121))
            .field(FieldOfStudy.builder().id(UUID.randomUUID()).name("engineering").build())
            .build();
    var v = validator.validate(tooLong);
    assertEquals(
        "{jakarta.validation.constraints.Size.message}", one(v, "name").getMessageTemplate());
  }

  @Test
  void fieldIsRequired() {
    var course = Course.builder().name("Databases").field(null).build();
    var v = validator.validate(course);
    assertEquals(
        "{jakarta.validation.constraints.NotNull.message}", one(v, "field").getMessageTemplate());
  }

  private static ConstraintViolation<Course> one(Set<ConstraintViolation<Course>> v, String prop) {
    List<ConstraintViolation<Course>> list =
        v.stream().filter(cv -> cv.getPropertyPath().toString().equals(prop)).toList();
    assertEquals(1, list.size());
    return list.getFirst();
  }
}
