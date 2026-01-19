package com.pug.geo.infra.persistence;

import com.pug.geo.domain.City;
import com.pug.geo.domain.CityRepository;
import com.pug.geo.infra.CityMapper;
import com.pug.shared.utils.CollectionUtils;
import com.pug.shared.utils.StringUtils;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Implementation of the CitiesRepository using Panache and Hibernate Search. */
@ApplicationScoped
public class CityRepositoryImpl implements CityRepository, PanacheRepositoryBase<CityEntity, UUID> {

  @Transactional
  @Override
  public City persist(City city) {
    if (city == null) {
      return null;
    }
    var e = CityMapper.toEntity(city);
    persistAndFlush(e);
    return CityMapper.toDomain(e);
  }

  @Transactional
  @Override
  public List<City> persistAll(Iterable<City> cities) {
    if (CollectionUtils.isEmpty(cities)) {
      return List.of();
    }
    var entities = new ArrayList<CityEntity>();
    for (City c : cities) {
      if (c != null) {
        entities.add(CityMapper.toEntity(c));
      }
    }
    if (entities.isEmpty()) {
      return List.of();
    }
    persist(entities);
    flush();
    return entities.stream().map(CityMapper::toDomain).toList();
  }

  @Override
  public void update(City city) {
    if (city == null || city.getId() == null) {
      return;
    }
    CityEntity managed = findById(city.getId());
    if (managed == null) {
      return;
    }
    CityMapper.copy(city, managed);
  }

  @Transactional
  @Override
  public long deleteByIds(Iterable<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return 0L;
    }
    long deleted = delete("id in ?1", ids);
    flush();
    getEntityManager().clear();
    return deleted;
  }

  @Override
  public Optional<City> findOptionalById(UUID id) {
    return findByIdOptional(id).map(CityMapper::toDomain);
  }

  @Override
  public Optional<City> findOptionalByIbgeCode(String ibgeCodeDigits) {
    return find("ibgeCode", ibgeCodeDigits).firstResultOptional().map(CityMapper::toDomain);
  }

  @Override
  public boolean existsByIbgeCode(String ibgeCodeDigits) {
    if (StringUtils.isEmpty(ibgeCodeDigits)) {
      return false;
    }
    return find("ibgeCode", ibgeCodeDigits).firstResultOptional().isPresent();
  }

  @Override
  public boolean existsAnyByIbgeCodeIn(Iterable<String> ibges) {
    if (CollectionUtils.isEmpty(ibges)) {
      return false;
    }
    return find("ibgeCode in ?1", ibges).firstResultOptional().isPresent();
  }
}
