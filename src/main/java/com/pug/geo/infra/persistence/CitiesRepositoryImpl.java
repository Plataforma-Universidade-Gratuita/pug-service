package com.pug.geo.infra.persistence;

import com.pug.geo.domain.CitiesRepository;
import com.pug.shared.text.Normalization;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the CitiesRepository using Panache and Hibernate Search.
 */
@ApplicationScoped
public class CitiesRepositoryImpl
        implements CitiesRepository, PanacheRepositoryBase<CitiesEntity, UUID> {

  @Inject
  EntityManager entityManager;

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

  @Override
  public Optional<CitiesEntity> findOptionalById(UUID id) {
    return findByIdOptional(id);
  }

  @Override
  public Optional<CitiesEntity> findByIbgeCode(String ibgeCodeDigits) {
    return find("ibgeCode", ibgeCodeDigits).firstResultOptional();
  }

  @Override
  public List<CitiesEntity> listAllCities() {
    return listAll();
  }

  @Override
  public List<CitiesEntity> searchByName(String q) {
    String k = Normalization.fold(q).toLowerCase(java.util.Locale.ROOT);
    String[] toks = k.split("\\s+");

    SearchSession s = Search.session(entityManager);
    return s.search(CitiesEntity.class)
            .where(
                    f ->
                            f.bool(
                                    b -> {
                                      b.should(
                                              f.wildcard().field("name_exact").matching(k + "*").boost(8f)); // prefix
                                      b.should(
                                              f.wildcard()
                                                      .field("name_exact")
                                                      .matching("*" + k + "*")
                                                      .boost(6f)); // infix

                                      for (String t : toks) {
                                        if (t.length() >= 3) {
                                          b.should(
                                                  f.wildcard().field("name_exact").matching("*" + t + "*").boost(3f));
                                        }
                                      }

                                      b.should(f.match().field("name").matching(q).fuzzy(1).boost(4f));

                                      b.should(f.match().field("name_auto").matching(q).boost(2f));
                                    }))
            .sort(f -> f.score().then().field("name_sort"))
            .fetchAllHits();
  }
}
