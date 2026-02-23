package com.pug.academic.service.impl;

import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.academic.infra.read.CourseQueries;
import com.pug.academic.infra.read.dtos.CourseView;
import com.pug.academic.service.CourseReadService;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

/** Service for reading course information. */
@ApplicationScoped
public class CourseReadServiceImpl implements CourseReadService {

  private static final Logger LOG = Logger.getLogger(CourseReadServiceImpl.class);

  @Inject CourseQueries queries;

  @Override
  public CourseView getViewById(UUID id) {
    return queries
        .findOptionalById(id)
        .orElseThrow(
            () -> {
              LOG.debugf("Course lookup failed: ID %s not found", id);
              return new ResourceNotFoundException(
                  AcademicErrorCodes.COURSE_NOT_FOUND, "id", id.toString());
            });
  }

  @Override
  public List<CourseView> listViews() {
    return queries.listAllCourses();
  }

  @Override
  public List<CourseView> listViewsBySchoolId(UUID schoolId) {
    if (schoolId == null) {
      return List.of();
    }
    return queries.listAllBySchoolId(schoolId);
  }

  @Override
  public List<CourseView> searchByName(String query) {
    String key = StringUtils.fold(query);
    return queries.searchByName(key);
  }
}
