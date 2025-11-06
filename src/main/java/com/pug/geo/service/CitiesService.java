package com.pug.geo.service;

import com.pug.geo.domain.CitiesRepository;
import com.pug.geo.domain.City;
import com.pug.geo.domain.errors.GeoErrorCodes;
import com.pug.geo.infra.CityMapper;
import com.pug.geo.infra.persistence.CitiesEntity;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.text.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

/** Service for managing cities. */
@ApplicationScoped
public class CitiesService {

  @Inject CitiesRepository repo;

  /**
   * Save a single city.
   *
   * @param city the city to save.
   * @return the saved city.
   */
  @Transactional
  public City save(City city) {
    if (repo.existsByIbgeCode(city.getIbgeCode().toString())) {
      throw new DuplicateResourceException(GeoErrorCodes.CITY_ALREADY_EXISTS);
    }
    CitiesEntity e = CityMapper.toEntity(city);
    repo.persist(e);
    return CityMapper.toDomain(e);
  }

  /**
   * Save cities in bulk.
   *
   * @param cities the cities to save.
   * @throws DuplicateResourceException if any city already exists.
   */
  @Transactional
  public void saveAll(Iterable<City> cities) {
    List<String> codes =
        java.util.stream.StreamSupport.stream(cities.spliterator(), false)
            .map(c -> c.getIbgeCode().toString())
            .toList();
    if (repo.existsAnyByIbgeCodeIn(codes)) {
      throw new DuplicateResourceException(GeoErrorCodes.CITY_ALREADY_EXISTS);
    }
    List<CitiesEntity> entities =
        StreamSupport.stream(cities.spliterator(), false).map(CityMapper::toEntity).toList();
    repo.persistAll(entities);
  }

  /**
   * Update a city by id.
   *
   * @param id the id of the city to update.
   * @param data the content to update.
   * @return the updated city.
   * @throws ResourceNotFoundException if the city is not found.
   */
  @Transactional
  public City update(UUID id, City data) {
    var entity =
        repo.findOptionalById(id)
            .orElseThrow(() -> new ResourceNotFoundException(GeoErrorCodes.CITY_NOT_FOUND));
    CityMapper.copy(data, entity);
    return CityMapper.toDomain(entity);
  }

  /**
   * Delete cities by ids.
   *
   * @param ids the ids of the cities to delete.
   * @return the number of deleted cities.
   */
  @Transactional
  public long deleteByIds(Iterable<UUID> ids) {
    return repo.deleteByIds(ids);
  }

  /**
   * List all cities.
   *
   * @return list of cities.
   */
  public List<City> listAll() {
    return repo.listAllCities().stream().map(CityMapper::toDomain).toList();
  }

  /**
   * Get a city by id or throw.
   *
   * @param id the id of the city.
   * @return the found city.
   * @throws ResourceNotFoundException if the city is not found.
   */
  public City getById(UUID id) {
    return repo.findOptionalById(id)
        .map(CityMapper::toDomain)
        .orElseThrow(() -> new ResourceNotFoundException(GeoErrorCodes.CITY_NOT_FOUND));
  }

  /**
   * Get a city by IBGE code or throw.
   *
   * @param ibgeCode the IBGE code of the city.
   * @return the found city.
   * @throws ResourceNotFoundException if the city is not found.
   */
  public City getByIbgeCode(String ibgeCode) {
    return repo.findOptionalByIbgeCode(ibgeCode)
        .map(CityMapper::toDomain)
        .orElseThrow(() -> new ResourceNotFoundException(GeoErrorCodes.CITY_NOT_FOUND));
  }

  /**
   * Search cities by name query.
   *
   * @param query the search query.
   * @return list of matching cities.
   */
  public List<City> search(String query) {
    String key = StringUtils.fold(query).toLowerCase(java.util.Locale.ROOT);
    return repo.searchByName(key).stream().map(CityMapper::toDomain).toList();
  }
}
