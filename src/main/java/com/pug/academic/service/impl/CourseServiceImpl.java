package com.pug.academic.service.impl;

import com.pug.academic.domain.Course;
import com.pug.academic.domain.CourseRepository;
import com.pug.academic.service.CourseService;
import com.pug.academic.service.SchoolService;
import com.pug.academic.service.StudentService;
import com.pug.academic.service.dtos.CourseCreateCommand;
import com.pug.academic.service.dtos.CourseUpdateCommand;
import com.pug.academic.service.utils.CourseProcessor;
import com.pug.academic.service.utils.ExceptionHelper;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.UUID;

/**
 * Implementation of the {@link CourseService} command interface.
 * <p>
 * This application-scoped service orchestrates state mutations for academic courses.
 * It manages transaction boundaries, enforces cross-domain constraints (such as verifying
 * school existence via {@link SchoolService}), and relies on the {@link CourseProcessor}
 * to isolate domain initialization logic.
 */
@ApplicationScoped
public class CourseServiceImpl implements CourseService {

  private static final Logger LOG = Logger.getLogger(CourseServiceImpl.class);

  @Inject
  CourseRepository repo;

  @Inject
  SchoolService schoolService;

  /**
   * {@inheritDoc}
   */
  @Transactional
  @Override
  public Course save(CourseCreateCommand cmd) {
    LOG.debugf("Attempting to create Course: %s", cmd.name());
    schoolService.getById(cmd.schoolId());

    Course courseToPersist = CourseProcessor.processCreateInput(cmd.name(), cmd.schoolId());

    if (courseToPersist.hasFieldErrors()) {
      throw new AppValidationException(courseToPersist.getFieldErrors());
    }

    if (existsByName(courseToPersist.getName())) {
      LOG.warnf("Creation failed: Course with name %s already exists", courseToPersist.getName());
      throw ExceptionHelper.courseAlreadyExists();
    }

    Course savedCourse = repo.persist(courseToPersist);
    LOG.infof("Course created successfully. ID: %s", savedCourse.getId());
    return savedCourse;
  }

  /**
   * {@inheritDoc}
   */
  @Transactional
  @Override
  public Course update(UUID id, CourseUpdateCommand cmd) {
    LOG.debugf("Attempting to update Course ID: %s", id);
    Course current = getById(id);

    if (cmd.schoolId() != null && !cmd.schoolId().equals(current.getSchoolId())) {
      schoolService.getById(cmd.schoolId());
    }

    Course updatedCourse = CourseProcessor.processUpdateInput(current, cmd.name(), cmd.schoolId());

    if (updatedCourse.hasFieldErrors()) {
      throw new AppValidationException(updatedCourse.getFieldErrors());
    }

    if (!updatedCourse.getName().equals(current.getName())
            && existsByName(updatedCourse.getName())) {
      LOG.warnf(
              "Update failed: Course ID %s tried to use existing name %s", id, updatedCourse.getName());
      throw ExceptionHelper.courseAlreadyExists();
    }

    repo.update(updatedCourse);
    LOG.infof("Course updated successfully. ID: %s", id);
    return getById(id);
  }

  /**
   * {@inheritDoc}
   */
  @Transactional
  @Override
  public boolean delete(UUID id) {
    LOG.debugf("Attempting to delete Course ID: %s", id);
    if (id == null) {
      return false;
    }

    boolean deleted = repo.deleteById(id);
    if (deleted) {
      LOG.infof("Course deleted successfully. ID: %s", id);
    } else {
      LOG.debugf("Delete failed: Course ID %s not found (idempotent)", id);
    }

    return deleted;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Course getById(UUID id) {
    Course course =
            repo.findOptionalById(id)
                    .orElseThrow(
                            () -> {
                              LOG.debugf("Course lookup failed: ID %s not found", id);
                              return ExceptionHelper.courseNotFound();
                            });

    if (course.hasFieldErrors()) {
      LOG.errorf(
              "Data integrity error: Course with ID %s in DB violates domain rules. Problems: %s",
              id, course.getProblemsSummary());
      throw ExceptionHelper.courseNotFound();
    }
    return course;
  }

  /* --------------- INTERNAL HELPER METHODS --------------- */

  /**
   * Checks if a Course entity exists by its exact name.
   *
   * @param name the exact name of the course to check
   * @return {@code true} if a course with the given name exists, {@code false} otherwise
   */
  private boolean existsByName(String name) {
    if (StringUtils.isEmpty(name)) {
      return false;
    }
    return repo.existsByName(name);
  }
}