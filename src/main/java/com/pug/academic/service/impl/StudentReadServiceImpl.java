package com.pug.academic.service.impl;

import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.academic.infra.read.StudentQueries;
import com.pug.academic.infra.read.dtos.StudentView;
import com.pug.academic.service.StudentReadService;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/** Service class for reading Student views. */
@ApplicationScoped
public class StudentReadServiceImpl implements StudentReadService {

  @Inject StudentQueries queries;

  @Override
  public StudentView getView(UUID accountId) {
    Objects.requireNonNull(accountId, "accountId");
    return queries
        .findOptionalById(accountId)
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    AcademicErrorCodes.STUDENT_NOT_FOUND, Map.of("accountId", accountId)));
  }

  @Override
  public StudentView getViewByAcademicRegistration(String academicRegistration) {
    if (StringUtils.isEmpty(academicRegistration)) {
      throw new ResourceNotFoundException(
          AcademicErrorCodes.STUDENT_NOT_FOUND,
          Map.of("academicRegistration", academicRegistration));
    }
    return queries
        .findOptionalByAcademicRegistration(academicRegistration)
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    AcademicErrorCodes.STUDENT_NOT_FOUND,
                    Map.of("academicRegistration", academicRegistration)));
  }

  @Override
  public List<StudentView> listViews() {
    try {
      return queries.listAllStudents();
    } catch (Exception e) {
      throw new ResourceNotFoundException(
          AcademicErrorCodes.STUDENT_NOT_FOUND, Map.of("detail", "Error listing all students."));
    }
  }

  @Override
  public List<StudentView> listViewsByCourseId(UUID courseId) {
    Objects.requireNonNull(courseId, "courseId");
    try {
      return queries.listAllByCourseId(courseId);
    } catch (Exception e) {
      throw new ResourceNotFoundException(
          AcademicErrorCodes.STUDENT_NOT_FOUND,
          Map.of("courseId", courseId, "detail", "Error listing students by course."));
    }
  }

  @Override
  public List<StudentView> listViewsByIds(Iterable<UUID> accountIds) {
    Objects.requireNonNull(accountIds, "accountIds");
    try {
      return queries.listAllByIds(accountIds);
    } catch (Exception e) {
      throw new ResourceNotFoundException(
          AcademicErrorCodes.STUDENT_NOT_FOUND, Map.of("detail", "Error listing students by IDs."));
    }
  }

  @Override
  public List<StudentView> searchByName(String query) {
    if (StringUtils.isEmpty(query)) {
      return List.of();
    }
    String key = StringUtils.fold(query);
    return queries.searchByName(key);
  }
}
