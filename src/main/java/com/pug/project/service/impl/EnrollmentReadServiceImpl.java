package com.pug.project.service.impl;

import com.pug.project.infra.read.EnrollmentQueries;
import com.pug.project.infra.read.dtos.EnrollmentView;
import com.pug.project.service.EnrollmentReadService;
import com.pug.project.service.utils.ExceptionHelper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

/**
 * Implementation of the {@link EnrollmentReadService}.
 *
 * <p>This application-scoped bean delegates read-only operations to the underlying {@link
 * EnrollmentQueries} infrastructure component. It handles basic input validation, translates "not
 * found" states into standardized domain exceptions, and exposes lightweight {@link EnrollmentView}
 * projections to the presentation layer.
 */
@ApplicationScoped
public class EnrollmentReadServiceImpl implements EnrollmentReadService {

  private static final Logger LOG = Logger.getLogger(EnrollmentReadServiceImpl.class);

  @Inject EnrollmentQueries queries;

  /** {@inheritDoc} */
  @Override
  public EnrollmentView getViewByIds(UUID projectId, UUID studentId) {
    return queries
        .findOptionalByIds(projectId, studentId)
        .orElseThrow(
            () -> {
              LOG.debugf("Enrollment lookup failed: Project %s, Student %s", projectId, studentId);
              return ExceptionHelper.enrollmentNotFound();
            });
  }

  /** {@inheritDoc} */
  @Override
  public List<EnrollmentView> listViews() {
    return queries.listAllEnrollments();
  }

  /** {@inheritDoc} */
  @Override
  public List<EnrollmentView> listViewsByProjectId(UUID projectId) {
    if (projectId == null) {
      return List.of();
    }
    return queries.listByProjectId(projectId);
  }

  /** {@inheritDoc} */
  @Override
  public List<EnrollmentView> listViewsByStudentId(UUID studentId) {
    if (studentId == null) {
      return List.of();
    }
    return queries.listByStudentId(studentId);
  }
}
