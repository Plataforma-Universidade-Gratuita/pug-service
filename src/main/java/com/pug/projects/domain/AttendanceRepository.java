package com.pug.projects.domain;

import com.pug.projects.infra.persistence.AttendanceEntity;

public interface AttendanceRepository {
  void persist(AttendanceEntity entity);
}
