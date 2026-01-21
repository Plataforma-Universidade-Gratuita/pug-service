package com.pug.academic.service.impl;

import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.academic.infra.read.ICourseQueries;
import com.pug.academic.infra.read.dtos.CourseView;
import com.pug.academic.service.ICourseReadService;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Service for reading course information. */
@ApplicationScoped
public class CourseReadService implements ICourseReadService {

  @Inject ICourseQueries queries;

  @Override
  public CourseView getViewById(UUID id) {
    return queries
        .findOptionalById(id)
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    AcademicErrorCodes.COURSE_NOT_FOUND, Map.of("id", id)));
  }

  @Override
  public CourseView getByName(String name) {
    if (StringUtils.isEmpty(name)) {
      throw new ResourceNotFoundException(
          AcademicErrorCodes.COURSE_NOT_FOUND, Map.of("name", name));
    }
    return queries
        .findOptionalByName(name)
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    AcademicErrorCodes.COURSE_NOT_FOUND, Map.of("name", name)));
  }

  @Override
  public List<CourseView> listViews() {
    return queries.listAllCourses();
  }

  @Override
  public List<CourseView> listViewsBySchoolId(UUID schoolId) {
    return queries.listAllBySchoolId(schoolId);
  }

  @Override
  public List<CourseView> searchByName(String query) {
    String key = StringUtils.fold(query);
    return queries.searchByName(key);
  }
}
