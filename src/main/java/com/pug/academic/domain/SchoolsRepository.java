package com.pug.academic.domain;

import com.pug.academic.infra.persistence.SchoolsEntity;

public interface SchoolsRepository {
  void persist(SchoolsEntity entity);
}
