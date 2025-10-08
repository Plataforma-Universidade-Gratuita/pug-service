package com.pug.academic.domain.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.academic.domain.Course;
import com.pug.academic.domain.FieldOfStudy;
import com.pug.academic.domain.Student;
import com.pug.identity.domain.Role;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class StudentValidationTest {

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
    var s =
        Student.builder()
            .userRole(new Role())
            .academicRegistration("2025A123")
            .course(
                Course.builder()
                    .name("databases")
                    .field(FieldOfStudy.builder().name("eng").build())
                    .build())
            .build();
    assertTrue(validator.validate(s).isEmpty());
  }

  @Test
  void academicRegistrationCannotBeBlankOrNull() {
    var blank =
        Student.builder()
            .userRole(new Role())
            .academicRegistration(" ")
            .course(
                Course.builder()
                    .name("db")
                    .field(FieldOfStudy.builder().name("eng").build())
                    .build())
            .build();
    var vBlank = validator.validate(blank);
    assertEquals(
        "{jakarta.validation.constraints.NotBlank.message}",
        one(vBlank, "academicRegistration").getMessageTemplate());

    var nul =
        Student.builder()
            .userRole(new Role())
            .academicRegistration(null)
            .course(
                Course.builder()
                    .name("db")
                    .field(FieldOfStudy.builder().name("eng").build())
                    .build())
            .build();
    var vNull = validator.validate(nul);
    assertEquals(
        "{jakarta.validation.constraints.NotBlank.message}",
        one(vNull, "academicRegistration").getMessageTemplate());
  }

  @Test
  void academicRegistrationMax15() {
    var tooLong =
        Student.builder()
            .userRole(new Role())
            .academicRegistration("x".repeat(16))
            .course(
                Course.builder()
                    .name("db")
                    .field(FieldOfStudy.builder().name("eng").build())
                    .build())
            .build();
    var v = validator.validate(tooLong);
    assertEquals(
        "{jakarta.validation.constraints.Size.message}",
        one(v, "academicRegistration").getMessageTemplate());
  }

  @Test
  void userRoleAndCourseAreRequired() {
    var missingRole =
        Student.builder()
            .userRole(null)
            .academicRegistration("AR123")
            .course(
                Course.builder()
                    .name("db")
                    .field(FieldOfStudy.builder().name("eng").build())
                    .build())
            .build();
    var v1 = validator.validate(missingRole);
    assertEquals(
        "{jakarta.validation.constraints.NotNull.message}",
        one(v1, "userRole").getMessageTemplate());

    var missingCourse =
        Student.builder().userRole(new Role()).academicRegistration("AR123").course(null).build();
    var v2 = validator.validate(missingCourse);
    assertEquals(
        "{jakarta.validation.constraints.NotNull.message}", one(v2, "course").getMessageTemplate());
  }

  private static ConstraintViolation<Student> one(
      Set<ConstraintViolation<Student>> v, String prop) {
    List<ConstraintViolation<Student>> list =
        v.stream().filter(cv -> cv.getPropertyPath().toString().equals(prop)).toList();
    assertEquals(1, list.size());
    return list.getFirst();
  }
}
