package com.pug.geo.usecase.get.byIbgeCode;

import com.pug.geo.domain.City;
import com.pug.geo.domain.exceptions.CityNotFoundException;
import com.pug.geo.infra.persistence.CityRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Validator;
import java.util.Locale;

@ApplicationScoped
public class GetCityByIbgeCodeHandler {
  @Inject CityRepository repo;
  @Inject Validator validator;

  public City handle(GetCityByIbgeCodeQuery q) {
    var v = validator.validate(q);
    if (!v.isEmpty()) throw new jakarta.validation.ConstraintViolationException(v);
    return repo.findByIbgeCode(q.ibgeCode().trim().toLowerCase(Locale.ROOT))
        .orElseThrow(() -> new CityNotFoundException(q.ibgeCode()));
  }
}
