package com.pug.enrollment.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.academic.domain.Student;
import com.pug.enrollment.domain.enums.ProjectEnrollmentStatus;
import com.pug.project.domain.Project;
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

class ProjectEnrollmentValidationTest {
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

  private static Project stubProject() {
    var p = new Project();
    p.setId(UUID.randomUUID());
    p.setName("P");
    return p;
  }

  private static Student stubStudent() {
    var s = new Student();
    s.setId(UUID.randomUUID());
    return s;
  }

  @Test
  void validPassesAndDefaultsPending() {
    var e = ProjectEnrollment.builder().project(stubProject()).student(stubStudent()).build();

    var v = validator.validate(e);
    assertTrue(v.isEmpty());
    assertEquals(ProjectEnrollmentStatus.PENDING, e.getStatus());
  }

  @Test
  void projectNotNull() {
    var e = ProjectEnrollment.builder().project(null).student(stubStudent()).build();
    var cv = one(validator.validate(e), "project");
    assertEquals("{jakarta.validation.constraints.NotNull.message}", cv.getMessageTemplate());
  }

  @Test
  void studentNotNull() {
    var e = ProjectEnrollment.builder().project(stubProject()).student(null).build();
    var cv = one(validator.validate(e), "student");
    assertEquals("{jakarta.validation.constraints.NotNull.message}", cv.getMessageTemplate());
  }

  @Test
  void statusNotNull() {
    var e =
        ProjectEnrollment.builder()
            .project(stubProject())
            .student(stubStudent())
            .status(null)
            .build();
    var cv = one(validator.validate(e), "status");
    assertEquals("{jakarta.validation.constraints.NotNull.message}", cv.getMessageTemplate());
  }

  private static ConstraintViolation<ProjectEnrollment> one(
      Set<ConstraintViolation<ProjectEnrollment>> v, String prop) {
    List<ConstraintViolation<ProjectEnrollment>> list =
        v.stream().filter(cv -> cv.getPropertyPath().toString().equals(prop)).toList();
    assertEquals(1, list.size());
    return list.getFirst();
  }
}
