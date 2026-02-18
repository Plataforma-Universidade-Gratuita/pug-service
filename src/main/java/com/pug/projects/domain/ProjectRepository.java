package com.pug.projects.domain;

import com.pug.projects.infra.persistence.ProjectEntity;

public interface ProjectRepository {
  void persist(ProjectEntity entity);
}
