package com.pug.academic.domain;

import com.pug.academic.infra.persistence.CoursesEntity;

public interface CoursesRepository {
  void persist(CoursesEntity entity);
}
