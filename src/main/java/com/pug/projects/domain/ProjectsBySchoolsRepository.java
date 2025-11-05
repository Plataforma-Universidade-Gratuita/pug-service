package com.pug.projects.domain;

import com.pug.projects.infra.persistence.ProjectsBySchoolsEntity;

public interface ProjectsBySchoolsRepository {
  void persist(ProjectsBySchoolsEntity entity);
}
