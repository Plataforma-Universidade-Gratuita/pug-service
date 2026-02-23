package com.pug.projects.infra.persistence.impl;

import com.pug.projects.domain.ProjectRepository;
import com.pug.projects.infra.persistence.ProjectEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class ProjectRepositoryImpl
    implements ProjectRepository, PanacheRepositoryBase<ProjectEntity, UUID> {
  @Override
  public void persist(ProjectEntity project) {
    persistAndFlush(project);
  }
}
