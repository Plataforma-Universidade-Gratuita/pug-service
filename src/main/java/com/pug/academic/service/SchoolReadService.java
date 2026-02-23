package com.pug.academic.service;

import com.pug.academic.infra.read.dtos.SchoolView;

import java.util.List;
import java.util.UUID;

/**
 * Interface for reading School data.
 */
public interface SchoolReadService {

  /**
   * Retrieves a SchoolView by its unique identifier.
   *
   * @param id the UUID of the school
   * @return the SchoolView corresponding to the given id
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if no school is found with the
   *                                                             given id
   */
  SchoolView getViewById(UUID id);

  /**
   * Lists all schools.
   *
   * @return a list of all SchoolView objects
   */
  List<SchoolView> listAll();

  /**
   * Searches for schools by name.
   *
   * @param key the search key for the school name
   * @return a list of SchoolView objects matching the search key
   */
  List<SchoolView> searchByName(String key);
}
