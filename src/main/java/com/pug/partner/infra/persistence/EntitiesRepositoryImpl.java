package com.pug.partner.infra.persistence;

import com.pug.partner.domain.EntitiesRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class EntitiesRepositoryImpl
    implements EntitiesRepository, PanacheRepositoryBase<EntitiesEntity, UUID> {
  @Override
  public void persist(EntitiesEntity entity) {
    persistAndFlush(entity);
  }
}
