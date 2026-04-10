package com.pug.project.service.impl;

import com.pug.project.infra.read.AttendanceQueries;
import com.pug.project.infra.read.dtos.AttendanceView;
import com.pug.project.service.AttendanceReadService;
import com.pug.project.service.utils.ExceptionHelper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

/**
 * Implementation of the {@link AttendanceReadService}.
 *
 * <p>Delegates read-only operations to the underlying {@link AttendanceQueries} infrastructure
 * component.
 */
@ApplicationScoped
public class AttendanceReadServiceImpl implements AttendanceReadService {

  private static final Logger LOG = Logger.getLogger(AttendanceReadServiceImpl.class);

  @Inject AttendanceQueries queries;

  /** {@inheritDoc} */
  @Override
  public AttendanceView getViewById(UUID id) {
    return queries
        .findOptionalById(id)
        .orElseThrow(
            () -> {
              LOG.debugf("Attendance lookup failed: ID %s not found", id);
              return ExceptionHelper.attendanceNotFound();
            });
  }

  /** {@inheritDoc} */
  @Override
  public List<AttendanceView> listByEnrollmentId(UUID projectId, UUID studentId) {
    if (projectId == null || studentId == null) {
      return List.of();
    }
    return queries.listByEnrollmentId(projectId, studentId);
  }

  /** {@inheritDoc} */
  @Override
  public List<AttendanceView> listByProjectId(UUID projectId) {
    if (projectId == null) {
      return List.of();
    }
    return queries.listByProjectId(projectId);
  }

  /** {@inheritDoc} */
  @Override
  public List<AttendanceView> listByStudentId(UUID studentId) {
    if (studentId == null) {
      return List.of();
    }
    return queries.listByStudentId(studentId);
  }

  /** {@inheritDoc} */
  @Override
  public List<AttendanceView> listViews() {
    return queries.listViews();
  }
}
