package com.pug.academic.service.impl;

import com.pug.academic.domain.Course;
import com.pug.academic.domain.ICourseRepository;
import com.pug.academic.domain.Student;
import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.academic.service.ICourseService;
import com.pug.academic.service.ISchoolService;
import com.pug.academic.service.IStudentService;
import com.pug.academic.service.dtos.CourseCreateCommand;
import com.pug.academic.service.dtos.CourseUpdateCommand;
import com.pug.shared.domain.enums.DeleteKeys;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ReferencedEntityException;
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
public class CourseService implements ICourseService {

  private static final Logger LOG = Logger.getLogger(CourseService.class);

  @Inject ICourseRepository repo;
  @Inject ISchoolService schoolService;
  @Inject IStudentService studentService;

  /**
   * Helper method to process DTO input and build Course domain object (or update existing),
   * collecting all validation problems.
   *
   * @param name The name string from DTO.
   * @param schoolId The school ID from DTO.
   * @param existingCourse Optional existing course for updates (null for creation).
   * @param problems List to collect AppValidationException.Problem instances.
   * @return The constructed or updated Course domain object if no problems, or null if problems
   *     occurred.
   */
  private Course processCourseInput(
      String name,
      UUID schoolId,
      Course existingCourse,
      List<AppValidationException.Problem> problems) {

    Course resultCourse = null;
    try {
      if (existingCourse == null) {
        resultCourse = Course.createNew(name, schoolId);
      } else {
        String effectiveName = (name != null) ? name : existingCourse.getName();
        UUID effectiveSchoolId = (schoolId != null) ? schoolId : existingCourse.getSchoolId();

        Course tempCourse = existingCourse;
        if (name != null && !effectiveName.equals(tempCourse.getName())) {
          tempCourse = tempCourse.changeName(effectiveName);
        }
        if (schoolId != null && !effectiveSchoolId.equals(tempCourse.getSchoolId())) {
          tempCourse = tempCourse.moveToSchool(effectiveSchoolId);
        }
        resultCourse = tempCourse;
      }
    } catch (AppValidationException e) {
      problems.addAll(e.getProblems());
    }
    return resultCourse;
  }

  @Transactional
  @Override
  public Course save(CourseCreateCommand cmd) {
    List<AppValidationException.Problem> problems = new ArrayList<>();

    schoolService.getById(cmd.schoolId());

    Course courseToPersist = processCourseInput(cmd.name(), cmd.schoolId(), null, problems);

    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
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

    List<AppValidationException.Problem> allCollectedProblems = new ArrayList<>();
    List<Course> coursesToPersist = new ArrayList<>();
    Set<String> processedNames = new HashSet<>();
    Set<UUID> uniqueSchoolIds = new HashSet<>();

    CollectionUtils.toStream(cmds).forEach(cmd -> uniqueSchoolIds.add(cmd.schoolId()));

    for (UUID schoolId : uniqueSchoolIds) {
      try {
        schoolService.getById(schoolId);
      } catch (ResourceNotFoundException e) {
        allCollectedProblems.add(
            new AppValidationException.Problem(
                AcademicErrorCodes.INVALID_SCHOOL_BLANK, "schoolId"));
      }
    }

    if (!allCollectedProblems.isEmpty()) {
      throw new AppValidationException(allCollectedProblems);
    }

    for (CourseCreateCommand cmd : cmds) {
      List<AppValidationException.Problem> currentCourseProblems = new ArrayList<>();
      Course course = processCourseInput(cmd.name(), cmd.schoolId(), null, currentCourseProblems);

      if (!currentCourseProblems.isEmpty()) {
        allCollectedProblems.addAll(currentCourseProblems);
      } else {
        String courseName = course.getName();
        if (!processedNames.add(courseName)) {
          allCollectedProblems.add(
              new AppValidationException.Problem(AcademicErrorCodes.COURSE_ALREADY_EXISTS, "name"));
        }
        coursesToPersist.add(course);
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

    List<AppValidationException.Problem> problems = new ArrayList<>();

    if (cmd.schoolId() != null && !cmd.schoolId().equals(current.getSchoolId())) {
      schoolService.getById(cmd.schoolId());
    }

    Course courseToUpdate = processCourseInput(cmd.name(), cmd.schoolId(), current, problems);

    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }

    if (cmd.name() != null && !cmd.name().equals(current.getName())) {
      if (existsByName(cmd.name())) {
        throw new DuplicateResourceException(
            AcademicErrorCodes.COURSE_ALREADY_EXISTS, Map.of("name", cmd.name()));
      }
    }

    repo.update(courseToUpdate);
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
      throw new ReferencedEntityException(AcademicErrorCodes.COURSE_STILL_REFERENCED);
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
    try {
      return repo.listAllCourses();
    } catch (AppValidationException e) {
      LOG.errorf(
          e,
          "Data integrity error: Corrupted Course entity found in DB. Problems: %s",
          e.getProblems().stream()
              .map(
                  p ->
                      p.code().getBundleKey()
                          + (p.fieldName() != null ? "(" + p.fieldName() + ")" : ""))
              .collect(Collectors.joining(", ")));
      throw new ResourceNotFoundException(AcademicErrorCodes.COURSE_NOT_FOUND);
    }
  }

  @Override
  public List<Course> listAllBySchoolId(UUID schoolId) {
    if (schoolId == null) {
      return List.of();
    }
    try {
      return repo.listAllBySchoolId(schoolId);
    } catch (AppValidationException e) {
      LOG.errorf(
          e,
          "Data integrity error: Corrupted Course entity found in DB while listing by school ID %s. Problems: %s",
          schoolId,
          e.getProblems().stream()
              .map(
                  p ->
                      p.code().getBundleKey()
                          + (p.fieldName() != null ? "(" + p.fieldName() + ")" : ""))
              .collect(Collectors.joining(", ")));
      throw new ResourceNotFoundException(AcademicErrorCodes.COURSE_NOT_FOUND);
    }
  }

  @Override
  public Course getById(UUID id) {
    try {
      return repo.findOptionalById(id)
          .orElseThrow(
              () ->
                  new ResourceNotFoundException(
                      AcademicErrorCodes.COURSE_NOT_FOUND, Map.of("id", id)));
    } catch (AppValidationException e) {
      LOG.errorf(
          e,
          "Data integrity error: Course with ID %s in DB violates domain rules. Problems: %s",
          id,
          e.getProblems().stream()
              .map(
                  p ->
                      p.code().getBundleKey()
                          + (p.fieldName() != null ? "(" + p.fieldName() + ")" : ""))
              .collect(Collectors.joining(", ")));
      throw new ResourceNotFoundException(AcademicErrorCodes.COURSE_NOT_FOUND, Map.of("id", id));
    }
  }

  @Override
  public Course getByName(String name) {
    String n = StringUtils.trim(name);
    try {
      return repo.findOptionalByName(n)
          .orElseThrow(
              () ->
                  new ResourceNotFoundException(
                      AcademicErrorCodes.COURSE_NOT_FOUND, Map.of("name", n)));
    } catch (AppValidationException e) {
      LOG.errorf(
          e,
          "Data integrity error: Course with name %s in DB violates domain rules. Problems: %s",
          name,
          e.getProblems().stream()
              .map(
                  p ->
                      p.code().getBundleKey()
                          + (p.fieldName() != null ? "(" + p.fieldName() + ")" : ""))
              .collect(Collectors.joining(", ")));
      throw new ResourceNotFoundException(AcademicErrorCodes.COURSE_NOT_FOUND, Map.of("name", n));
    }
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
