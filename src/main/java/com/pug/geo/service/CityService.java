package com.pug.geo.service;

import com.pug.geo.domain.City;
import com.pug.geo.domain.CityRepository;
import com.pug.geo.domain.enums.GeoErrorCodes;
import com.pug.geo.domain.vos.IbgeCode;
import com.pug.partner.service.EntityService;
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
import java.util.Objects;
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
   * @param name the city name.
   * @param ibgeCode the city IBGE code.
   * @return the saved city.
   * @throws DuplicateResourceException if a city with the same IBGE code already exists.
   */
  @Transactional
  public City save(String name, IbgeCode ibgeCode) {
    Objects.requireNonNull(ibgeCode, "ibgeCode");
    String code = ibgeCode.toString();
    if (repo.existsByIbgeCode(code)) {
      throw new DuplicateResourceException(
          GeoErrorCodes.CITY_ALREADY_EXISTS, Map.of("code", ibgeCode));
    }
    return repo.persist(City.createNew(name, ibgeCode));
  }

  /**
   * Bulk save with intra-payload duplicate check.
   *
   * @param cities the cities to save.
   * @return the saved cities.
   * @throws DuplicateResourceException if any city with the same IBGE code already exists.
   */
  @Transactional
  public List<City> saveAll(Iterable<City> cities) {
    List<City> list = CollectionUtils.toStream(cities).filter(Objects::nonNull).toList();
    if (list.isEmpty()) {
      return List.of();
    }

    Set<String> seen = new HashSet<>();
    for (City c : list) {
      String code = c.getIbgeCode().toString();
      if (!seen.add(code)) {
        throw new DuplicateResourceException(
            GeoErrorCodes.CITY_ALREADY_EXISTS, Map.of("code", code));
      }
    }

    List<String> codes = list.stream().map(c -> c.getIbgeCode().toString()).toList();
    if (repo.existsAnyByIbgeCodeIn(codes)) {
      throw new DuplicateResourceException(GeoErrorCodes.CITY_ALREADY_EXISTS);
    }

    return repo.persistAll(list);
  }

  /**
   * Update name and/or IBGE code.
   *
   * @param id the city ID.
   * @param data the new city data.
   * @return the updated city.
   * @throws ResourceNotFoundException if the city does not exist.
   * @throws DuplicateResourceException if a city with the same IBGE code already exists.
   */
  @Transactional
  public City update(UUID id, City data) {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(data, "data");
    City current = getById(id);
    String newCode = data.getIbgeCode().toString();
    String curCode = current.getIbgeCode().toString();

    if (!newCode.equals(curCode) && repo.existsByIbgeCode(newCode)) {
      throw new DuplicateResourceException(
          GeoErrorCodes.CITY_ALREADY_EXISTS, Map.of("code", newCode));
    }

    City updated = current.changeName(data.getName()).changeIbgeCode(data.getIbgeCode());
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
  public Map<String, Long> deleteByIds(Iterable<UUID> ids) {
    List<UUID> list = CollectionUtils.toStream(ids).filter(Objects::nonNull).toList();
    if (list.isEmpty()) {
      return Map.of();
    }
    if (entityService.existsAnyByCityIdIn(list)) {
      throw new ReferencedEntityException(GeoErrorCodes.CITY_REFERENCED_BY_ENTITY);
    }
    return Map.of("cities", repo.deleteByIds(list));
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
}
