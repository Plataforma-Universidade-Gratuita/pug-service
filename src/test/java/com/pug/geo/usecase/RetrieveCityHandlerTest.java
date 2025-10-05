package com.pug.geo.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.pug.geo.domain.City;
import com.pug.geo.domain.exceptions.CityNotFoundException;
import com.pug.geo.infra.persistence.CityRepository;
import com.pug.geo.usecase.get.RetrieveCitiesByPatternQuery;
import com.pug.geo.usecase.get.RetrieveCityByIbgeCodeQuery;
import com.pug.geo.usecase.get.RetrieveCityHandler;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RetrieveCityHandlerTest {

  @Mock CityRepository repo;

  @SuppressWarnings("unused")
  @Spy
  Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @InjectMocks RetrieveCityHandler handler;

  @Test
  void returnsByPatternWhenLimitProvided() {
    when(repo.listByPattern("flo", 10)).thenReturn(List.of(new City()));

    var out = handler.handle(new RetrieveCitiesByPatternQuery("flo", 10));

    assertEquals(1, out.size());
    verify(repo).listByPattern("flo", 10);
    verifyNoMoreInteractions(repo);
  }

  @Test
  void returnsAllWhenLimitIsNull() {
    when(repo.listAllSorted()).thenReturn(List.of(new City()));

    var out = handler.handle(new RetrieveCitiesByPatternQuery("any", null));

    assertEquals(1, out.size());
    verify(repo).listAllSorted();
    verifyNoMoreInteractions(repo);
  }

  @Test
  void nullQueryFailsValidation() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new RetrieveCitiesByPatternQuery(null, 5)));

    verifyNoInteractions(repo);
  }

  @Test
  void limitBelowMinFailsValidation() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new RetrieveCitiesByPatternQuery("x", 0)));

    verifyNoInteractions(repo);
  }

  @Test
  void limitAboveMaxFailsValidation() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new RetrieveCitiesByPatternQuery("x", 201)));

    verifyNoInteractions(repo);
  }

  @Test
  void returnsCityWhenFound() {
    var city = new City();
    when(repo.findByIbgeCode("4205407")).thenReturn(Optional.of(city));

    var out = handler.handle(new RetrieveCityByIbgeCodeQuery("4205407"));

    assertSame(city, out);
    verify(repo).findByIbgeCode("4205407");
    verifyNoMoreInteractions(repo);
  }

  @Test
  void throwsNotFoundWhenRepoEmpty() {
    when(repo.findByIbgeCode("4209102")).thenReturn(Optional.empty());

    assertThrows(
        CityNotFoundException.class,
        () -> handler.handle(new RetrieveCityByIbgeCodeQuery("4209102")));

    verify(repo).findByIbgeCode("4209102");
    verifyNoMoreInteractions(repo);
  }

  @Test
  void blankCodeFailsValidationAndDoesNotQueryRepo() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new RetrieveCityByIbgeCodeQuery("   ")));

    verifyNoInteractions(repo);
  }

  @Test
  void nullCodeFailsValidationAndDoesNotQueryRepo() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new RetrieveCityByIbgeCodeQuery(null)));

    verifyNoInteractions(repo);
  }
}
