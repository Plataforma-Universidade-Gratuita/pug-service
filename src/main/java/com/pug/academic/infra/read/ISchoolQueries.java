package com.pug.academic.infra.read;

import com.pug.academic.infra.read.dtos.SchoolView;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Queries related to schools. */
public interface ISchoolQueries {

  /**
   * Finds a school by its ID.
   *
   * @param id the UUID of the school to find
   * @return an Optional containing the found SchoolView or empty if not found
   */
  Optional<SchoolView> findOptionalById(UUID id);

  /**
   * Finds a school by its name.
   *
   * @param name the name of the school to find.
   * @return an Optional containing the found SchoolView, or empty if not found.
   */
  Optional<SchoolView> findOptionalByName(String name);

  /**
   * Lists all schools.
   *
   * @return a list of all SchoolView
   */
  List<SchoolView> listAllSchools();

  /**
   * Searches schools by name.
   *
   * @param key the search key for the school name
   * @return a list of SchoolView matching the search key
   */
  List<SchoolView> searchByName(String key);
}
