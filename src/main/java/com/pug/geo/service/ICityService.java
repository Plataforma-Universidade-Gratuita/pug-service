package com.pug.geo.service;

import com.pug.geo.domain.City;
import com.pug.geo.domain.vos.IbgeCode;
import com.pug.geo.service.dtos.CityCreateOrUpdateCommand;
import com.pug.shared.domain.enums.DeleteKeys;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Interface for managing City entities.
 */
public interface ICityService {

    /**
     * Factory-style save.
     *
     * @param cmd the command with city data.
     * @return the saved city.
     * @throws com.pug.shared.exceptions.DuplicateResourceException if a city with the same IBGE code already exists.
     * @throws com.pug.shared.exceptions.AppValidationException     if input validation fails (e.g., blank name, invalid IBGE code).
     */
    City save(CityCreateOrUpdateCommand cmd);

    /**
     * Bulk save with intra-payload duplicate check.
     *
     * @param cmds the iterable of commands with city data.
     * @return the saved cities.
     * @throws com.pug.shared.exceptions.DuplicateResourceException if any city with the same IBGE code already exists.
     * @throws com.pug.shared.exceptions.AppValidationException     if input validation fails for any city in the bulk.
     */
    List<City> saveAll(Iterable<CityCreateOrUpdateCommand> cmds);

    /**
     * Update name and/or IBGE code.
     *
     * @param id  the city ID.
     * @param cmd the command with updated city data.
     * @return the updated city.
     * @throws com.pug.shared.exceptions.ResourceNotFoundException  if the city does not exist (or data is corrupted in DB).
     * @throws com.pug.shared.exceptions.DuplicateResourceException if a city with the same IBGE code already exists.
     * @throws com.pug.shared.exceptions.AppValidationException     if input validation fails.
     */
    City update(UUID id, CityCreateOrUpdateCommand cmd);

    /**
     * Delete cities by their IDs.
     *
     * @param ids the iterable of city IDs to delete.
     * @return a map with the number of deleted cities.
     * @throws com.pug.shared.exceptions.ReferencedEntityException if any city is still referenced by another entity.
     */
    Map<DeleteKeys, Long> deleteAll(Iterable<UUID> ids);

    /**
     * Get a city by its ID.
     *
     * @param id the city ID.
     * @return the found city.
     * @throws com.pug.shared.exceptions.ResourceNotFoundException if the city does not exist.
     * @throws com.pug.shared.exceptions.AppValidationException    if the city is found but its data is corrupted in the database.
     */
    City getById(UUID id);

    /**
     * Get a city by its IBGE code.
     *
     * @param ibgeCode the IBGE code (already a validated Value Object).
     * @return the found city.
     * @throws com.pug.shared.exceptions.ResourceNotFoundException if the city does not exist.
     * @throws com.pug.shared.exceptions.AppValidationException    if the city is found but its data is corrupted in the database.
     */
    City getByIbge(IbgeCode ibgeCode);

    /**
     * Check if a city exists by its IBGE code.
     *
     * @param ibgeCode the IBGE code (already a validated Value Object).
     * @return true if the city exists, false otherwise.
     */
    boolean existsByIbge(IbgeCode ibgeCode);
}