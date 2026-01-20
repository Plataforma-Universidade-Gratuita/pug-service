package com.pug.academic.service;

import com.pug.academic.domain.Course;
import com.pug.academic.domain.School;
import com.pug.academic.domain.SchoolRepository;
import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.shared.domain.enums.DeleteKeys;
import com.pug.shared.utils.CollectionUtils;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/** Service class for managing School entities. */
@ApplicationScoped
public class SchoolService {

  @Inject SchoolRepository repo;
  @Inject CourseService courseService;

  /**
   * Saves a new School entity.
   *
   * @param name the name of the school
   * @return the saved School entity
   * @throws DuplicateResourceException if a school with the same name already exists
   */
  @Transactional
  public School save(String name) {
    String n = StringUtils.trim(name);
    if (existsByName(n)) {
      throw new DuplicateResourceException(
          AcademicErrorCodes.SCHOOL_ALREADY_EXISTS, Map.of("name", n));
    }
    return repo.persist(School.createNew(n));
  }

  /**
   * Saves multiple new School entities.
   *
   * @param names an iterable of school names
   * @return a list of saved School entities
   * @throws DuplicateResourceException if any school with the same name already exists
   */
  @Transactional
  public List<School> saveAll(Iterable<String> names) {
    if (CollectionUtils.isEmpty(names)) {
      return List.of();
    }
    Set<String> trimmedNames = new HashSet<>();
    CollectionUtils.toStream(names).forEach(trimmedNames::add);

    var seen = new HashSet<String>();
    for (var s : trimmedNames) {
      if (!seen.add(s)) {
        throw new DuplicateResourceException(AcademicErrorCodes.SCHOOL_ALREADY_EXISTS);
      }
    }

    if (existsAnyByNameIn(trimmedNames)) {
      throw new DuplicateResourceException(AcademicErrorCodes.SCHOOL_ALREADY_EXISTS);
    }

    return repo.persistAll(trimmedNames.stream().map(School::createNew).toList());
  }

  /**
   * Updates an existing School entity.
   *
   * @param id the UUID of the school to update
   * @param name the new name of the school
   * @return the updated School entity
   * @throws ResourceNotFoundException if the school with the given ID does not exist
   * @throws DuplicateResourceException if a school with the same name already exists
   */
  @Transactional
  public School update(UUID id, String name) {
    School current = getById(id);

    String newName;
    if (name != null) {
      if (!name.equals(current.getName()) && existsByName(name)) {
        throw new DuplicateResourceException(
            AcademicErrorCodes.SCHOOL_ALREADY_EXISTS, Map.of("name", name));
      }
      newName = name;
    } else {
      newName = current.getName();
    }

    repo.update(current.changeName(newName));
    return getById(id);
  }

  /**
   * Deletes School entities by their IDs.
   *
   * @param ids an iterable of UUIDs of the schools to delete
   * @return a map containing the number of deleted schools
   */
  @Transactional
  public Map<DeleteKeys, Long> deleteAll(Iterable<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return Map.of(DeleteKeys.SCHOOLS, 0L);
    }

    List<UUID> coursesIds =
        CollectionUtils.toStream(ids)
            .filter(Objects::nonNull)
            .map(id -> courseService.listAllBySchoolId(id))
            .flatMap(List::stream)
            .map(Course::getId)
            .toList();
    var courses = courseService.deleteAll(coursesIds);

    return Map.of(
        DeleteKeys.SCHOOLS, repo.deleteByIds(ids),
        DeleteKeys.COURSES, courses.getOrDefault(DeleteKeys.COURSES, 0L),
        DeleteKeys.STUDENTS, courses.getOrDefault(DeleteKeys.STUDENTS, 0L),
        DeleteKeys.ACCOUNTS, courses.getOrDefault(DeleteKeys.ACCOUNTS, 0L),
        DeleteKeys.USERS, courses.getOrDefault(DeleteKeys.USERS, 0L));
  }

  /**
   * Lists all School entities.
   *
   * @return a list of all School entities
   */
  public List<School> listAll() {
    return repo.listAllSchools();
  }

  /**
   * Retrieves a School entity by its ID.
   *
   * @param id the UUID of the school
   * @return the School entity
   * @throws ResourceNotFoundException if the school with the given ID does not exist
   */
  public School getById(UUID id) {
    return repo.findOptionalById(id)
        .orElseThrow(() -> new ResourceNotFoundException(AcademicErrorCodes.SCHOOL_NOT_FOUND));
  }

  /**
   * Retrieves a School entity by its name.
   *
   * @param name the name of the school
   * @return the School entity
   * @throws ResourceNotFoundException if the school with the given name does not exist
   */
  public School getByName(String name) {
    return repo.findOptionalByName(name)
        .orElseThrow(() -> new ResourceNotFoundException(AcademicErrorCodes.SCHOOL_NOT_FOUND));
  }

  /**
   * Checks if a School entity exists by its name.
   *
   * @param name the name of the school
   * @return true if a school with the given name exists, false otherwise
   */
  public boolean existsByName(String name) {
    return repo.existsByName(name);
  }

  /**
   * Checks if any School entities exist by their names.
   *
   * @param names an iterable of school names
   * @return true if any school with the given names exists, false otherwise
   */
  public boolean existsAnyByNameIn(Iterable<String> names) {
    if (CollectionUtils.isEmpty(names)) {
      return false;
    }
    return repo.existsAnyByNameIn(names);
  }
}
