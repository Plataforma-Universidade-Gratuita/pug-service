package com.pug.academic.service;

import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.academic.infra.read.SchoolQueries;
import com.pug.academic.infra.read.dtos.SchoolView;
import com.pug.shared.exceptions.ResourceNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/** Read-only application service for Schools. */
@ApplicationScoped
public class SchoolReadService {

  @Inject SchoolQueries queries;

  /**
   * Retrieves a SchoolView by its unique identifier.
   *
   * @param id the UUID of the school
   * @return the SchoolView corresponding to the given id
   * @throws ResourceNotFoundException if no school is found with the given id
   */
  public SchoolView getById(UUID id) {
    Objects.requireNonNull(id, "id");
    return queries
        .findOptionalById(id)
        .orElseThrow(() -> new ResourceNotFoundException(AcademicErrorCodes.SCHOOL_NOT_FOUND));
  }

  /**
   * Retrieves multiple SchoolView objects by their unique identifiers.
   *
   * @param ids an iterable of UUIDs representing the school ids
   * @return a list of SchoolView objects corresponding to the given ids
   */
  public List<SchoolView> getAllByIds(Iterable<UUID> ids) {
    Objects.requireNonNull(ids, "ids");
    return queries.listAllByIds(ids);
  }

  /**
   * Lists all schools.
   *
   * @return a list of all SchoolView objects
   */
  public List<SchoolView> listAll() {
    return queries.listAllSchools();
  }

  /**
   * Searches for schools by name.
   *
   * @param key the search key for the school name
   * @return a list of SchoolView objects matching the search key
   */
  public List<SchoolView> searchByName(String key) {
    return queries.searchByName(key);
  }
}
