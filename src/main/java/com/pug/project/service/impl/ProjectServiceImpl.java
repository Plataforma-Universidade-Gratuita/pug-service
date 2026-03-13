package com.pug.project.service.impl;

import com.pug.project.domain.ProjectRepository;
import com.pug.project.service.ProjectService;
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
