package com.pug.academic.service;

import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.academic.infra.read.SchoolQueries;
import com.pug.academic.infra.read.dtos.SchoolView;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.CollectionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Read-only application service for Schools.
 */
@ApplicationScoped
public class SchoolReadService {

  @Inject
  SchoolQueries queries;

  /**
   * Retrieves a SchoolView by its unique identifier.
   *
   * @param id the UUID of the school
   * @return the SchoolView corresponding to the given id
   * @throws ResourceNotFoundException if no school is found with the given id
   */
  public SchoolView getViewById(UUID id) {
    return queries
            .findOptionalById(id)
            .orElseThrow(() -> new ResourceNotFoundException(AcademicErrorCodes.SCHOOL_NOT_FOUND, Map.of("id", id)));
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
