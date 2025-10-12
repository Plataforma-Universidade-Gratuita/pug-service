package com.pug.geo.service.queries;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.shared.infra.persistence.PageRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

class SearchCitiesQueryTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void validQueryPasses() {
    var q = new SearchCitiesQuery("flor", new PageRequest(0, 10));
    var v = validator.validate(q);
    assertTrue(v.isEmpty());
  }

  @Test
  void patternMustBeAtLeastTwoAndNotBlank() {
    var q1 = new SearchCitiesQuery(" ", new PageRequest(0, 10));
    assertFalse(validator.validate(q1).isEmpty());

    var q2 = new SearchCitiesQuery("a", new PageRequest(0, 10));
    assertFalse(validator.validate(q2).isEmpty());
  }

  @Test
  void pageRequestGuardsBoundsViaCtor() {
    assertThrows(
        IllegalArgumentException.class, () -> new SearchCitiesQuery("ok", new PageRequest(-1, 10)));
    assertThrows(
        IllegalArgumentException.class, () -> new SearchCitiesQuery("ok", new PageRequest(0, 0)));
    assertThrows(
        IllegalArgumentException.class,
        () -> new SearchCitiesQuery("ok", new PageRequest(0, 1001)));
  }
}
