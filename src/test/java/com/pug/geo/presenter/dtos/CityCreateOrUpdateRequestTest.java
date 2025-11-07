package com.pug.geo.presenter.dtos;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CityCreateOrUpdateRequestTest {

  private static Validator validator;

  @BeforeAll
  static void init() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @AfterAll
  static void destroy() {
    validator = null;
  }

  @Test
  void valid_payload_passes_validation() {
    CityCreateOrUpdateRequest dto = new CityCreateOrUpdateRequest("Joinville", "4209102");
    Set<ConstraintViolation<CityCreateOrUpdateRequest>> v = validator.validate(dto);
    assertTrue(v.isEmpty());
  }

  @Test
  void blank_name_fails_notblank() {
    CityCreateOrUpdateRequest dto = new CityCreateOrUpdateRequest("   ", "4209102");
    Set<ConstraintViolation<CityCreateOrUpdateRequest>> v = validator.validate(dto);

    assertFalse(v.isEmpty());
    assertTrue(
        v.stream()
            .anyMatch(
                cv ->
                    cv.getPropertyPath().toString().equals("name")
                        && cv.getConstraintDescriptor()
                            .getAnnotation()
                            .annotationType()
                            .equals(NotBlank.class)));
  }

  @Test
  void too_long_name_fails_size_max_100() {
    String longName = "A".repeat(101);
    CityCreateOrUpdateRequest dto = new CityCreateOrUpdateRequest(longName, "4209102");
    Set<ConstraintViolation<CityCreateOrUpdateRequest>> v = validator.validate(dto);

    assertFalse(v.isEmpty());
    assertTrue(
        v.stream()
            .anyMatch(
                cv ->
                    cv.getPropertyPath().toString().equals("name")
                        && cv.getConstraintDescriptor()
                            .getAnnotation()
                            .annotationType()
                            .equals(Size.class)));
  }

  @Test
  void blank_ibgeCode_fails_notblank() {
    CityCreateOrUpdateRequest dto = new CityCreateOrUpdateRequest("Jaraguá do Sul", "   ");
    Set<ConstraintViolation<CityCreateOrUpdateRequest>> v = validator.validate(dto);

    assertFalse(v.isEmpty());
    assertTrue(
        v.stream()
            .anyMatch(
                cv ->
                    cv.getPropertyPath().toString().equals("ibgeCode")
                        && cv.getConstraintDescriptor()
                            .getAnnotation()
                            .annotationType()
                            .equals(NotBlank.class)));
  }

  @Test
  void boundary_name_100_chars_is_ok() {
    String name100 = "A".repeat(100);
    CityCreateOrUpdateRequest dto = new CityCreateOrUpdateRequest(name100, "4209102");
    Set<ConstraintViolation<CityCreateOrUpdateRequest>> v = validator.validate(dto);
    assertTrue(v.isEmpty());
  }
}
