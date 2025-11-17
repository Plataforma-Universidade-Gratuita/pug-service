package com.pug.geo.service;

import com.pug.geo.domain.City;
import com.pug.geo.domain.CityRepository;
import com.pug.geo.domain.enums.GeoErrorCodes;
import com.pug.geo.domain.vos.IbgeCode;
import com.pug.geo.service.dtos.CityCreateOrUpdateCommand;
import com.pug.partner.service.EntityService;
import com.pug.shared.domain.enums.DeleteKeys;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ReferencedEntityException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.CollectionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Service for managing cities. */
@ApplicationScoped
public class CityService {

  @Inject CityRepository repo;
  @Inject EntityService entityService;

  /**
   * Factory-style save.
   *
   * @param cmd the command with city data.
   * @return the saved city.
   * @throws DuplicateResourceException if a city with the same IBGE code already exists.
   */
  @Transactional
  public City save(CityCreateOrUpdateCommand cmd) {
    if (existsByIbge(cmd.ibgeCode())) {
      throw new DuplicateResourceException(
          GeoErrorCodes.CITY_ALREADY_EXISTS, Map.of("code", cmd.ibgeCode()));
    }
    return repo.persist(City.createNew(cmd.name(), cmd.ibgeCode()));
  }

  /**
   * Bulk save with intra-payload duplicate check.
   *
   * @param cmds the iterable of commands with city data.
   * @return the saved cities.
   * @throws DuplicateResourceException if any city with the same IBGE code already exists.
   */
  @Transactional
  public List<City> saveAll(Iterable<CityCreateOrUpdateCommand> cmds) {
    if (CollectionUtils.isEmpty(cmds)) {
      return List.of();
    }

    Set<String> seen = new HashSet<>();
    for (CityCreateOrUpdateCommand c : cmds) {
      String code = c.ibgeCode().toString();
      if (!seen.add(code)) {
        throw new DuplicateResourceException(
            GeoErrorCodes.CITY_ALREADY_EXISTS, Map.of("code", code));
      }
    }

    List<String> codes = CollectionUtils.toStream(cmds).map(c -> c.ibgeCode().toString()).toList();
    if (repo.existsAnyByIbgeCodeIn(codes)) {
      throw new DuplicateResourceException(GeoErrorCodes.CITY_ALREADY_EXISTS);
    }

    List<City> cities =
        CollectionUtils.toStream(cmds).map(c -> City.createNew(c.name(), c.ibgeCode())).toList();
    return repo.persistAll(cities);
  }

  /**
   * Update name and/or IBGE code.
   *
   * @param id the city ID.
   * @param cmd the command with updated city data.
   * @return the updated city.
   * @throws ResourceNotFoundException if the city does not exist.
   * @throws DuplicateResourceException if a city with the same IBGE code already exists.
   */
  @Transactional
  public City update(UUID id, CityCreateOrUpdateCommand cmd) {
    City current = getById(id);
    IbgeCode code;

    if (cmd.ibgeCode() != null) {
      if (!cmd.ibgeCode().equals(current.getIbgeCode()) && existsByIbge(cmd.ibgeCode())) {
        throw new DuplicateResourceException(
            GeoErrorCodes.CITY_ALREADY_EXISTS, Map.of("code", cmd.ibgeCode()));
      }
      code = cmd.ibgeCode();
    } else {
      code = current.getIbgeCode();
    }

    String name = cmd.name() != null ? cmd.name() : current.getName();
    City updated = current.changeName(name).changeIbgeCode(code);
    repo.update(updated);

    return getById(id);
  }

  /**
   * Delete cities by their IDs.
   *
   * @param ids the iterable of city IDs to delete.
   * @return a map with the number of deleted cities.
   */
  @Transactional
  public Map<DeleteKeys, Long> deleteAll(Iterable<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return Map.of(DeleteKeys.CITIES, 0L);
    }
    if (entityService.existsAnyByCityIdIn(ids)) {
      throw new ReferencedEntityException(GeoErrorCodes.CITY_STILL_REFERENCED_BY_ENTITY);
    }
    return Map.of(DeleteKeys.CITIES, repo.deleteByIds(ids));
  }

  /**
   * Get a city by its ID.
   *
   * @param id the city ID.
   * @return the found city.
   * @throws ResourceNotFoundException if the city does not exist.
   */
  public City getById(UUID id) {
    return repo.findOptionalById(id)
        .orElseThrow(
            () -> new ResourceNotFoundException(GeoErrorCodes.CITY_NOT_FOUND, Map.of("id", id)));
  }

  /**
   * Get a city by its IBGE code.
   *
   * @param ibgeCode the IBGE code.
   * @return the found city.
   * @throws ResourceNotFoundException if the city does not exist.
   */
  public City getByIbge(IbgeCode ibgeCode) {
    return repo.findOptionalByIbgeCode(ibgeCode.toString())
        .orElseThrow(
            () -> new ResourceNotFoundException(GeoErrorCodes.CITY_NOT_FOUND, Map.of("code", ibgeCode)));
  }

  /**
   * Check if a city exists by its IBGE code.
   *
   * @param ibgeCode the IBGE code.
   * @return true if the city exists, false otherwise.
   */
  public boolean existsByIbge(IbgeCode ibgeCode) {
    if (ibgeCode == null) {
      return false;
    }
    return repo.existsByIbgeCode(ibgeCode.toString());
  }
}
