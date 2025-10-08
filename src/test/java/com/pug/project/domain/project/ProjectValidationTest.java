package com.pug.project.domain.project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.academic.domain.FieldOfStudy;
import com.pug.partner.domain.PartnerEntity;
import com.pug.partner.domain.Staff;
import com.pug.project.domain.Project;
import com.pug.project.domain.enums.ProjectStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ProjectValidationTest {
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

  @Test
  void validProjectPassesAndDefaultsStatusPlanned() {
    var p =
        Project.builder()
            .name("Community Garden")
            .description("Init")
            .entity(stubEntity())
            .field(stubField())
            .createdBy(stubStaff())
            .updatedBy(stubStaff())
            .build();

    var v = validator.validate(p);
    assertTrue(v.isEmpty());
    assertEquals(ProjectStatus.PLANNED, p.getStatus());
  }

  @Test
  void nameCannotBeBlankAndMax150() {
    var blank =
        Project.builder()
            .name(" ")
            .entity(stubEntity())
            .field(stubField())
            .createdBy(stubStaff())
            .updatedBy(stubStaff())
            .build();

    var v1 = violationsForProperty(blank, "name");
    assertTrue(v1.contains("{jakarta.validation.constraints.NotBlank.message}"));

    var tooLong =
        Project.builder()
            .name("x".repeat(151))
            .entity(stubEntity())
            .field(stubField())
            .createdBy(stubStaff())
            .updatedBy(stubStaff())
            .build();

    var v2 = violationsForProperty(tooLong, "name");
    assertTrue(v2.contains("{jakarta.validation.constraints.Size.message}"));
  }

  @Test
  void entityFieldCreatedByUpdatedByCannotBeNull() {
    var p =
        Project.builder()
            .name("X")
            .entity(null)
            .field(null)
            .createdBy(null)
            .updatedBy(null)
            .build();

    var templates =
        validator.validate(p).stream()
            .filter(
                cv ->
                    List.of("entity", "field", "createdBy", "updatedBy")
                        .contains(cv.getPropertyPath().toString()))
            .map(ConstraintViolation::getMessageTemplate)
            .toList();

    assertTrue(templates.contains("{jakarta.validation.constraints.NotNull.message}"));
  }

  @Test
  void statusCannotBeNull() {
    var p =
        Project.builder()
            .name("Y")
            .entity(stubEntity())
            .field(stubField())
            .createdBy(stubStaff())
            .updatedBy(stubStaff())
            .status(null)
            .build();

    var v = violationsForProperty(p, "status");
    assertTrue(v.contains("{jakarta.validation.constraints.NotNull.message}"));
  }

  private static List<String> violationsForProperty(Project p, String prop) {
    return validator.validate(p).stream()
        .filter(cv -> cv.getPropertyPath().toString().equals(prop))
        .map(ConstraintViolation::getMessageTemplate)
        .toList();
  }
}
