package com.pug.geo.infra.persistence;

import com.pug.geo.domain.CitiesRepository;
import com.pug.geo.domain.City;
import com.pug.geo.infra.CityMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;

/** Implementation of the CitiesRepository using Panache and Hibernate Search. */
@ApplicationScoped
public class CitiesRepositoryImpl
    implements CitiesRepository, PanacheRepositoryBase<CitiesEntity, UUID> {

  @Inject EntityManager entityManager;

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
    if (cities == null || !cities.iterator().hasNext()) {
      return List.of();
    }
    var entities = new ArrayList<CitiesEntity>();
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

  @Transactional
  @Override
  public long deleteByIds(Iterable<UUID> ids) {
    if (ids == null || !ids.iterator().hasNext()) {
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
  public List<City> listAllCities() {
    return listAll().stream().map(CityMapper::toDomain).toList();
  }

  @Override
  public List<City> searchByName(String key) {
    if (key == null || key.isBlank()) {
      return List.of();
    }
    String[] tokens = key.split("\\s+");
    SearchSession s = Search.session(entityManager);

    List<CitiesEntity> hits =
        s.search(CitiesEntity.class)
            .where(
                f ->
                    f.bool(
                        b -> {
                          b.should(f.wildcard().field("name_exact").matching(key + "*").boost(8f));
                          b.should(
                              f.wildcard().field("name_exact").matching("*" + key + "*").boost(6f));
                          for (String t : tokens) {
                            if (t.length() >= 3) {
                              b.should(
                                  f.wildcard()
                                      .field("name_exact")
                                      .matching("*" + t + "*")
                                      .boost(3f));
                            }
                          }
                          b.should(f.match().field("name").matching(key).fuzzy(1).boost(4f));
                          b.should(f.match().field("name_auto").matching(key).boost(2f));
                        }))
            .sort(f -> f.score().then().field("name_sort"))
            .fetchAllHits();

    return hits.stream().map(CityMapper::toDomain).toList();
  }

  @Override
  public boolean existsByIbgeCode(String ibgeCodeDigits) {
    return find("ibgeCode", ibgeCodeDigits).firstResultOptional().isPresent();
  }

  @Override
  public boolean existsAnyByIbgeCodeIn(Collection<String> ibges) {
    if (ibges == null || ibges.isEmpty()) {
      return false;
    }
    return find("ibgeCode in ?1", ibges).firstResultOptional().isPresent();
  }

  @Override
  public void update(City city) {
    if (city == null || city.getId() == null) {
      return;
    }
    CitiesEntity managed = findById(city.getId());
    if (managed == null) {
      return;
    }
    com.pug.geo.infra.CityMapper.copy(city, managed);
  }
}
