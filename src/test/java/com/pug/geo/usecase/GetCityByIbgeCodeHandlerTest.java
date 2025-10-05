package com.pug.geo.usecase;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.pug.geo.domain.City;
import com.pug.geo.domain.exceptions.CityNotFoundException;
import com.pug.geo.infra.persistence.CityRepository;
import com.pug.geo.usecase.get.byIbgeCode.GetCityByIbgeCodeHandler;
import com.pug.geo.usecase.get.byIbgeCode.GetCityByIbgeCodeQuery;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetCityByIbgeCodeHandlerTest {

  @Mock CityRepository repo;

  @SuppressWarnings("unused")
  @Spy
  Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @InjectMocks GetCityByIbgeCodeHandler handler;

  @Test
  void returnsCityWhenFound() {
    var city = new City();
    when(repo.findByIbgeCode("4205407")).thenReturn(Optional.of(city));

    var out = handler.handle(new GetCityByIbgeCodeQuery("4205407"));

    assertSame(city, out);
    verify(repo).findByIbgeCode("4205407");
    verifyNoMoreInteractions(repo);
  }

  @Test
  void throwsNotFoundWhenRepoEmpty() {
    when(repo.findByIbgeCode("4209102")).thenReturn(Optional.empty());

    assertThrows(
        CityNotFoundException.class, () -> handler.handle(new GetCityByIbgeCodeQuery("4209102")));

    verify(repo).findByIbgeCode("4209102");
    verifyNoMoreInteractions(repo);
  }

  @Test
  void blankCodeFailsValidationAndDoesNotQueryRepo() {
    assertThrows(
        ConstraintViolationException.class,
        () -> handler.handle(new GetCityByIbgeCodeQuery("   ")));

    verifyNoInteractions(repo);
  }

  @Test
  void nullCodeFailsValidationAndDoesNotQueryRepo() {
    assertThrows(
        ConstraintViolationException.class, () -> handler.handle(new GetCityByIbgeCodeQuery(null)));

    verifyNoInteractions(repo);
  }
}
