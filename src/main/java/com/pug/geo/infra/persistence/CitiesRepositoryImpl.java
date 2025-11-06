package com.pug.geo.infra.persistence;

import com.pug.geo.domain.CitiesRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
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
  public void persist(CitiesEntity city) {
    persistAndFlush(city);
  }

  @Transactional
  @Override
  public void persistAll(Iterable<CitiesEntity> cities) {
    persist(cities);
    flush();
  }

  @Transactional
  @Override
  public long deleteByIds(Iterable<UUID> ids) {
    if (!ids.iterator().hasNext()) {
      return 0;
    }
    long deleted = delete("id in ?1", ids);
    flush();
    return deleted;
  }

  @Override
  public Optional<CitiesEntity> findOptionalById(UUID id) {
    return findByIdOptional(id);
  }

  @Override
  public Optional<CitiesEntity> findOptionalByIbgeCode(String ibgeCodeDigits) {
    return find("ibgeCode", ibgeCodeDigits).firstResultOptional();
  }

  @Override
  public List<CitiesEntity> listAllCities() {
    return listAll();
  }

  @Override
  public List<CitiesEntity> searchByName(String key) {
    String[] tokens = key.split("\\s+");
    SearchSession s = Search.session(entityManager);
    return s.search(CitiesEntity.class)
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
                              f.wildcard().field("name_exact").matching("*" + t + "*").boost(3f));
                        }
                      }
                      b.should(f.match().field("name").matching(key).fuzzy(1).boost(4f));
                      b.should(f.match().field("name_auto").matching(key).boost(2f));
                    }))
        .sort(f -> f.score().then().field("name_sort"))
        .fetchAllHits();
  }

  @Override
  public boolean existsByIbgeCode(String ibgeCodeDigits) {
    return find("ibgeCode", ibgeCodeDigits).firstResultOptional().isPresent();
  }

  @Override
  public boolean existsAnyByIbgeCodeIn(Collection<String> ibges) {
    if (ibges.isEmpty()) {
      return false;
    }
    return find("ibgeCode in ?1", ibges).firstResultOptional().isPresent();
  }
}
