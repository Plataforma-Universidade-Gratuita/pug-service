package com.pug.geo.service;

import com.pug.geo.domain.City;
import com.pug.geo.domain.CityRepository;
import com.pug.geo.service.queries.SearchCitiesQuery;
import com.pug.shared.application.UuidQuery;
import com.pug.shared.infra.persistence.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;

@ApplicationScoped
public class CityService {

  @Inject CityRepository repo;

  public java.util.Optional<City> getById(@Valid UuidQuery q) {
    return repo.findOptionalById(q.id());
  }

  public Page<City> search(@Valid SearchCitiesQuery q) {
    return repo.listByPattern(q.pattern(), q.pageRequest());
  }
}
