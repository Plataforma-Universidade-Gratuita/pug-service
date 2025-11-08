package com.pug.partner.infra.persistence;

import com.pug.partner.domain.EntitiesRepository;
import com.pug.partner.domain.Entity;
import com.pug.partner.domain.vos.Cnpj;
import com.pug.partner.infra.EntityMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;

/** Implementation of the EntitiesRepository using Panache. */
@ApplicationScoped
public class EntitiesRepositoryImpl
    implements EntitiesRepository, PanacheRepositoryBase<EntitiesEntity, UUID> {

  @Inject EntityManager entityManager;

  @Transactional
  @Override
  public void persist(Entity entity) {
    if (entity == null) {
      return;
    }
    persistAndFlush(EntityMapper.toEntity(entity));
  }

  @Transactional
  @Override
  public void persistAll(Iterable<Entity> entities) {
    if (entities == null || !entities.iterator().hasNext()) {
      return;
    }
    var batch = new ArrayList<EntitiesEntity>();
    for (var d : entities) {
      if (d != null) {
        batch.add(EntityMapper.toEntity(d));
      }
    }
    if (batch.isEmpty()) {
      return;
    }
    persist(batch);
    flush();
  }

  @Transactional
  @Override
  public long deleteByIds(Iterable<UUID> ids) {
    if (ids == null || !ids.iterator().hasNext()) {
      return 0L;
    }
    long n = delete("id in ?1", ids);
    flush();
    getEntityManager().clear();
    return n;
  }

  @Override
  public Optional<Entity> findOptionalById(UUID id) {
    return find("select e from EntitiesEntity e left join fetch e.city where e.id = ?1", id)
        .firstResultOptional()
        .map(EntityMapper::toDomain);
  }

  @Override
  public Optional<Entity> findOptionalByCnpj(Cnpj cnpj) {
    if (cnpj == null) {
      return Optional.empty();
    }
    return find(
            "select e from EntitiesEntity e left join fetch e.city where e.cnpj = ?1",
            cnpj.toString())
        .firstResultOptional()
        .map(EntityMapper::toDomain);
  }

  @Override
  public List<Entity> listAllEntities() {
    return find("select e from EntitiesEntity e left join fetch e.city").list().stream()
        .map(EntityMapper::toDomain)
        .toList();
  }

  @Override
  public List<Entity> listAllByCityId(UUID cityId) {
    return find("select e from EntitiesEntity e left join fetch e.city where e.cityId = ?1", cityId)
        .list()
        .stream()
        .map(EntityMapper::toDomain)
        .toList();
  }

  @Override
  public List<Entity> searchByName(String query) {
    if (query == null || query.isBlank()) {
      return List.of();
    }
    String[] tokens = query.split("\\s+");
    SearchSession s = Search.session(entityManager);

    List<EntitiesEntity> hits =
        s.search(EntitiesEntity.class)
            .where(
                f ->
                    f.bool(
                        b -> {
                          b.should(
                              f.wildcard().field("name_exact").matching(query + "*").boost(8f));
                          b.should(
                              f.wildcard()
                                  .field("name_exact")
                                  .matching("*" + query + "*")
                                  .boost(6f));
                          for (String t : tokens) {
                            if (t.length() >= 3) {
                              b.should(
                                  f.wildcard()
                                      .field("name_exact")
                                      .matching("*" + t + "*")
                                      .boost(3f));
                            }
                          }
                          b.should(f.match().field("name").matching(query).fuzzy(1).boost(4f));
                          b.should(f.match().field("name_auto").matching(query).boost(2f));
                        }))
            .sort(f -> f.score().then().field("name_sort"))
            .fetchAllHits();

    return hits.stream().map(EntityMapper::toDomain).toList();
  }

  @Override
  public boolean existsByCnpj(String cnpj) {
    String digits = Cnpj.sanitize(cnpj);
    if (digits == null) {
      return false;
    }
    return find("cnpj", digits).firstResultOptional().isPresent();
  }
}
