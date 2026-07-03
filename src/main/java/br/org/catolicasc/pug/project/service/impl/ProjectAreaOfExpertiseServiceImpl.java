/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.project.service.impl;

import br.org.catolicasc.pug.academic.domain.AreaOfExpertise;
import br.org.catolicasc.pug.project.domain.ProjectAreaOfExpertise;
import br.org.catolicasc.pug.project.domain.ProjectAreaOfExpertiseRepository;
import br.org.catolicasc.pug.project.service.ProjectAreaOfExpertiseService;
import br.org.catolicasc.pug.project.service.utils.ProjectAreaOfExpertiseProcessor;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import br.org.catolicasc.pug.shared.infra.audit.AuditPublisher;
import br.org.catolicasc.pug.shared.utils.CollectionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.jboss.logging.Logger;

/**
 * Implementation of the {@link ProjectAreaOfExpertiseService} command interface.
 *
 * <p>This application-scoped service orchestrates state mutations for the association between
 * projects and areas of expertise. It delegates aggregate construction to {@link
 * ProjectAreaOfExpertiseProcessor} and persistence concerns to the {@link
 * ProjectAreaOfExpertiseRepository}, enforcing domain validation before write operations.
 */
@ApplicationScoped
public class ProjectAreaOfExpertiseServiceImpl implements ProjectAreaOfExpertiseService {

  private static final Logger LOG = Logger.getLogger(ProjectAreaOfExpertiseServiceImpl.class);

  @Inject AuditPublisher auditPublisher;
  @Inject ProjectAreaOfExpertiseRepository repo;

  /** {@inheritDoc} */
  @Override
  public List<AreaOfExpertise> listByProjects(UUID projectId) {
    return repo.findAllAreasOfExpertiseByProjectId(projectId);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public List<ProjectAreaOfExpertise> save(UUID projectId, List<UUID> areaOfExpertiseIds) {
    if (projectId == null || CollectionUtils.isEmpty(areaOfExpertiseIds)) {
      LOG.debugf(
          "ProjectBySchool save skipped: projectId=%s, areaOfExpertiseIds=%s",
          projectId, areaOfExpertiseIds);
      return List.of();
    }

    List<UUID> requestedAreaOfExpertiseIds =
        areaOfExpertiseIds.stream().filter(Objects::nonNull).distinct().toList();

    Set<UUID> existingAreaOfExpertiseIds = repo.findAllAreaOfExpertiseIdsByProjectId(projectId);

    LOG.debugf(
        "Creating ProjectBySchool associations for projectId=%s. Requested=%d, existing=%d",
        projectId, requestedAreaOfExpertiseIds.size(), existingAreaOfExpertiseIds.size());

    List<ProjectAreaOfExpertise> created = new ArrayList<>();

    for (UUID areaOfExpertiseId : requestedAreaOfExpertiseIds) {
      if (existingAreaOfExpertiseIds.contains(areaOfExpertiseId)) {
        LOG.debugf(
            "Skipping: association already exists (projectId=%s, areaOfExpertiseId=%s)",
            projectId, areaOfExpertiseId);
        continue;
      }

      ProjectAreaOfExpertise association =
          ProjectAreaOfExpertiseProcessor.processCreateInput(projectId, areaOfExpertiseId);

      if (association.hasFieldErrors()) {
        throw new AppValidationException(association.getFieldErrors());
      }

      ProjectAreaOfExpertise persisted = repo.persist(association);
      created.add(persisted);
    }

    LOG.infof(
        "Created %d new ProjectBySchool associations for projectId=%s", created.size(), projectId);

    auditPublisher.fireCreate(ProjectAreaOfExpertise.class.getName(), projectId);
    return created;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public boolean delete(UUID projectId, UUID areaOfExpertiseId) {
    if (projectId == null || areaOfExpertiseId == null) {
      LOG.debugf(
          "Delete project-area-of-expertise skipped due to null identifier(s):"
              + " projectId=%s, areaOfExpertiseId=%s",
          projectId, areaOfExpertiseId);
      return false;
    }

    LOG.debugf(
        "Attempting to delete ProjectsBySchool association: projectId=%s, areaOfExpertiseId=%s",
        projectId, areaOfExpertiseId);

    ProjectAreaOfExpertise association =
        ProjectAreaOfExpertise.builder()
            .projectId(projectId)
            .areaOfExpertiseId(areaOfExpertiseId)
            .build();

    boolean deleted = repo.delete(association);
    if (deleted) {
      LOG.infof(
          "ProjectsBySchool association deleted successfully. projectId=%s, areaOfExpertiseId=%s",
          projectId, areaOfExpertiseId);
    } else {
      LOG.debugf(
          "ProjectsBySchool association not found for deletion. projectId=%s, areaOfExpertiseId=%s",
          projectId, areaOfExpertiseId);
    }

    if (deleted) {
      auditPublisher.fireDelete(ProjectAreaOfExpertise.class.getName(), projectId);
    }
    return deleted;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public long deleteAllByProjectId(UUID projectId) {
    if (projectId == null) {
      LOG.debug("deleteAllByProjectId skipped: projectId is null");
      return 0L;
    }

    LOG.debugf("Deleting all ProjectsBySchool associations for projectId=%s", projectId);
    long deleted = repo.deleteAllByProjectId(projectId);
    LOG.infof("Deleted %d ProjectsBySchool associations for projectId=%s", deleted, projectId);

    if (deleted > 0) {
      auditPublisher.fireDelete(ProjectAreaOfExpertise.class.getName(), projectId);
    }
    return deleted;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public long deleteAllByAreaOfExpertiseId(UUID areaOfExpertiseId) {
    if (areaOfExpertiseId == null) {
      LOG.debug("deleteAllByAreaOfExpertiseId skipped: areaOfExpertiseId is null");
      return 0L;
    }

    LOG.debugf(
        "Deleting all ProjectsBySchool associations for areaOfExpertiseId=%s", areaOfExpertiseId);
    long deleted = repo.deleteAllByAreaOfExpertiseId(areaOfExpertiseId);
    LOG.infof(
        "Deleted %d ProjectsBySchool associations for areaOfExpertiseId=%s",
        deleted, areaOfExpertiseId);

    if (deleted > 0) {
      auditPublisher.fireDelete(ProjectAreaOfExpertise.class.getName(), areaOfExpertiseId);
    }
    return deleted;
  }
}
