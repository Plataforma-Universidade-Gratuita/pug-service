package com.pug.geo.usecase.read;

import com.pug.geo.domain.City;
import com.pug.geo.domain.exceptions.CityNotFoundException;
import com.pug.geo.infra.persistence.CityRepository;
import com.pug.shared.dtos.ReadByPatternQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Locale;

@ApplicationScoped
public class ReadCityHandler {
  @Inject CityRepository repo;
  @Inject Validator validator;

  public City handle(ReadCityByIbgeCodeQuery q) {
    var v = validator.validate(q);
    if (!v.isEmpty()) throw new jakarta.validation.ConstraintViolationException(v);
    return repo.findByIbgeCode(q.ibgeCode().trim().toLowerCase(Locale.ROOT))
        .orElseThrow(() -> new CityNotFoundException(q.ibgeCode()));
  }

  public List<City> handle(ReadByPatternQuery q) {
    var v = validator.validate(q);
    if (!v.isEmpty()) throw new ConstraintViolationException(v);
    if (q.limit() != null) return repo.listByPattern(q.query(), q.limit());
    return repo.listAllSorted();
  }
}
