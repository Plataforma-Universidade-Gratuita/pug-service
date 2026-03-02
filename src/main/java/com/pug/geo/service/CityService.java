package com.pug.geo.service;

import com.pug.geo.domain.City;
import com.pug.geo.service.dtos.CityCreateCommand;
import com.pug.geo.service.dtos.CityUpdateCommand;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ResourceNotFoundException;

import java.util.UUID;

/**
 * Application service interface for managing the state of {@link City} domain aggregates.
 * <p>
 * Following CQRS principles, this service handles the "Command" operations (Create, Update, Delete)
 * and strict domain-level retrievals. It orchestrates domain logic, enforces business constraints,
 * and coordinates with the persistence layer to ensure the integrity of geographic data.
 */
public interface CityService {

  /**
   * Instantiates and persists a new {@link City} aggregate based on the provided command.
   * <p>
   * The command data is routed through the domain's factory methods to ensure all
   * internal validations (e.g., IBGE code formatting) are strictly applied before persistence.
   *
   * @param cmd the structured command containing the data required to create a city
   * @return the fully instantiated and persisted {@link City} aggregate
   * @throws DuplicateResourceException if a city with the provided IBGE code already exists
   * @throws AppValidationException     if the input data violates domain constraints
   *                                    (e.g., blank name, malformed IBGE code)
   */
  City save(CityCreateCommand cmd);

  /**
   * Updates the state (name and/or IBGE code) of an existing {@link City} aggregate.
   * <p>
   * This method reconstitutes the aggregate from the repository, applies the requested
   * mutations through domain behaviors, and persists the updated state.
   *
   * @param id  the unique identifier (UUIDv7) of the city to update
   * @param cmd the structured command containing the updated city data
   * @return the mutated and persisted {@link City} aggregate
   * @throws ResourceNotFoundException  if the city cannot be found in the repository
   * @throws DuplicateResourceException if the updated IBGE code conflicts with an existing city
   * @throws AppValidationException     if the updated input data violates domain constraints
   */
  City update(UUID id, CityUpdateCommand cmd);

  /**
   * Removes a {@link City} from the system by its unique identifier.
   * <p>
   * This operation enforces strict data retention and integrity policies.
   * Attempting to delete protected system defaults (e.g., Jaraguá do Sul or Joinville)
   * or cities that are currently assigned to active aggregates (such as Partner entities)
   * is strictly prohibited and will actively fail to prevent orphaned records.
   *
   * @param id the unique identifier (UUID) of the city to delete
   * @return {@code true} if the city was successfully deleted, {@code false} if the ID is null
   * or if the actual database deletion was silently ignored (e.g., an idempotent concurrent delete)
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the city to be deleted does not exist
   * @throws com.pug.shared.exceptions.BusinessRuleException     if the city is a protected default system record,
   *                                                             or if it is currently referenced by other entities
   */
  boolean delete(UUID id);

  /**
   * Retrieves a full {@link City} domain aggregate by its unique identifier.
   * <p>
   * <b>Note:</b> This method is intended strictly for internal domain orchestration
   * (e.g., loading an aggregate to mutate it). For API responses, use {@link CityReadService#getViewById(UUID)} instead.
   *
   * @param id the unique identifier (UUID) of the city
   * @return the fully reconstituted {@link City} aggregate
   * @throws ResourceNotFoundException if the city does not exist
   * @throws AppValidationException    if the city exists in the database but its stored state
   *                                   currently violates strict domain invariants (data integrity error)
   */
  City getById(UUID id);
}