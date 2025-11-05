package com.pug.geo.domain;

import com.pug.geo.infra.persistence.CitiesEntity;
import java.util.Optional;
import java.util.UUID;

public interface CitiesRepository {
  void persist(CitiesEntity city);

  void persistAll(Iterable<CitiesEntity> cities);

  Optional<CitiesEntity> findOptionalById(UUID id);

  Optional<CitiesEntity> findByIbgeCode(String ibgeCodeDigits);
}
