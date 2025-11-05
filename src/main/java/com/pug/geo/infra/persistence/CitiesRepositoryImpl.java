package com.pug.geo.infra.persistence;

import com.pug.geo.domain.CitiesRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CitiesRepositoryImpl
    implements CitiesRepository, PanacheRepositoryBase<CitiesEntity, UUID> {
  @Override
  public void persist(CitiesEntity city) {
    persistAndFlush(city);
  }

  @Override
  public void persistAll(Iterable<CitiesEntity> cities) {
    persist(cities);
    flush();
  }

  @Override
  public Optional<CitiesEntity> findOptionalById(UUID id) {
    return findByIdOptional(id);
  }

  @Override
  public Optional<CitiesEntity> findByIbgeCode(String ibgeCodeDigits) {
    return find("ibgeCode", ibgeCodeDigits).firstResultOptional();
  }
}
