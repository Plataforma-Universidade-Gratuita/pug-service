package br.org.catolicasc.pug.project.service.impl;

import br.org.catolicasc.pug.project.domain.ProjectSchool;
import br.org.catolicasc.pug.project.domain.ProjectSchoolRepository;
import br.org.catolicasc.pug.project.service.ProjectSchoolService;
import br.org.catolicasc.pug.project.service.utils.ProjectProcessor;
import br.org.catolicasc.pug.project.service.utils.ProjectSchoolProcessor;
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
 * Implementation of the {@link ProjectSchoolService} command interface.
 *
 * <p>This application-scoped service orchestrates state mutations for the association between
 * projects and schools. It delegates aggregate construction to {@link ProjectProcessor} and
 * persistence concerns to the {@link ProjectSchoolRepository}, enforcing domain validation before
 * write operations.
 */
@ApplicationScoped
public class ProjectSchoolServiceImpl implements ProjectSchoolService {

  private static final Logger LOG = Logger.getLogger(ProjectSchoolServiceImpl.class);

  @Inject AuditPublisher auditPublisher;
  @Inject ProjectSchoolRepository repo;

  /** {@inheritDoc} */
  @Transactional
  @Override
  public List<ProjectSchool> save(UUID projectId, List<UUID> areaOfExpertiseIds) {
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

    List<ProjectSchool> created = new ArrayList<>();

    for (UUID areaOfExpertiseId : requestedAreaOfExpertiseIds) {
      if (existingAreaOfExpertiseIds.contains(areaOfExpertiseId)) {
        LOG.debugf(
            "Skipping: association already exists (projectId=%s, areaOfExpertiseId=%s)",
            projectId, areaOfExpertiseId);
        continue;
      }

      ProjectSchool association =
          ProjectSchoolProcessor.processCreateInput(projectId, areaOfExpertiseId);

      if (association.hasFieldErrors()) {
        throw new AppValidationException(association.getFieldErrors());
      }

      ProjectSchool persisted = repo.persist(association);
      created.add(persisted);
    }

    LOG.infof(
        "Created %d new ProjectBySchool associations for projectId=%s", created.size(), projectId);

    auditPublisher.fireCreate(ProjectSchool.class.getName(), projectId);
    return created;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public boolean delete(UUID projectId, UUID areaOfExpertiseId) {
    if (projectId == null || areaOfExpertiseId == null) {
      LOG.debugf(
          "Delete ProjectsBySchool skipped due to null identifier(s): projectId=%s, areaOfExpertiseId=%s",
          projectId, areaOfExpertiseId);
      return false;
    }

    LOG.debugf(
        "Attempting to delete ProjectsBySchool association: projectId=%s, areaOfExpertiseId=%s",
        projectId, areaOfExpertiseId);

    ProjectSchool association =
        ProjectSchool.builder().projectId(projectId).areaOfExpertiseId(areaOfExpertiseId).build();

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
      auditPublisher.fireDelete(ProjectSchool.class.getName(), projectId);
    }
    return deleted;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public long deleteAllByProjectId(UUID projectId) {
    if (projectId == null) {
      LOG.debug("deleteAllByProjectId skipped: projectId is null");
      return 0L;
    }

    LOG.debugf("Deleting all ProjectsBySchool associations for projectId=%s", projectId);
    long deleted = repo.deleteAllByProjectId(projectId);
    LOG.infof("Deleted %d ProjectsBySchool associations for projectId=%s", deleted, projectId);

    if (deleted > 0) {
      auditPublisher.fireDelete(ProjectSchool.class.getName(), projectId);
    }
    return deleted;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
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
      auditPublisher.fireDelete(ProjectSchool.class.getName(), areaOfExpertiseId);
    }
    return deleted;
  }
}
