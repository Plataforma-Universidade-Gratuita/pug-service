package com.pug.geo.service;

import com.pug.geo.domain.CitiesRepository;
import com.pug.geo.domain.City;
import com.pug.geo.domain.errors.GeoErrorCodes;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.text.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

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
    String code = city.getIbgeCode().toString();
    if (repo.existsByIbgeCode(code)) {
      throw new DuplicateResourceException(GeoErrorCodes.CITY_ALREADY_EXISTS);
    }
    return repo.persist(city);
  }

  /**
   * Save cities in bulk.
   *
   * @param cities the cities to save.
   * @return the saved cities.
   * @throws DuplicateResourceException if any city already exists.
   */
  @Transactional
  public List<City> saveAll(Iterable<City> cities) {
    List<City> list = toStream(cities).filter(Objects::nonNull).toList();
    if (!list.isEmpty()) {
      List<String> codes = list.stream().map(c -> c.getIbgeCode().toString()).toList();
      if (repo.existsAnyByIbgeCodeIn(codes)) {
        throw new DuplicateResourceException(GeoErrorCodes.CITY_ALREADY_EXISTS);
      }
    }
    return repo.persistAll(list);
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
    City current =
        repo.findOptionalById(id)
            .orElseThrow(() -> new ResourceNotFoundException(GeoErrorCodes.CITY_NOT_FOUND));

    City updated = current.toBuilder().name(data.getName()).ibgeCode(data.getIbgeCode()).build();

    repo.update(updated);
    return repo.findOptionalById(id)
        .orElseThrow(() -> new ResourceNotFoundException(GeoErrorCodes.CITY_NOT_FOUND));
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
    return repo.listAllCities();
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
        .orElseThrow(() -> new ResourceNotFoundException(GeoErrorCodes.CITY_NOT_FOUND));
  }

  /**
   * Search cities by name query.
   *
   * @param query the search query.
   * @return list of matching cities.
   */
  public List<City> search(String query) {
    String key = StringUtils.fold(query).toLowerCase(Locale.ROOT);
    return repo.searchByName(key);
  }

  /**
   * Convert an Iterable to a Stream.
   *
   * @param it the iterable
   * @param <T> the type of elements
   * @return the stream
   */
  private static <T> java.util.stream.Stream<T> toStream(Iterable<T> it) {
    return it == null
        ? java.util.stream.Stream.empty()
        : java.util.stream.StreamSupport.stream(it.spliterator(), false);
  }
}
