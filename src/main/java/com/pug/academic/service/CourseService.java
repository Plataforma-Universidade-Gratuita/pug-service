package com.pug.academic.service;

import com.pug.academic.domain.Course;
import com.pug.academic.domain.CourseRepository;
import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.text.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/** Service class for managing Course entities. */
@ApplicationScoped
public class CourseService {

  @Inject CourseRepository repo;

  /**
   * Saves a new Course with the given name and schoolId.
   *
   * @param name the name of the course
   * @param schoolId the ID of the associated school
   * @return the saved Course entity
   * @throws DuplicateResourceException if a course with the same name already exists
   */
  @Transactional
  public Course save(String name, UUID schoolId) {
    Objects.requireNonNull(name, "name");
    Objects.requireNonNull(schoolId, "schoolId");
    String n = StringUtils.trim(name);
    if (repo.existsByName(n)) {
      throw new DuplicateResourceException(AcademicErrorCodes.COURSE_ALREADY_EXISTS);
    }
    return repo.persist(Course.createNew(n, schoolId));
  }

  /**
   * Saves all given Course entities.
   *
   * @param courses the Course entities to save
   * @return the list of saved Course entities
   * @throws DuplicateResourceException if any course with the same name already exists
   */
  @Transactional
  public List<Course> saveAll(Iterable<Course> courses) {
    List<Course> list = toStream(courses).filter(Objects::nonNull).toList();
    if (list.isEmpty()) {
      return List.of();
    }

    Set<String> seen = new HashSet<>();
    for (Course c : list) {
      if (!seen.add(c.getName())) {
        throw new DuplicateResourceException(AcademicErrorCodes.COURSE_ALREADY_EXISTS);
      }
    }
    List<String> names = list.stream().map(Course::getName).toList();
    if (names.stream().anyMatch(repo::existsByName)) {
      throw new DuplicateResourceException(AcademicErrorCodes.COURSE_ALREADY_EXISTS);
    }
    return repo.persistAll(list);
  }

  /**
   * Deletes Course entities by their IDs.
   *
   * @param ids the IDs of the Course entities to delete
   * @return a map containing the count of deleted courses
   */
  @Transactional
  public Map<String, Long> deleteByIds(Iterable<UUID> ids) {
    return Map.of("courses", repo.deleteByIds(ids));
  }

  /**
   * Lists all Course entities.
   *
   * @return the list of all Course entities
   */
  public List<Course> listAll() {
    return repo.listAllCourses();
  }

  /**
   * Lists all Course entities by the given school ID.
   *
   * @param schoolId the ID of the associated school
   * @return the list of Course entities for the specified school
   */
  public List<Course> listAllBySchoolId(UUID schoolId) {
    Objects.requireNonNull(schoolId, "schoolId");
    return repo.listAllBySchoolId(schoolId);
  }

  /**
   * Retrieves a Course entity by its ID.
   *
   * @param id the ID of the Course entity
   * @return the Course entity
   * @throws ResourceNotFoundException if the Course entity is not found
   */
  public Course getById(UUID id) {
    return repo.findOptionalById(id)
        .orElseThrow(() -> new ResourceNotFoundException(AcademicErrorCodes.COURSE_NOT_FOUND));
  }

  /**
   * Lists all Course entities by their IDs.
   *
   * @param ids the IDs of the Course entities
   * @return the list of Course entities
   */
  public List<Course> listAllByIds(Iterable<UUID> ids) {
    if (ids == null || !ids.iterator().hasNext()) {
      return List.of();
    }
    return repo.listAllByIds(ids);
  }

  /**
   * Updates an existing Course entity.
   *
   * @param id the ID of the Course entity to update
   * @param data the Course entity containing updated data
   * @return the updated Course entity
   * @throws ResourceNotFoundException if the Course entity is not found
   * @throws DuplicateResourceException if a course with the same name already exists
   */
  @Transactional
  public Course update(UUID id, Course data) {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(data, "data");

    Course current =
        repo.findOptionalById(id)
            .orElseThrow(() -> new ResourceNotFoundException(AcademicErrorCodes.COURSE_NOT_FOUND));

    String newName = data.getName();
    String curName = current.getName();
    if (!newName.equals(curName) && repo.existsByName(newName)) {
      throw new DuplicateResourceException(AcademicErrorCodes.COURSE_ALREADY_EXISTS);
    }

    Course updated = current.changeName(data.getName()).moveToSchool(data.getSchoolId());
    repo.update(updated);

    return repo.findOptionalById(id)
        .orElseThrow(() -> new ResourceNotFoundException(AcademicErrorCodes.COURSE_NOT_FOUND));
  }

  /**
   * Converts an Iterable to a Stream.
   *
   * @param it the Iterable to convert.
   * @param <T> the type of elements in the Iterable.
   * @return the resulting Stream.
   */
  private static <T> Stream<T> toStream(Iterable<T> it) {
    return it == null ? Stream.empty() : StreamSupport.stream(it.spliterator(), false);
  }
}
