package com.pug.projects.infra.persistence;

import com.pug.projects.domain.ProjectsRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class ProjectsRepositoryImpl
    implements ProjectsRepository, PanacheRepositoryBase<ProjectsEntity, UUID> {
  @Override
  public void persist(ProjectsEntity project) {
    persistAndFlush(project);
  }
}
