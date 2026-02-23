package com.pug.projects.infra.persistence.impl;

import com.pug.projects.domain.EnrollmentRepository;
import com.pug.projects.infra.persistence.EnrollmentEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class EnrollmentRepositoryImpl
    implements EnrollmentRepository, PanacheRepositoryBase<EnrollmentEntity, UUID> {
  @Override
  public void persist(EnrollmentEntity enrollment) {
    persistAndFlush(enrollment);
  }
}
