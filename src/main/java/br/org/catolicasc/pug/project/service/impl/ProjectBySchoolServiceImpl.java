package br.org.catolicasc.pug.project.service.impl;

import br.org.catolicasc.pug.project.domain.ProjectBySchool;
import br.org.catolicasc.pug.project.domain.ProjectBySchoolRepository;
import br.org.catolicasc.pug.project.service.ProjectBySchoolService;
import br.org.catolicasc.pug.project.service.utils.ProjectBySchoolProcessor;
import br.org.catolicasc.pug.project.service.utils.ProjectProcessor;
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
 * Implementation of the {@link ProjectBySchoolService} command interface.
 *
 * <p>This application-scoped service orchestrates state mutations for the association between
 * projects and schools. It delegates aggregate construction to {@link ProjectProcessor} and
 * persistence concerns to the {@link ProjectBySchoolRepository}, enforcing domain validation before
 * write operations.
 */
@ApplicationScoped
public class ProjectBySchoolServiceImpl implements ProjectBySchoolService {

  private static final Logger LOG = Logger.getLogger(ProjectBySchoolServiceImpl.class);

  @Inject AuditPublisher auditPublisher;
  @Inject ProjectBySchoolRepository repo;

  /** {@inheritDoc} */
  @Transactional
  @Override
  public List<ProjectBySchool> save(UUID projectId, List<UUID> schoolIds) {
    if (projectId == null || CollectionUtils.isEmpty(schoolIds)) {
      LOG.debugf("ProjectBySchool save skipped: projectId=%s, schoolIds=%s", projectId, schoolIds);
      return List.of();
    }

    List<UUID> requestedSchoolIds = schoolIds.stream().filter(Objects::nonNull).distinct().toList();

    Set<UUID> existingSchoolIds = repo.findAllSchoolIdsByProjectId(projectId);

    LOG.debugf(
        "Creating ProjectBySchool associations for projectId=%s. Requested=%d, existing=%d",
        projectId, requestedSchoolIds.size(), existingSchoolIds.size());

    List<ProjectBySchool> created = new ArrayList<>();

    for (UUID schoolId : requestedSchoolIds) {
      if (existingSchoolIds.contains(schoolId)) {
        LOG.debugf(
            "Skipping: association already exists (projectId=%s, schoolId=%s)",
            projectId, schoolId);
        continue;
      }

      ProjectBySchool association =
          ProjectBySchoolProcessor.processCreateInput(projectId, schoolId);

      if (association.hasFieldErrors()) {
        throw new AppValidationException(association.getFieldErrors());
      }

      ProjectBySchool persisted = repo.persist(association);
      created.add(persisted);
    }

    LOG.infof(
        "Created %d new ProjectBySchool associations for projectId=%s", created.size(), projectId);

    auditPublisher.fireCreate(ProjectBySchool.class.getName(), projectId);
    return created;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public boolean delete(UUID projectId, UUID schoolId) {
    if (projectId == null || schoolId == null) {
      LOG.debugf(
          "Delete ProjectsBySchool skipped due to null identifier(s): projectId=%s, schoolId=%s",
          projectId, schoolId);
      return false;
    }

    LOG.debugf(
        "Attempting to delete ProjectsBySchool association: projectId=%s, schoolId=%s",
        projectId, schoolId);

    ProjectBySchool association =
        ProjectBySchool.builder().projectId(projectId).schoolId(schoolId).build();

    boolean deleted = repo.delete(association);
    if (deleted) {
      LOG.infof(
          "ProjectsBySchool association deleted successfully. projectId=%s, schoolId=%s",
          projectId, schoolId);
    } else {
      LOG.debugf(
          "ProjectsBySchool association not found for deletion. projectId=%s, schoolId=%s",
          projectId, schoolId);
    }

    if (deleted) {
      auditPublisher.fireDelete(ProjectBySchool.class.getName(), projectId);
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
      auditPublisher.fireDelete(ProjectBySchool.class.getName(), projectId);
    }
    return deleted;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public long deleteAllBySchoolId(UUID schoolId) {
    if (schoolId == null) {
      LOG.debug("deleteAllBySchoolId skipped: schoolId is null");
      return 0L;
    }

    LOG.debugf("Deleting all ProjectsBySchool associations for schoolId=%s", schoolId);
    long deleted = repo.deleteAllBySchoolId(schoolId);
    LOG.infof("Deleted %d ProjectsBySchool associations for schoolId=%s", deleted, schoolId);

    if (deleted > 0) {
      auditPublisher.fireDelete(ProjectBySchool.class.getName(), schoolId);
    }
    return deleted;
  }
}
