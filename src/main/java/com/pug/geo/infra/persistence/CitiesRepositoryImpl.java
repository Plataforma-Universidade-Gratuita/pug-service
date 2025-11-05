package com.pug.geo.infra.persistence;

import com.pug.geo.domain.CitiesRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class CitiesRepositoryImpl
    implements CitiesRepository, PanacheRepositoryBase<CitiesEntity, UUID> {
  @Override
  public void persist(CitiesEntity city) {
    persistAndFlush(city);
  }
}
