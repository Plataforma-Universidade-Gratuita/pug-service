package com.pug.project.domain.projectAllocation;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.academic.domain.FieldOfStudy;
import com.pug.partner.domain.PartnerEntity;
import com.pug.partner.domain.Staff;
import com.pug.project.domain.Project;
import com.pug.project.domain.ProjectAllocation;
import com.pug.project.domain.enums.ProjectStatus;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ProjectAllocationValidationTest {
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

  // minimal stubs
  private static PartnerEntity stubEntity() {
    var e = new PartnerEntity();
    e.setId(UUID.randomUUID());
    return e;
  }

  private static FieldOfStudy stubField() {
    var f = new FieldOfStudy();
    f.setId(UUID.randomUUID());
    return f;
  }

  private static Staff stubStaff() {
    var s = new Staff();
    s.setId(UUID.randomUUID());
    return s;
  }

  private static Project stubProject() {
    return Project.builder()
        .id(UUID.randomUUID())
        .name("P")
        .entity(stubEntity())
        .field(stubField())
        .status(ProjectStatus.PLANNED)
        .createdBy(stubStaff())
        .updatedBy(stubStaff())
        .build();
  }

  @Test
  void validAllocationPasses() {
    var a =
        ProjectAllocation.builder()
            .project(stubProject())
            .offeredHours(new BigDecimal("1234.50"))
            .startDate(LocalDate.of(2025, 1, 10))
            .endDate(LocalDate.of(2025, 2, 10))
            .build();

    assertTrue(validator.validate(a).isEmpty());
  }

  @Test
  void requiredFieldsNotNull() {
    var a =
        ProjectAllocation.builder()
            .project(null)
            .offeredHours(null)
            .startDate(null)
            .endDate(null)
            .build();

    var templates =
        validator.validate(a).stream()
            .filter(
                cv ->
                    List.of("project", "offeredHours", "startDate", "endDate")
                        .contains(cv.getPropertyPath().toString()))
            .map(cv -> cv.getMessageTemplate())
            .toList();

    assertTrue(templates.contains("{jakarta.validation.constraints.NotNull.message}"));
  }

  @Test
  void offeredHoursDigitsConstraint() {
    var f =
        ProjectAllocation.builder()
            .project(stubProject())
            .offeredHours(new BigDecimal("10.123"))
            .startDate(LocalDate.now())
            .endDate(LocalDate.now())
            .build();
    var v1 =
        validator.validate(f).stream()
            .filter(cv -> cv.getPropertyPath().toString().equals("offeredHours"))
            .map(cv -> cv.getMessageTemplate())
            .toList();
    assertTrue(v1.contains("{jakarta.validation.constraints.Digits.message}"));

    var i =
        ProjectAllocation.builder()
            .project(stubProject())
            .offeredHours(new BigDecimal("12345.00"))
            .startDate(LocalDate.now())
            .endDate(LocalDate.now())
            .build();
    var v2 =
        validator.validate(i).stream()
            .filter(cv -> cv.getPropertyPath().toString().equals("offeredHours"))
            .map(cv -> cv.getMessageTemplate())
            .toList();
    assertTrue(v2.contains("{jakarta.validation.constraints.Digits.message}"));
  }
}
