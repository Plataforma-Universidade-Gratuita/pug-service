package com.pug.attendance.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.attendance.domain.enums.AttendanceStatus;
import com.pug.enrollment.domain.ProjectEnrollment;
import com.pug.project.domain.ProjectLocation;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ProjectAttendanceValidationTest {
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

  private static ProjectEnrollment stubEnrollment() {
    var e = new ProjectEnrollment();
    e.setId(UUID.randomUUID());
    return e;
  }

  private static ProjectLocation stubLocation() {
    var l = new ProjectLocation();
    l.setId(UUID.randomUUID());
    return l;
  }

  @Test
  void validPassesAndDefaultsPending() {
    var a =
        ProjectAttendance.builder()
            .enrollment(stubEnrollment())
            .projectLocation(stubLocation())
            .duration(new BigDecimal("1.50"))
            .build();

    var v = validator.validate(a);
    assertTrue(v.isEmpty());
    assertEquals(AttendanceStatus.PENDING, a.getStatus());
  }

  @Test
  void requiredFieldsNotNull() {
    var a = ProjectAttendance.builder().build();
    var props = validator.validate(a).stream().map(v -> v.getPropertyPath().toString()).toList();

    assertTrue(props.contains("duration"));
    assertTrue(
        validator.validate(a).stream()
            .noneMatch(v -> v.getPropertyPath().toString().equals("status")));
  }

  @Test
  void durationDigitsConstraint() {
    var fracTooLong =
        ProjectAttendance.builder()
            .enrollment(stubEnrollment())
            .projectLocation(stubLocation())
            .duration(new BigDecimal("1.234"))
            .build();
    var v1 = one(validator.validate(fracTooLong), "duration");
    assertEquals("{jakarta.validation.constraints.Digits.message}", v1.getMessageTemplate());

    var intTooLong =
        ProjectAttendance.builder()
            .enrollment(stubEnrollment())
            .projectLocation(stubLocation())
            .duration(new BigDecimal("123.00"))
            .build(); // integer=3 > 2
    var v2 = one(validator.validate(intTooLong), "duration");
    assertEquals("{jakarta.validation.constraints.Digits.message}", v2.getMessageTemplate());
  }

  @Test
  void qrHashMax512() {
    var a =
        ProjectAttendance.builder()
            .enrollment(stubEnrollment())
            .projectLocation(stubLocation())
            .duration(new BigDecimal("1.00"))
            .qrValidationHash("x".repeat(513))
            .build();
    var v = one(validator.validate(a), "qrValidationHash");
    assertEquals("{jakarta.validation.constraints.Size.message}", v.getMessageTemplate());
  }

  private static ConstraintViolation<ProjectAttendance> one(
      Set<ConstraintViolation<ProjectAttendance>> v, String prop) {
    var list = v.stream().filter(cv -> cv.getPropertyPath().toString().equals(prop)).toList();
    assertEquals(1, list.size());
    return list.getFirst();
  }
}
