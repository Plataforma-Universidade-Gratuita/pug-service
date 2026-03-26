package com.pug.project.service.impl;

import com.pug.academic.infra.read.SchoolQueries;
import com.pug.academic.infra.read.dtos.SchoolView;
import com.pug.project.infra.read.ProjectQueries;
import com.pug.project.infra.read.ProjectsBySchoolsQueries;
import com.pug.project.infra.read.dtos.ProjectView;
import com.pug.project.infra.read.dtos.SchoolProjectView;
import com.pug.project.service.ProjectReadService;
import com.pug.project.service.utils.ExceptionHelper;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

/**
 * Implementation of the {@link ProjectReadService}.
 *
 * <p>This application-scoped bean delegates read-only operations to the underlying infrastructure
 * components. It handles basic input validation and translates "not found" states into standardized
 * domain exceptions.
 */
@ApplicationScoped
public class ProjectReadServiceImpl implements ProjectReadService {

  private static final Logger LOG = Logger.getLogger(ProjectReadServiceImpl.class);

  @Inject ProjectQueries queries;
  @Inject ProjectsBySchoolsQueries pbsQueries;
  @Inject SchoolQueries schoolQueries;

  /** {@inheritDoc} */
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

  /** {@inheritDoc} */
  @Override
  public List<ProjectView> listViews() {
    return queries.listAllProjects();
  }

  /** {@inheritDoc} */
  @Override
  public List<ProjectView> listViewsByCreatedBy(UUID accountId) {
    if (accountId == null) {
      return List.of();
    }
    return queries.listByCreatedBy(accountId);
  }

  /** {@inheritDoc} */
  @Override
  public List<ProjectView> listViewsByEntityId(UUID entityId) {
    if (entityId == null) {
      return List.of();
    }
    return queries.listByEntityId(entityId);
  }

  /** {@inheritDoc} */
  @Override
  public List<SchoolView> listViewsSchoolsByProjectId(UUID projectId) {
    if (projectId == null) {
      return List.of();
    }
    List<UUID> schoolIds = pbsQueries.listAllSchoolsIdsByProjectId(projectId);
    return schoolQueries.listByIds(schoolIds);
  }

  /** {@inheritDoc} */
  @Override
  public SchoolProjectView listViewsBySchool(UUID schoolId) {
    if (schoolId == null) {
      return null;
    }
    return pbsQueries.listBySchool(schoolId);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Prior to execution, the input query is "folded" (lowercased and accents removed via {@link
   * StringUtils#fold(String)}) to ensure maximum compatibility with the underlying search indexing
   * rules.
   */
  @Override
  public List<ProjectView> searchByName(String query) {
    if (StringUtils.isEmpty(query)) {
      return List.of();
    }
    String key = StringUtils.fold(query);
    return queries.searchByName(key);
  }
}
