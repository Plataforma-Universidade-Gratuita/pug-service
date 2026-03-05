package com.pug.academic.service.impl;

import com.pug.academic.infra.read.StudentQueries;
import com.pug.academic.infra.read.dtos.StudentView;
import com.pug.academic.service.StudentReadService;
import com.pug.academic.service.utils.ExceptionHelper;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

/**
 * Implementation of the {@link StudentReadService}.
 *
 * <p>This application-scoped bean delegates read-only operations to the underlying {@link
 * StudentQueries} infrastructure component. It handles basic input validation and translates "not
 * found" states into standardized domain exceptions.
 */
@ApplicationScoped
public class StudentReadServiceImpl implements StudentReadService {

  private static final Logger LOG = Logger.getLogger(StudentReadServiceImpl.class);

  @Inject StudentQueries queries;

  /** {@inheritDoc} */
  @Override
  public StudentView getViewByAccountId(UUID accountId) {
    return queries
        .findOptionalById(accountId)
        .orElseThrow(
            () -> {
              LOG.debugf("Student lookup failed: Account ID %s not found", accountId);
              return ExceptionHelper.studentNotFound();
            });
  }

  /** {@inheritDoc} */
  @Override
  public StudentView getViewByAcademicRegistration(String academicRegistration) {
    return queries
        .findOptionalByAcademicRegistration(academicRegistration)
        .orElseThrow(
            () -> {
              LOG.debugf("Student lookup failed: Registration %s not found", academicRegistration);
              return ExceptionHelper.studentNotFound();
            });
  }

  /** {@inheritDoc} */
  @Override
  public StudentView getViewByEmail(String email) {
    return queries
        .findOptionalByEmail(email)
        .orElseThrow(
            () -> {
              LOG.debugf("Student lookup failed: Email %s not found", email);
              return ExceptionHelper.studentNotFound();
            });
  }

  /** {@inheritDoc} */
  @Override
  public StudentView getViewByCpf(String cpf) {
    return queries
        .findOptionalByCpf(cpf)
        .orElseThrow(
            () -> {
              LOG.debugf("Student lookup failed: CPF %s not found", cpf);
              return ExceptionHelper.studentNotFound();
            });
  }

  /** {@inheritDoc} */
  @Override
  public List<StudentView> listViews() {
    return queries.listAllStudents();
  }

  /** {@inheritDoc} */
  @Override
  public List<StudentView> listViewsByCourseId(UUID courseId) {
    if (courseId == null) {
      return List.of();
    }
    return queries.listAllByCourseId(courseId);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Prior to execution, the input query is "folded" (lowercased and accents removed via {@link
   * StringUtils#fold(String)}) to ensure maximum compatibility with the underlying search indexing
   * rules.
   */
  @Override
  public List<StudentView> searchByName(String query) {
    if (StringUtils.isEmpty(query)) {
      return List.of();
    }
    String key = StringUtils.fold(query);
    return queries.searchByName(key);
  }
}
