package com.pug.geo.infra.persistence.impl;

import com.pug.geo.domain.City;
import com.pug.geo.domain.CityRepository;
import com.pug.geo.infra.CityMapper;
import com.pug.geo.infra.persistence.CityEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.UUID;

/** Implementation of the read-only CityRepository using Panache. */
@ApplicationScoped
public class CityRepositoryImpl implements CityRepository, PanacheRepositoryBase<CityEntity, UUID> {
  /** {@inheritDoc} */
  @Override
  public Optional<City> findOptionalById(UUID id) {
    if (id == null) {
      return Optional.empty();
    }
    return findByIdOptional(id).map(CityMapper::toDomain);
  }
}
