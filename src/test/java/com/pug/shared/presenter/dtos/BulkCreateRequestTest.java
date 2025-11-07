package com.pug.shared.presenter.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class BulkCreateRequestTest {

  private static Validator validator;

  record Dummy(@NotBlank String name) {}

  @BeforeAll
  static void init() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @AfterAll
  static void tearDown() {
    validator = null;
  }

  @Test
  void not_empty_is_enforced() {
    BulkCreateRequest<String> req = new BulkCreateRequest<>(List.of());
    Set<ConstraintViolation<BulkCreateRequest<String>>> v = validator.validate(req);
    assertFalse(v.isEmpty());
  }

  @Test
  void element_validation_is_propagated_via_Valid() {
    BulkCreateRequest<Dummy> req = new BulkCreateRequest<>(List.of(new Dummy("")));
    Set<ConstraintViolation<BulkCreateRequest<Dummy>>> v = validator.validate(req);
    assertTrue(
        v.stream()
            .anyMatch(
                cv ->
                    cv.getConstraintDescriptor()
                        .getAnnotation()
                        .annotationType()
                        .equals(NotBlank.class)));
  }

  @Test
  void constructor_copies_and_exposes_unmodifiable_view() {
    var src = new ArrayList<>(List.of("a", "b"));
    BulkCreateRequest<String> req = new BulkCreateRequest<>(src);

    src.add("c");
    assertEquals(2, req.entities().size());

    assertThrows(UnsupportedOperationException.class, () -> req.entities().add("x"));
  }

  @Test
  void entities_method_returns_copy() {
    BulkCreateRequest<String> req = new BulkCreateRequest<>(List.of("a"));
    List<String> first = req.entities();
    List<String> second = req.entities();
    assertEquals(first, second);
    assertThrows(UnsupportedOperationException.class, () -> first.add("x"));
    assertThrows(UnsupportedOperationException.class, () -> second.add("y"));
  }
}
