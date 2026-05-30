package br.org.catolicasc.pug.academic.service.impl;

import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.academic.domain.CourseRepository;
import br.org.catolicasc.pug.academic.service.AreasOfExpertiseService;
import br.org.catolicasc.pug.academic.service.CoursesService;
import br.org.catolicasc.pug.academic.service.FormerStudentsService;
import br.org.catolicasc.pug.academic.service.dtos.courses.CourseCreateCommand;
import br.org.catolicasc.pug.academic.service.dtos.courses.CourseUpdateCommand;
import br.org.catolicasc.pug.academic.service.utils.CourseProcessor;
import br.org.catolicasc.pug.academic.service.utils.ExceptionHelper;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import br.org.catolicasc.pug.shared.infra.audit.AuditPublisher;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.jboss.logging.Logger;

/**
 * Implementation of the {@link CoursesService} command interface.
 *
 * <p>This application-scoped service orchestrates state mutations for academic courses. It manages
 * transaction boundaries, enforces cross-domain constraints, and relies on the {@link
 * CourseProcessor} to isolate domain initialization logic.
 */
@ApplicationScoped
public class CoursesServiceImpl implements CoursesService {

  private static final Logger LOG = Logger.getLogger(CoursesServiceImpl.class);

  @Inject AuditPublisher auditPublisher;

  @Inject CourseRepository repo;

  @Inject AreasOfExpertiseService areasOfExpertiseService;

  @Inject FormerStudentsService formerStudentsService;

  /** {@inheritDoc} */
  @Transactional
  @Override
  public boolean delete(UUID id) {
    LOG.debugf("Attempting to delete Course ID: %s", id);
    if (id == null) {
      return false;
    }

    if (formerStudentsService.existsAnyByCourseId(id)) {
      LOG.warnf("Delete failed: Course ID %s has active former students", id);
      throw ExceptionHelper.courseHasStudents();
    }

    boolean deleted = repo.deleteById(id);
    if (deleted) {
      LOG.infof("Course deleted successfully. ID: %s", id);
      auditPublisher.fireDelete(Course.class.getName(), id);
    } else {
      LOG.debugf("Delete failed: Course ID %s not found (idempotent)", id);
    }

    return deleted;
  }

  /** {@inheritDoc} */
  @Override
  public boolean existsAnyByAreaOfExpertiseId(UUID areaOfExpertiseId) {
    return repo.existsByAreaOfExpertiseId(areaOfExpertiseId);
  }

  /** {@inheritDoc} */
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

  /** {@inheritDoc} */
  @Transactional
  @Override
  public Course save(CourseCreateCommand cmd) {
    LOG.debugf("Attempting to create Course: %s", cmd.name());
    areasOfExpertiseService.getById(cmd.areaOfExpertiseId());

    Course courseToPersist =
        CourseProcessor.processCreateInput(cmd.name(), cmd.areaOfExpertiseId());

    if (courseToPersist.hasFieldErrors()) {
      throw new AppValidationException(courseToPersist.getFieldErrors());
    }

    if (existsByName(courseToPersist.getName())) {
      LOG.warnf("Creation failed: Course with name %s already exists", courseToPersist.getName());
      throw ExceptionHelper.courseAlreadyExists();
    }

    Course savedCourse = repo.persist(courseToPersist);
    LOG.infof("Course created successfully. ID: %s", savedCourse.getId());

    auditPublisher.fireCreate(Course.class.getName(), savedCourse.getId());
    return savedCourse;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public Course update(UUID id, CourseUpdateCommand cmd) {
    LOG.debugf("Attempting to update Course ID: %s", id);
    Course current = getById(id);

    if (cmd.areaOfExpertiseId() != null
        && !cmd.areaOfExpertiseId().equals(current.getAreaOfExpertiseId())) {
      areasOfExpertiseService.getById(cmd.areaOfExpertiseId());
    }

    Course updatedCourse =
        CourseProcessor.processUpdateInput(current, cmd.name(), cmd.areaOfExpertiseId());

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

    auditPublisher.fireUpdate(Course.class.getName(), id, current, updatedCourse);
    return getById(id);
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
