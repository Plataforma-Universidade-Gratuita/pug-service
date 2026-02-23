package com.pug.academic.service.impl;

import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.academic.infra.read.StudentQueries;
import com.pug.academic.infra.read.dtos.StudentView;
import com.pug.academic.service.StudentReadService;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.UUID;

/**
 * Service class for reading Student views.
 */
@ApplicationScoped
public class StudentReadServiceImpl implements StudentReadService {

  private static final Logger LOG = Logger.getLogger(StudentReadServiceImpl.class);

  @Inject
  StudentQueries queries;

  @Override
  public StudentView getViewByAccountId(UUID accountId) {
    return queries.findOptionalById(accountId)
            .orElseThrow(() -> {
              LOG.debugf("Student lookup failed: Account ID %s not found", accountId);
              return new ResourceNotFoundException(
                      AcademicErrorCodes.STUDENT_NOT_FOUND,
                      "accountId",
                      accountId.toString()
              );
            });
  }

  @Override
  public StudentView getViewByAcademicRegistration(String academicRegistration) {
    return queries.findOptionalByAcademicRegistration(academicRegistration)
            .orElseThrow(() -> {
              LOG.debugf("Student lookup failed: Registration %s not found", academicRegistration);
              return new ResourceNotFoundException(
                      AcademicErrorCodes.STUDENT_NOT_FOUND,
                      "academicRegistration",
                      academicRegistration
              );
            });
  }

  @Override
  public StudentView getViewByEmail(String email) {
    return queries.findOptionalByEmail(email)
            .orElseThrow(() -> {
              LOG.debugf("Student lookup failed: Email %s not found", email);
              return new ResourceNotFoundException(
                      AcademicErrorCodes.STUDENT_NOT_FOUND,
                      "email",
                      email
              );
            });
  }

  @Override
  public StudentView getViewByCpf(String cpf) {
    return queries.findOptionalByCpf(cpf)
            .orElseThrow(() -> {
              LOG.debugf("Student lookup failed: CPF %s not found", cpf);
              return new ResourceNotFoundException(
                      AcademicErrorCodes.STUDENT_NOT_FOUND,
                      "cpf",
                      cpf
              );
            });
  }

  @Override
  public List<StudentView> listViews() {
    return queries.listAllStudents();
  }

  @Override
  public List<StudentView> listViewsByCourseId(UUID courseId) {
    if (courseId == null) {
      return List.of();
    }
    return queries.listAllByCourseId(courseId);
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