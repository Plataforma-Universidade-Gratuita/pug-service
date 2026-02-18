package com.pug.projects.domain;

import com.pug.projects.infra.persistence.EnrollmentEntity;

public interface EnrollmentRepository {
  void persist(EnrollmentEntity entity);
}
