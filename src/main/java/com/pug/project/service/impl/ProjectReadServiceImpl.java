package com.pug.project.service.impl;

import com.pug.project.infra.read.ProjectQueries;
import com.pug.project.infra.read.dtos.ProjectView;
import com.pug.project.service.ProjectReadService;
import com.pug.project.service.utils.ExceptionHelper;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ProjectReadServiceImpl implements ProjectReadService {

  private static final Logger LOG = Logger.getLogger(ProjectReadServiceImpl.class);

  @Inject ProjectQueries queries;

  @Override
  public ProjectView getViewById(UUID id) {
    return queries
        .findOptionalById(id)
        .orElseThrow(
            () -> {
              LOG.debugf("Project lookup failed: ID %s not found", id);
              return ExceptionHelper.projectNotFound();
            });
  }

  @Override
  public List<ProjectView> listViews() {
    return queries.listAllProjects();
  }

  @Override
  public List<ProjectView> listViewsByEntityId(UUID entityId) {
    if (entityId == null) {
      return List.of();
    }
    return queries.listByEntityId(entityId);
  }

  @Override
  public List<ProjectView> searchByName(String query) {
    return queries.searchByName(StringUtils.fold(query));
  }
}
