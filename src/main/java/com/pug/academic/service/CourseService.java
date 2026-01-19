package com.pug.academic.service;

import com.pug.academic.domain.Course;
import com.pug.academic.domain.CourseRepository;
import com.pug.academic.domain.Student;
import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.academic.service.dtos.CourseCreateBulkCommand;
import com.pug.shared.domain.enums.DeleteKeys;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.CollectionUtils;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/** Service class for managing Course entities. */
@ApplicationScoped
public class CourseService {

  @Inject CourseRepository repo;
  @Inject SchoolService schoolService;
  @Inject StudentService studentService;

  /**
   * Saves a new Course entity.
   *
   * @param name the name of the course
   * @param schoolName the name of the school
   * @return the saved Course entity
   * @throws DuplicateResourceException if a course with the same name already exists
   */
  @Transactional
  public Course save(String name, String schoolName) {
    String n = StringUtils.trim(name);
    if (existsByName(n)) {
      throw new DuplicateResourceException(
          AcademicErrorCodes.COURSE_ALREADY_EXISTS, Map.of("name", n));
    }
    var schoolId = schoolService.getByName(schoolName).getId();
    return repo.persist(Course.createNew(n, schoolId));
  }

  /**
   * Saves multiple new Course entities.
   *
   * @param cmds an iterable of CourseCreateBulkCommand
   * @return a list of saved Course entities
   * @throws DuplicateResourceException if any course with the same name already exists
   */
  @Transactional
  public List<Course> saveAll(Iterable<CourseCreateBulkCommand> cmds) {
    if (CollectionUtils.isEmpty(cmds)) {
      return List.of();
    }

    Set<String> names =
        CollectionUtils.toStream(cmds)
            .filter(Objects::nonNull)
            .map(CourseCreateBulkCommand::names)
            .flatMap(List::stream)
            .map(StringUtils::trim)
            .collect(Collectors.toSet());

    if (existsAnyByNameIn(names)) {
      throw new DuplicateResourceException(AcademicErrorCodes.COURSE_ALREADY_EXISTS);
    }

    Set<Course> toPersist =
        CollectionUtils.toStream(cmds)
            .map(
                cmd -> {
                  var schoolId = schoolService.getByName(cmd.schoolName()).getId();
                  return cmd.names().stream()
                      .map(StringUtils::trim)
                      .map(name -> Course.createNew(name, schoolId))
                      .collect(Collectors.toSet());
                })
            .flatMap(Set::stream)
            .collect(Collectors.toSet());
    return repo.persistAll(toPersist);
  }

  /**
   * Updates an existing Course entity.
   *
   * @param id the UUID of the course to update
   * @param name the new name of the course
   * @param schoolName the new name of the school
   * @return the updated Course entity
   * @throws ResourceNotFoundException if the course with the given ID does not exist
   * @throws DuplicateResourceException if a course with the new name already exists
   */
  @Transactional
  public Course update(UUID id, String name, String schoolName) {
    Course current = getById(id);

    String newName;
    if (StringUtils.isEmpty(name)) {
      newName = current.getName();
    } else {
      if (!name.equals(current.getName()) && existsByName(name)) {
        throw new DuplicateResourceException(
            AcademicErrorCodes.COURSE_ALREADY_EXISTS, Map.of("name", name));
      }
      newName = StringUtils.trim(name);
    }
    var schoolId =
        StringUtils.isEmpty(schoolName)
            ? current.getSchoolId()
            : schoolService.getByName(StringUtils.trim(schoolName)).getId();

    Course updated = current.changeName(newName).moveToSchool(schoolId);
    repo.update(updated);

    return getById(updated.getId());
  }

  /**
   * Deletes Course entities by their IDs.
   *
   * @param ids an iterable of UUIDs representing the course IDs to delete
   * @return a map containing the count of deleted entities for each DeleteKeys
   */
  @Transactional
  public Map<DeleteKeys, Long> deleteAll(Iterable<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return Map.of(
          DeleteKeys.COURSES, 0L,
          DeleteKeys.STUDENTS, 0L,
          DeleteKeys.ACCOUNTS, 0L,
          DeleteKeys.USERS, 0L);
    }
    var studentIds =
        CollectionUtils.toStream(ids)
            .map(studentService::listAllByCourseId)
            .flatMap(List::stream)
            .map(Student::getAccountId)
            .collect(Collectors.toSet());
    var deletedStudents = studentService.deleteAll(studentIds);
    return Map.of(
        DeleteKeys.COURSES, repo.deleteByIds(ids),
        DeleteKeys.STUDENTS, deletedStudents.getOrDefault(DeleteKeys.STUDENTS, 0L),
        DeleteKeys.ACCOUNTS, deletedStudents.getOrDefault(DeleteKeys.ACCOUNTS, 0L),
        DeleteKeys.USERS, deletedStudents.getOrDefault(DeleteKeys.USERS, 0L));
  }

  /**
   * Lists all Course entities.
   *
   * @return a list of all Course entities
   */
  public List<Course> listAll() {
    return repo.listAllCourses();
  }

  /**
   * Lists all Course entities associated with a specific school ID.
   *
   * @param schoolId the UUID of the school
   * @return a list of Course entities associated with the given school ID
   */
  public List<Course> listAllBySchoolId(UUID schoolId) {
    if (schoolId == null) {
      return List.of();
    }
    return repo.listAllBySchoolId(schoolId);
  }

  /**
   * Retrieves a Course entity by its ID.
   *
   * @param id the UUID of the course
   * @return the Course entity
   * @throws ResourceNotFoundException if the course with the given ID does not exist
   */
  public Course getById(UUID id) {
    return repo.findOptionalById(id)
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    AcademicErrorCodes.COURSE_NOT_FOUND, Map.of("id", id)));
  }

  /**
   * Retrieves a Course entity by its name.
   *
   * @param name the name of the course
   * @return the Course entity
   * @throws ResourceNotFoundException if the course with the given name does not exist
   */
  public Course getByName(String name) {
    String n = StringUtils.trim(name);
    return repo.findOptionalByName(n)
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    AcademicErrorCodes.COURSE_NOT_FOUND, Map.of("name", n)));
  }

  /**
   * Checks if a Course entity exists with the given name.
   *
   * @param name the name of the course to check
   * @return true if a Course entity exists with the given name, false otherwise
   */
  public boolean existsByName(String name) {
    if (StringUtils.isEmpty(name)) {
      return false;
    }
    return repo.existsByName(name);
  }

  /**
   * Checks if any Course entities exist with names in the provided iterable.
   *
   * @param names an iterable of course names to check
   * @return true if any Course entities exist with the given names, false otherwise
   */
  public boolean existsAnyByNameIn(Iterable<String> names) {
    if (CollectionUtils.isEmpty(names)) {
      return false;
    }
    return repo.existsAnyByNameIn(names);
  }
}
