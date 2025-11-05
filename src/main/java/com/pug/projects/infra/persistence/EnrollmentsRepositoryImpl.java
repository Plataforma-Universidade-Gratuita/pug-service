package com.pug.projects.infra.persistence;

import com.pug.projects.domain.EnrollmentsRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class EnrollmentsRepositoryImpl
    implements EnrollmentsRepository, PanacheRepositoryBase<EnrollmentsEntity, UUID> {
  @Override
  public void persist(EnrollmentsEntity enrollment) {
    persistAndFlush(enrollment);
  }
}
