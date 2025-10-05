package com.pug.geo.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.pug.geo.domain.City;
import com.pug.geo.infra.persistence.CityRepository;
import com.pug.geo.usecase.get.byPattern.ListCitiesByPatternHandler;
import com.pug.geo.usecase.get.byPattern.ListCitiesByPatternQuery;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListCitiesByPatternHandlerTest {

  @Mock CityRepository repo;

  @SuppressWarnings("unused")
  @Spy
  Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @InjectMocks ListCitiesByPatternHandler handler;

  @Test
  void returnsByPatternWhenLimitProvided() {
    when(repo.listByPattern("flo", 10)).thenReturn(List.of(new City()));

    var out = handler.handle(new ListCitiesByPatternQuery("flo", 10));

    assertEquals(1, out.size());
    verify(repo).listByPattern("flo", 10);
    verifyNoMoreInteractions(repo);
  }

  @Test
  void returnsAllWhenLimitIsNull() {
    when(repo.listAllSorted()).thenReturn(List.of(new City()));

    var out = handler.handle(new ListCitiesByPatternQuery("any", null));

    assertEquals(1, out.size());
    verify(repo).listAllSorted();
    verifyNoMoreInteractions(repo);
  }

  @Test
  void nullQueryFailsValidation() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new ListCitiesByPatternQuery(null, 5)));

    verifyNoInteractions(repo);
  }

  @Test
  void limitBelowMinFailsValidation() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new ListCitiesByPatternQuery("x", 0)));

    verifyNoInteractions(repo);
  }

  @Test
  void limitAboveMaxFailsValidation() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new ListCitiesByPatternQuery("x", 201)));

    verifyNoInteractions(repo);
  }
}
