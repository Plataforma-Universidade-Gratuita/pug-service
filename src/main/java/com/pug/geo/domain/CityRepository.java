package com.pug.geo.domain;

import com.pug.shared.infra.persistence.Page;
import com.pug.shared.infra.persistence.PageRequest;
import java.util.Optional;
import java.util.UUID;

public interface CityRepository {
  City save(City city);

  Optional<City> findOptionalById(UUID id);

  Optional<City> findByIbgeCode(String ibgeCodeDigits);

  Page<City> listByPattern(String pattern, PageRequest pr);
}
