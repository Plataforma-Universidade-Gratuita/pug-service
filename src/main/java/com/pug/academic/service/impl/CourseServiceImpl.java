package com.pug.academic.service.impl;

import com.pug.academic.domain.Course;
import com.pug.academic.domain.CourseRepository;
import com.pug.academic.domain.Student;
import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.academic.service.CourseService;
import com.pug.academic.service.SchoolService;
import com.pug.academic.service.StudentService;
import com.pug.academic.service.dtos.CourseCreateCommand;
import com.pug.academic.service.dtos.CourseUpdateCommand;
import com.pug.academic.service.utils.CourseProcessor;
import com.pug.shared.domain.enums.DeleteKeys;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.DataIntegrityException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.CollectionUtils;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.jboss.logging.Logger;

/** Service class for managing Course entities. */
@ApplicationScoped
public class CourseServiceImpl implements CourseService {

  private static final Logger LOG = Logger.getLogger(CourseServiceImpl.class);

  @Inject CourseRepository repo;
  @Inject SchoolService schoolService;
  @Inject StudentService studentService;

  @Transactional
  @Override
  public Course save(CourseCreateCommand cmd) {
    // Validate school existence first (fail-fast)
    schoolService.getById(cmd.schoolId());

    Course courseToPersist = CourseProcessor.processCreateInput(cmd.name(), cmd.schoolId());

    if (courseToPersist.hasErrors()) {
      throw new AppValidationException(courseToPersist.getProblems());
    }

    if (existsByName(courseToPersist.getName())) {
      throw new DuplicateResourceException(
          AcademicErrorCodes.COURSE_ALREADY_EXISTS, Map.of("name", courseToPersist.getName()));
    }
    return repo.persist(courseToPersist);
  }

  @Transactional
  @Override
  public List<Course> saveAll(Iterable<CourseCreateCommand> cmds) {
    if (CollectionUtils.isEmpty(cmds)) {
      return List.of();
    }

    List<Problem> allCollectedProblems = new ArrayList<>();
    List<Course> coursesToPersist = new ArrayList<>();
    Set<String> processedNames = new HashSet<>();
    Set<UUID> uniqueSchoolIds = new HashSet<>();

    CollectionUtils.toStream(cmds).forEach(cmd -> uniqueSchoolIds.add(cmd.schoolId()));

    for (UUID schoolId : uniqueSchoolIds) {
      try {
        schoolService.getById(schoolId);
      } catch (ResourceNotFoundException e) {
        allCollectedProblems.add(
            new Problem(
                AcademicErrorCodes
                    .INVALID_SCHOOL_BLANK)); // Using generic key as field name isn't directly
        // mappable here easily without context
      }
    }

    if (!allCollectedProblems.isEmpty()) {
      throw new AppValidationException(allCollectedProblems);
    }

    for (CourseCreateCommand cmd : cmds) {
      Course course = CourseProcessor.processCreateInput(cmd.name(), cmd.schoolId());

      if (course.hasErrors()) {
        allCollectedProblems.addAll(course.getProblems());
      } else {
        String courseName = course.getName();
        if (!processedNames.add(courseName)) {
          allCollectedProblems.add(
              new Problem(AcademicErrorCodes.COURSE_ALREADY_EXISTS));
        } else {
          coursesToPersist.add(course);
        }
      }
    }

    if (!allCollectedProblems.isEmpty()) {
      throw new AppValidationException(allCollectedProblems);
    }

    List<String> namesToPersist = coursesToPersist.stream().map(Course::getName).toList();

    if (repo.existsAnyByNameIn(namesToPersist)) {
      throw new DuplicateResourceException(AcademicErrorCodes.COURSE_ALREADY_EXISTS);
    }

    return repo.persistAll(coursesToPersist);
  }

  @Transactional
  @Override
  public Course update(UUID id, CourseUpdateCommand cmd) {
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
      throw new DuplicateResourceException(
          AcademicErrorCodes.COURSE_ALREADY_EXISTS, Map.of("name", updatedCourse.getName()));
    }

    repo.update(updatedCourse);
    return getById(id);
  }

  @Transactional
  @Override
  public Map<DeleteKeys, Long> deleteAll(Iterable<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return Map.of(
          DeleteKeys.COURSES, 0L,
          DeleteKeys.STUDENTS, 0L,
          DeleteKeys.ACCOUNTS, 0L,
          DeleteKeys.USERS, 0L);
    }

    if (studentService.existsAnyByCourseIdIn(ids)) {
      throw new DataIntegrityException(AcademicErrorCodes.COURSE_STILL_REFERENCED);
    }

    Set<UUID> studentAccountIdsToDelete = new HashSet<>();
    for (UUID courseId : ids) {
      studentAccountIdsToDelete.addAll(
          studentService.listAllByCourseId(courseId).stream()
              .map(Student::getAccountId)
              .collect(Collectors.toSet()));
    }

    Map<DeleteKeys, Long> deletedStudentsAndDependents =
        studentService.deleteAll(studentAccountIdsToDelete);

    long coursesDeleted = repo.deleteByIds(ids);

    return Map.of(
        DeleteKeys.COURSES, coursesDeleted,
        DeleteKeys.STUDENTS, deletedStudentsAndDependents.getOrDefault(DeleteKeys.STUDENTS, 0L),
        DeleteKeys.ACCOUNTS, deletedStudentsAndDependents.getOrDefault(DeleteKeys.ACCOUNTS, 0L),
        DeleteKeys.USERS, deletedStudentsAndDependents.getOrDefault(DeleteKeys.USERS, 0L));
  }

  @Override
  public List<Course> listAll() {
    List<Course> courses = repo.listAllCourses();
    for (Course c : courses) {
      if (c.hasErrors()) {
        LOG.errorf(
            "Data integrity error: Corrupted Course entity found in DB. Problems: %s",
            c.getProblemsSummary());
        throw new ResourceNotFoundException(AcademicErrorCodes.COURSE_NOT_FOUND);
      }
    }
    return courses;
  }

  @Override
  public List<Course> listAllBySchoolId(UUID schoolId) {
    if (schoolId == null) {
      return List.of();
    }
    List<Course> courses = repo.listAllBySchoolId(schoolId);
    for (Course c : courses) {
      if (c.hasErrors()) {
        LOG.errorf(
            "Data integrity error: "
                + "Corrupted Course entity found in DB while listing by school ID %s. Problems: %s",
            schoolId, c.getProblemsSummary());
        throw new ResourceNotFoundException(AcademicErrorCodes.COURSE_NOT_FOUND);
      }
    }
    return courses;
  }

  @Override
  public Course getById(UUID id) {
    Course course =
        repo.findOptionalById(id)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        AcademicErrorCodes.COURSE_NOT_FOUND, Map.of("id", id)));

    if (course.hasErrors()) {
      LOG.errorf(
          "Data integrity error: Course with ID %s in DB violates domain rules. Problems: %s",
          id, course.getProblemsSummary());
      throw new ResourceNotFoundException(AcademicErrorCodes.COURSE_NOT_FOUND, Map.of("id", id));
    }
    return course;
  }

  @Override
  public Course getByName(String name) {
    String n = StringUtils.trim(name);
    Course course =
        repo.findOptionalByName(n)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        AcademicErrorCodes.COURSE_NOT_FOUND, Map.of("name", n)));

    if (course.hasErrors()) {
      LOG.errorf(
          "Data integrity error: Course with name %s in DB violates domain rules. Problems: %s",
          n, course.getProblemsSummary());
      throw new ResourceNotFoundException(AcademicErrorCodes.COURSE_NOT_FOUND, Map.of("name", n));
    }
    return course;
  }

  @Override
  public boolean existsByName(String name) {
    if (StringUtils.isEmpty(name)) {
      return false;
    }
    return repo.existsByName(name);
  }

  @Override
  public boolean existsAnyByNameIn(Iterable<String> names) {
    if (CollectionUtils.isEmpty(names)) {
      return false;
    }
    return repo.existsAnyByNameIn(names);
  }
}
