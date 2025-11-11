package com.pug.academic.service;

import com.pug.academic.domain.School;
import com.pug.academic.domain.SchoolRepository;
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

/** Service class for managing School entities. */
@ApplicationScoped
public class SchoolService {

  @Inject SchoolRepository repo;

  /**
   * Creates and saves a new School with the given name.
   *
   * @param name the name of the school
   * @return the saved School entity
   * @throws DuplicateResourceException if a school with the same name already exists
   */
  @Transactional
  public School save(String name) {
    Objects.requireNonNull(name, "name");
    String n = StringUtils.trim(name);
    if (repo.existsByName(n)) {
      throw new DuplicateResourceException(AcademicErrorCodes.SCHOOL_ALREADY_EXISTS);
    }
    return repo.persist(School.createNew(n));
  }

  /**
   * Saves multiple School entities.
   *
   * @param schools an iterable of School entities to be saved
   * @return a list of saved School entities
   * @throws DuplicateResourceException if any school name is duplicated in the input or already
   *     exists
   */
  @Transactional
  public List<School> saveAll(Iterable<School> schools) {
    List<School> list = toStream(schools).filter(Objects::nonNull).toList();
    if (list.isEmpty()) {
      return List.of();
    }

    Set<String> seen = new HashSet<>();
    for (School s : list) {
      String n = s.getName();
      if (!seen.add(n)) {
        throw new DuplicateResourceException(AcademicErrorCodes.SCHOOL_ALREADY_EXISTS);
      }
    }

    List<String> names = list.stream().map(School::getName).toList();
    if (repo.existsAnyByNameIn(names)) {
      throw new DuplicateResourceException(AcademicErrorCodes.SCHOOL_ALREADY_EXISTS);
    }

    return repo.persistAll(list);
  }

  /**
   * Updates an existing School entity with new data.
   *
   * @param id the UUID of the school to be updated
   * @param data the new School data
   * @return the updated School entity
   * @throws ResourceNotFoundException if the school with the given ID does not exist
   * @throws DuplicateResourceException if a school with the new name already exists
   */
  @Transactional
  public School update(UUID id, School data) {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(data, "data");

    School current =
        repo.findOptionalById(id)
            .orElseThrow(() -> new ResourceNotFoundException(AcademicErrorCodes.SCHOOL_NOT_FOUND));

    String newName = data.getName();
    String curName = current.getName();
    if (!newName.equals(curName) && repo.existsByName(newName)) {
      throw new DuplicateResourceException(AcademicErrorCodes.SCHOOL_ALREADY_EXISTS);
    }

    School updated = current.changeName(newName);
    repo.update(updated);

    return repo.findOptionalById(id)
        .orElseThrow(() -> new ResourceNotFoundException(AcademicErrorCodes.SCHOOL_NOT_FOUND));
  }

  /**
   * Deletes schools by their IDs.
   *
   * @param ids an iterable of UUIDs representing the IDs of the schools to be deleted
   * @return a map containing the count of deleted schools
   */
  @Transactional
  public Map<String, Long> deleteByIds(Iterable<UUID> ids) {
    return Map.of("schools", repo.deleteByIds(ids));
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
   * Retrieves multiple School entities by their IDs.
   *
   * @param ids an iterable of UUIDs representing the school IDs
   * @return a list of School entities corresponding to the given IDs
   */
  public List<School> getAllByIds(Iterable<UUID> ids) {
    Objects.requireNonNull(ids, "ids");
    return repo.listAllByIds(ids);
  }

  /**
   * Converts an Iterable to a Stream.
   *
   * @param it the iterable to convert.
   * @param <T> the type of elements in the iterable.
   * @return a stream of the iterable's elements.
   */
  private static <T> Stream<T> toStream(Iterable<T> it) {
    return it == null ? Stream.empty() : StreamSupport.stream(it.spliterator(), false);
  }
}
