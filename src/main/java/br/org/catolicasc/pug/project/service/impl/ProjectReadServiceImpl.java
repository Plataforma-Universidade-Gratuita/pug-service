package br.org.catolicasc.pug.project.service.impl;

import br.org.catolicasc.pug.project.infra.read.ProjectQueries;
import br.org.catolicasc.pug.project.infra.read.dtos.ProjectView;
import br.org.catolicasc.pug.project.service.ProjectReadService;
import br.org.catolicasc.pug.project.service.dtos.ProjectComplexSearchCriteria;
import br.org.catolicasc.pug.project.service.utils.ExceptionHelper;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import br.org.catolicasc.pug.shared.utils.CollectionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

/**
 * Implementation of the {@link ProjectReadService}.
 *
 * <p>This application-scoped bean delegates project queries to the underlying infrastructure
 * components and translates missing rows into the module's standardized not-found exception.
 */
@ApplicationScoped
public class ProjectReadServiceImpl implements ProjectReadService {

  private static final Logger LOG = Logger.getLogger(ProjectReadServiceImpl.class);

  @Inject ProjectQueries queries;

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
    return queries.listAll();
  }

  /** {@inheritDoc} */
  @Override
  public List<ProjectView> listViewsByIds(List<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return queries.listAll();
    }
    return queries.listAllByIds(ids);
  }

  /** {@inheritDoc} */
  @Override
  public List<ProjectView> listViewsByCreatedBy(UUID accountId) {
    if (accountId == null) {
      return List.of();
    }
    return queries.listAllByCreatedBy(accountId);
  }

  /** {@inheritDoc} */
  @Override
  public List<ProjectView> listViewsByEntityId(UUID entityId) {
    if (entityId == null) {
      return List.of();
    }
    return queries.listAllByEntityId(entityId);
  }

  /** {@inheritDoc} */
  @Override
  public PageResult<ProjectView> search(
      ProjectComplexSearchCriteria criteria, PageQuery pageQuery) {
    return queries.search(criteria, pageQuery);
  }
}
