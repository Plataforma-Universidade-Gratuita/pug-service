package br.org.catolicasc.pug.geo.infra.persistence.impl;

import br.org.catolicasc.pug.geo.domain.City;
import br.org.catolicasc.pug.geo.domain.CityRepository;
import br.org.catolicasc.pug.geo.infra.CityMapper;
import br.org.catolicasc.pug.geo.infra.persistence.CityEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.UUID;

/**
 * Panache-backed implementation of the read-only {@link CityRepository}.
 *
 * <p>This repository reconstitutes immutable {@link City} aggregates from persisted {@link
 * CityEntity} rows while preserving the geo module's read-only dictionary semantics.
 */
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
