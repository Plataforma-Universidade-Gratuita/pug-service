package com.pug.shared.presenter.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.f4b6a3.uuid.UuidCreator;
import com.pug.shared.validation.UuidV7;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class UuidsRequestTest {

  private static Validator validator;

  @BeforeAll
  static void init() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @AfterAll
  static void tearDown() {
    validator = null;
  }

  @Test
  void valid_v7_list_passes() {
    List<UUID> ids = List.of(UuidCreator.getTimeOrderedEpoch(), UuidCreator.getTimeOrderedEpoch());
    UuidsRequest req = new UuidsRequest(ids);
    Set<ConstraintViolation<UuidsRequest>> v = validator.validate(req);
    assertTrue(v.isEmpty());
  }

  @Test
  void empty_list_violates_NotEmpty() {
    UuidsRequest req = new UuidsRequest(List.of());
    Set<ConstraintViolation<UuidsRequest>> v = validator.validate(req);
    assertTrue(
        v.stream()
            .anyMatch(
                cv ->
                    cv.getConstraintDescriptor()
                        .getAnnotation()
                        .annotationType()
                        .equals(NotEmpty.class)));
  }

  @Test
  void null_element_violates_NotNull() {
    var ids = new java.util.ArrayList<UUID>();
    ids.add(com.github.f4b6a3.uuid.UuidCreator.getTimeOrderedEpoch());
    ids.add(null);

    Set<ConstraintViolation<UuidsRequest>> v =
        validator.validateValue(UuidsRequest.class, "ids", ids);

    assertFalse(v.isEmpty());
    assertTrue(
        v.stream()
            .anyMatch(
                cv ->
                    cv.getConstraintDescriptor()
                        .getAnnotation()
                        .annotationType()
                        .equals(jakarta.validation.constraints.NotNull.class)));
  }

  @Test
  void non_v7_element_violates_UuidV7() {
    List<UUID> ids = List.of(UUID.randomUUID());
    UuidsRequest req = new UuidsRequest(ids);

    Set<ConstraintViolation<UuidsRequest>> v = validator.validate(req);
    assertTrue(
        v.stream()
            .anyMatch(
                cv ->
                    cv.getConstraintDescriptor()
                        .getAnnotation()
                        .annotationType()
                        .equals(UuidV7.class)));
  }

  @Test
  void constructor_copies_and_exposes_unmodifiable_view() {
    var src = new ArrayList<>(List.of(UuidCreator.getTimeOrderedEpoch()));
    UuidsRequest req = new UuidsRequest(src);

    src.add(UuidCreator.getTimeOrderedEpoch());
    assertEquals(1, req.ids().size());

    assertThrows(
        UnsupportedOperationException.class,
        () -> req.ids().add(UuidCreator.getTimeOrderedEpoch()));
  }
}
