package com.pug.academic.domain;

import com.pug.academic.infra.persistence.StudentsEntity;

public interface StudentsRepository {
  void persist(StudentsEntity entity);
}
