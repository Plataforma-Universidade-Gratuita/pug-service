package com.pug.projects.infra.persistence;

import com.pug.projects.domain.AttendancesRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class AttendancesRepositoryImpl
    implements AttendancesRepository, PanacheRepositoryBase<AttendancesEntity, UUID> {
  @Override
  public void persist(AttendancesEntity attendance) {
    persistAndFlush(attendance);
  }
}
