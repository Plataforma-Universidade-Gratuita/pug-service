package com.pug.academic.service.impl;

import com.pug.academic.domain.Course;
import com.pug.academic.domain.CourseRepository;
import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.academic.service.CourseService;
import com.pug.academic.service.SchoolService;
import com.pug.academic.service.StudentService;
import com.pug.academic.service.dtos.CourseCreateCommand;
import com.pug.academic.service.dtos.CourseUpdateCommand;
import com.pug.academic.service.utils.CourseProcessor;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.UUID;

/**
 * Service class for managing Course entities.
 */
@ApplicationScoped
public class CourseServiceImpl implements CourseService {

  private static final Logger LOG = Logger.getLogger(CourseServiceImpl.class);

  @Inject
  CourseRepository repo;
  @Inject
  SchoolService schoolService;
  @Inject
  StudentService studentService;

  @Transactional
  @Override
  public Course save(CourseCreateCommand cmd) {
    LOG.debugf("Attempting to create Course: %s", cmd.name());
    schoolService.getById(cmd.schoolId());
    Course courseToPersist = CourseProcessor.processCreateInput(cmd.name(), cmd.schoolId());

    if (courseToPersist.hasErrors()) {
      throw new AppValidationException(courseToPersist.getProblems());
    }

    if (existsByName(courseToPersist.getName())) {
      LOG.warnf("Creation failed: Course with name %s already exists", courseToPersist.getName());
      throw new DuplicateResourceException(
              AcademicErrorCodes.COURSE_ALREADY_EXISTS,
              "name",
              courseToPersist.getName()
      );
    }

    Course savedCourse = repo.persist(courseToPersist);
    LOG.infof("Course created successfully. ID: %s", savedCourse.getId());
    return savedCourse;
  }

  @Transactional
  @Override
  public Course update(UUID id, CourseUpdateCommand cmd) {
    LOG.debugf("Attempting to update Course ID: %s", id);
    Course current = getById(id);

    if (cmd.schoolId() != null && !cmd.schoolId().equals(current.getSchoolId())) {
      schoolService.getById(cmd.schoolId());
    }

    Course updatedCourse = CourseProcessor.processUpdateInput(current, cmd.name(), cmd.schoolId());

    if (updatedCourse.hasErrors()) {
      throw new AppValidationException(updatedCourse.getProblems());
    }

    if (!updatedCourse.getName().equals(current.getName())
            && existsByName(updatedCourse.getName())) {
      LOG.warnf("Update failed: Course ID %s tried to use existing name %s", id, updatedCourse.getName());
      throw new DuplicateResourceException(
              AcademicErrorCodes.COURSE_ALREADY_EXISTS,
              "name",
              updatedCourse.getName()
      );
    }

    repo.update(updatedCourse);
    LOG.infof("Course updated successfully. ID: %s", id);
    return getById(id);
  }

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

  @Override
  public List<Course> listAll() {
    LOG.debug("Listing all courses");
    List<Course> courses = repo.listAllCourses();

    return courses.stream()
            .filter(course -> {
              if (course.hasErrors()) {
                LOG.errorf("DATA CORRUPTION DETECTED: Course %s violates domain rules: %s",
                        course.getId(), course.getProblemsSummary());
                return false;
              }
              return true;
            })
            .toList();
  }

  @Override
  public Course getById(UUID id) {
    Course course = repo.findOptionalById(id)
            .orElseThrow(() -> {
              LOG.debugf("Course lookup failed: ID %s not found", id);
              return new ResourceNotFoundException(
                      AcademicErrorCodes.COURSE_NOT_FOUND,
                      "id",
                      id.toString()
              );
            });

    if (course.hasErrors()) {
      LOG.errorf(
              "Data integrity error: Course with ID %s in DB violates domain rules. Problems: %s",
              id, course.getProblemsSummary());
      throw new ResourceNotFoundException(
              AcademicErrorCodes.COURSE_NOT_FOUND,
              "id",
              id.toString()
      );
    }
    return course;
  }

  /* --------------- INTERNAL HELPER METHODS --------------- */

  /**
   * Checks if a Course with the given name already exists.
   *
   * @param name the name to check for existence.
   * @return true if a Course with the given name exists, false otherwise.
   */
  private boolean existsByName(String name) {
    if (StringUtils.isEmpty(name)) {
      return false;
    }
    return repo.existsByName(name);
  }
}