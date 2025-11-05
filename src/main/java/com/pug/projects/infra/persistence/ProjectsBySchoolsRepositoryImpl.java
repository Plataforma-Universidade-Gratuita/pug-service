package com.pug.projects.infra.persistence;

import com.pug.projects.domain.ProjectsBySchoolsRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class ProjectsBySchoolsRepositoryImpl
    implements ProjectsBySchoolsRepository, PanacheRepositoryBase<ProjectsBySchoolsEntity, UUID> {
  @Override
  public void persist(ProjectsBySchoolsEntity pbs) {
    persistAndFlush(pbs);
  }
}
