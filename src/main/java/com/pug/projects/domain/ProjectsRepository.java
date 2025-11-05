package com.pug.projects.domain;

import com.pug.projects.infra.persistence.ProjectsEntity;

public interface ProjectsRepository {
  void persist(ProjectsEntity entity);
}
