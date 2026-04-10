package com.pug.academic.service.impl;

import com.pug.academic.domain.School;
import com.pug.academic.domain.SchoolRepository;
import com.pug.academic.service.CourseService;
import com.pug.academic.service.SchoolService;
import com.pug.academic.service.dtos.SchoolCreateCommand;
import com.pug.academic.service.dtos.SchoolUpdateCommand;
import com.pug.academic.service.utils.ExceptionHelper;
import com.pug.academic.service.utils.SchoolProcessor;
import com.pug.project.service.ProjectBySchoolService;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.infra.audit.AuditPublisher;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.jboss.logging.Logger;

/**
 * Implementation of the {@link SchoolService} command interface.
 *
 * <p>This application-scoped service orchestrates state mutations for academic schools. It invokes
 * pure domain logic via {@link SchoolProcessor}, enforces uniqueness rules, and coordinates
 * transaction boundaries with the {@link SchoolRepository}.
 */
@ApplicationScoped
public class SchoolServiceImpl implements SchoolService {

  private static final Logger LOG = Logger.getLogger(SchoolServiceImpl.class);

  @Inject AuditPublisher auditPublisher;

  @Inject SchoolRepository repo;

  @Inject CourseService courseService;

  @Inject ProjectBySchoolService projectBySchoolService;

  /** {@inheritDoc} */
  @Transactional
  @Override
  public boolean delete(UUID id) {
    LOG.debugf("Attempting to delete School ID: %s", id);
    if (id == null) {
      return false;
    }

    if (courseService.existsAnyBySchoolId(id)) {
      LOG.warnf("Delete failed: School ID %s has active courses", id);
      throw ExceptionHelper.schoolHasCourses();
    }

    boolean deleted = repo.deleteById(id);
    if (deleted) {
      LOG.infof("School deleted successfully. ID: %s", id);
      projectBySchoolService.deleteAllBySchoolId(id);
      auditPublisher.fireDelete(School.class.getName(), id);
    } else {
      LOG.debugf("Delete failed: School ID %s not found (idempotent)", id);
    }

    return deleted;
  }

  /** {@inheritDoc} */
  @Override
  public School getById(UUID id) {
    School school =
        repo.findOptionalById(id)
            .orElseThrow(
                () -> {
                  LOG.debugf("School lookup failed: ID %s not found", id);
                  return ExceptionHelper.schoolNotFound();
                });

    if (school.hasFieldErrors()) {
      LOG.errorf(
          "DATA CORRUPTION DETECTED: School %s violates domain rules: %s",
          id, school.getProblemsSummary());
      throw ExceptionHelper.schoolNotFound();
    }
    return school;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public School save(SchoolCreateCommand cmd) {
    LOG.debugf("Attempting to create School: %s", cmd.name());
    School schoolToPersist = SchoolProcessor.processCreateInput(cmd.name());

    if (schoolToPersist.hasFieldErrors()) {
      throw new AppValidationException(schoolToPersist.getFieldErrors());
    }

    if (existsByName(schoolToPersist.getName())) {
      LOG.warnf("Creation failed: School with name %s already exists", schoolToPersist.getName());
      throw ExceptionHelper.schoolAlreadyExists();
    }

    School savedSchool = repo.persist(schoolToPersist);
    LOG.infof("School created successfully. ID: %s", savedSchool.getId());

    auditPublisher.fireCreate(School.class.getName(), savedSchool.getId());
    return savedSchool;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public School update(UUID id, SchoolUpdateCommand cmd) {
    LOG.debugf("Attempting to update School ID: %s", id);
    School current = getById(id);
    School updatedSchool = SchoolProcessor.processUpdateInput(current, cmd.name());

    if (updatedSchool.hasFieldErrors()) {
      throw new AppValidationException(updatedSchool.getFieldErrors());
    }

    if (!updatedSchool.getName().equals(current.getName())
        && existsByName(updatedSchool.getName())) {
      LOG.warnf(
          "Update failed: School ID %s tried to use existing name %s", id, updatedSchool.getName());
      throw ExceptionHelper.schoolAlreadyExists();
    }

    repo.update(updatedSchool);
    LOG.infof("School updated successfully. ID: %s", id);

    auditPublisher.fireUpdate(School.class.getName(), id, current, updatedSchool);
    return getById(id);
  }

  /* --------------- INTERNAL HELPER METHODS --------------- */

  /**
   * Checks if a School entity exists by its exact name.
   *
   * @param name the exact name of the school to check
   * @return {@code true} if a school with the given name exists, {@code false} otherwise
   */
  private boolean existsByName(String name) {
    if (StringUtils.isEmpty(name)) {
      return false;
    }
    return repo.existsByName(name);
  }
}
