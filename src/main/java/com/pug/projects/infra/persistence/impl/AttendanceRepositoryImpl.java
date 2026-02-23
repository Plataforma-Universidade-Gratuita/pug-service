package com.pug.projects.infra.persistence.impl;

import com.pug.projects.domain.AttendanceRepository;
import com.pug.projects.infra.persistence.AttendanceEntity;
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
