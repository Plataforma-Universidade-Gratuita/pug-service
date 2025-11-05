package com.pug.projects.domain;

import com.pug.projects.infra.persistence.AttendancesEntity;

public interface AttendancesRepository {
  void persist(AttendancesEntity entity);
}
