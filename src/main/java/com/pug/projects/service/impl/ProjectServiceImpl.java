package com.pug.projects.service.impl;

import com.pug.projects.domain.ProjectRepository;
import com.pug.projects.service.ProjectService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.UUID;

@ApplicationScoped
public class ProjectServiceImpl implements ProjectService {
  @Inject ProjectRepository repo;

  @Override
  public boolean existsAnyByEntityId(UUID entityId) {
    return repo.existsByEntityId(entityId);
  }

  @Override
  public boolean existsByCreatedBy(UUID accountId) {
    return false;
  }
}
