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

@ApplicationScoped
public class EnrollmentReadServiceImpl implements EnrollmentReadService {

  private static final Logger LOG = Logger.getLogger(EnrollmentReadServiceImpl.class);

  @Inject EnrollmentQueries queries;

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

  @Override
  public List<EnrollmentView> listViews() {
    return queries.listAllEnrollments();
  }

  @Override
  public List<EnrollmentView> listViewsByProjectId(UUID projectId) {
    if (projectId == null) return List.of();
    return queries.listByProjectId(projectId);
  }

  @Override
  public List<EnrollmentView> listViewsByStudentId(UUID studentId) {
    if (studentId == null) return List.of();
    return queries.listByStudentId(studentId);
  }
}
