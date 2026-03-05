package com.pug.geo.infra.persistence.impl;

import com.pug.geo.domain.City;
import com.pug.geo.domain.CityRepository;
import com.pug.geo.infra.CityMapper;
import com.pug.geo.infra.persistence.CityEntity;
import com.pug.shared.utils.StringUtils;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

/** Implementation of the CityRepository using Panache and Hibernate Search. */
@ApplicationScoped
public class CityRepositoryImpl implements CityRepository, PanacheRepositoryBase<CityEntity, UUID> {

  /** {@inheritDoc} */
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

  /** {@inheritDoc} */
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

  /** {@inheritDoc} */
  @Transactional
  @Override
  public boolean deleteById(UUID id) {
    if (id == null) {
      return false;
    }
    var deleted = PanacheRepositoryBase.super.deleteById(id);
    flush();
    return deleted;
  }

  /** {@inheritDoc} */
  @Override
  public Optional<City> findOptionalById(UUID id) {
    return findByIdOptional(id).map(CityMapper::toDomain);
  }

  /** {@inheritDoc} */
  @Override
  public boolean existsByIbgeCode(String ibgeCodeDigits) {
    if (StringUtils.isEmpty(ibgeCodeDigits)) {
      return false;
    }
    return find("ibgeCode", ibgeCodeDigits).firstResultOptional().isPresent();
  }
}
