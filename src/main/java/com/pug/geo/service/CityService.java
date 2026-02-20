package com.pug.geo.service;

import com.pug.geo.domain.City;
import com.pug.geo.domain.vos.IbgeCode;
import com.pug.geo.service.dtos.CityCreateCommand;
import com.pug.geo.service.dtos.CityUpdateCommand;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ResourceNotFoundException;

import java.util.UUID;

/**
 * Interface for managing City entities.
 */
public interface CityService {

  /**
   * Factory-style save.
   *
   * @param cmd the command with city data.
   * @return the saved city.
   * @throws DuplicateResourceException if a city with the same IBGE code already exists.
   * @throws AppValidationException     if input validation fails (e.g., blank name, invalid IBGE code).
   */
  City save(CityCreateCommand cmd);

  /**
   * Update name and/or IBGE code.
   *
   * @param id  the city ID.
   * @param cmd the command with updated city data.
   * @return the updated city.
   * @throws ResourceNotFoundException  if the city does not exist (or data is corrupted in DB).
   * @throws DuplicateResourceException if a city with the same IBGE code already exists.
   * @throws AppValidationException     if input validation fails.
   */
  City update(UUID id, CityUpdateCommand cmd);

  /**
   * Delete a city by its ID.
   *
   * @param id the city ID.
   * @return true if the city was successfully deleted, false if the city does not exist or cannot be deleted
   */
  boolean delete(UUID id);

  /**
   * Get a city by its ID.
   *
   * @param id the city ID.
   * @return the found city.
   * @throws ResourceNotFoundException if the city does not exist OR if the city exists but violates
   *                                   domain rules (data integrity error).
   */
  City getById(UUID id);

  /**
   * Check if a city exists by its IBGE code.
   *
   * @param ibgeCode the IBGE code (already a validated Value Object).
   * @return true if the city exists, false otherwise.
   */
  boolean existsByIbge(IbgeCode ibgeCode);
}
