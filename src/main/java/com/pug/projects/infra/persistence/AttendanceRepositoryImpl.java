package com.pug.projects.infra.persistence;

import com.pug.projects.domain.AttendanceRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class AttendanceRepositoryImpl
    implements AttendanceRepository, PanacheRepositoryBase<AttendanceEntity, UUID> {
  @Override
  public void persist(AttendanceEntity attendance) {
    persistAndFlush(attendance);
  }
}
