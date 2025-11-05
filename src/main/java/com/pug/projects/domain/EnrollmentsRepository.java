package com.pug.projects.domain;

import com.pug.projects.infra.persistence.EnrollmentsEntity;

public interface EnrollmentsRepository {
  void persist(EnrollmentsEntity entity);
}
