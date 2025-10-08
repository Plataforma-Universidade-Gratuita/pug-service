package com.pug.project.domain.projectLocation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.project.domain.ProjectAllocation;
import com.pug.project.domain.ProjectLocation;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ProjectLocationValidationTest {
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

  private static ProjectAllocation stubAlloc() {
    var a = new ProjectAllocation();
    a.setId(UUID.randomUUID());
    return a;
  }

  @Test
  void validLocationPasses() {
    var loc =
        ProjectLocation.builder()
            .projectAllocation(stubAlloc())
            .address("Av. Mauro Ramos 123")
            .latitude(BigDecimal.valueOf(-27.595377))
            .longitude(BigDecimal.valueOf(-48.548050))
            .build();

    assertTrue(validator.validate(loc).isEmpty());
  }

  @Test
  void projectAllocationNotNull() {
    var loc =
        ProjectLocation.builder()
            .projectAllocation(null)
            .latitude(BigDecimal.valueOf(-27.595377))
            .longitude(BigDecimal.valueOf(0.000001))
            .build();

    var cv = one(validator.validate(loc), "projectAllocation");
    assertEquals("{jakarta.validation.constraints.NotNull.message}", cv.getMessageTemplate());
  }

  @Test
  void latLngPairUsesBundleMessageKey() {
    var loc =
        ProjectLocation.builder()
            .projectAllocation(stubAlloc())
            .latitude(new BigDecimal("10.000000"))
            .longitude(null)
            .build();

    var cv = one(validator.validate(loc), "latLngPairValid");
    assertEquals("{error.project_location.latlng.pair}", cv.getMessageTemplate());
  }

  @Test
  void latitudeRangeValidated() {
    var loc =
        ProjectLocation.builder()
            .projectAllocation(stubAlloc())
            .latitude(new BigDecimal("91"))
            .longitude(new BigDecimal("0"))
            .build();

    var cv = one(validator.validate(loc), "latitude");
    assertEquals("{jakarta.validation.constraints.DecimalMax.message}", cv.getMessageTemplate());
  }

  @Test
  void longitudeRangeValidated() {
    var loc =
        ProjectLocation.builder()
            .projectAllocation(stubAlloc())
            .latitude(new BigDecimal("0"))
            .longitude(new BigDecimal("181"))
            .build();

    var cv = one(validator.validate(loc), "longitude");
    assertEquals("{jakarta.validation.constraints.DecimalMax.message}", cv.getMessageTemplate());
  }

  private static ConstraintViolation<ProjectLocation> one(
      Set<ConstraintViolation<ProjectLocation>> v, String prop) {
    List<ConstraintViolation<ProjectLocation>> list =
        v.stream().filter(cv -> cv.getPropertyPath().toString().equals(prop)).toList();
    assertEquals(1, list.size());
    return list.getFirst();
  }
}
