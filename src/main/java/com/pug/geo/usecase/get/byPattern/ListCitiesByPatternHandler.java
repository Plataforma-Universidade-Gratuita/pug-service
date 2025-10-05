package com.pug.geo.usecase.get.byPattern;

import com.pug.geo.domain.City;
import com.pug.geo.infra.persistence.CityRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.List;

@ApplicationScoped
public class ListCitiesByPatternHandler {
  @Inject CityRepository repo;
  @Inject Validator validator;

  public List<City> handle(ListCitiesByPatternQuery q) {
    var v = validator.validate(q);
    if (!v.isEmpty()) throw new ConstraintViolationException(v);
    if (q.limit() != null) return repo.listByPattern(q.query(), q.limit());
    return repo.listAllSorted();
  }
}
