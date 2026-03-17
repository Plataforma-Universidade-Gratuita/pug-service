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

@ApplicationScoped
public class AttendanceReadServiceImpl implements AttendanceReadService {

  private static final Logger LOG = Logger.getLogger(AttendanceReadServiceImpl.class);

  @Inject AttendanceQueries queries;

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

  @Override
  public List<AttendanceView> listViews() {
    return queries.listAllAttendances();
  }

  @Override
  public List<AttendanceView> listViewsByProjectId(UUID projectId) {
    if (projectId == null) return List.of();
    return queries.listByProjectId(projectId);
  }

  @Override
  public List<AttendanceView> listViewsByStudentId(UUID studentId) {
    if (studentId == null) return List.of();
    return queries.listByStudentId(studentId);
  }
}
