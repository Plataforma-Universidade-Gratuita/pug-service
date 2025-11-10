package com.pug.academic.domain;

import com.pug.academic.infra.persistence.StudentEntity;

public interface StudentRepository {
  void persist(StudentEntity entity);
}
