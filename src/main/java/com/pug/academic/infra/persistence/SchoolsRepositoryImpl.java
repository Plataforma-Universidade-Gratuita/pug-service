package com.pug.academic.infra.persistence;

import com.pug.academic.domain.SchoolsRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class SchoolsRepositoryImpl
    implements SchoolsRepository, PanacheRepositoryBase<SchoolsEntity, UUID> {
  @Override
  public void persist(SchoolsEntity school) {
    persistAndFlush(school);
  }
}
