package com.pug.identity.infra.persistence;

import com.pug.identity.domain.UsersRepository;
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

/**
 * Implementation of the UsersRepository using PanacheRepositoryBase for CRUD operations on
 * UsersEntity.
 */
@ApplicationScoped
public class UsersRepositoryImpl
    implements UsersRepository, PanacheRepositoryBase<UsersEntity, UUID> {

  @Inject EntityManager entityManager;

  @Transactional
  @Override
  public void persist(UsersEntity entity) {
    persistAndFlush(entity);
  }

  @Transactional
  @Override
  public void persistAll(Iterable<UsersEntity> entities) {
    persist(entities);
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
  public Optional<UsersEntity> findOptionalById(UUID id) {
    return findByIdOptional(id);
  }

  @Override
  public Optional<UsersEntity> findOptionalByEmail(String email) {
    return find("email", email).firstResultOptional();
  }

  @Override
  public List<UsersEntity> listAllUsers() {
    return listAll();
  }

  @Override
  public List<UsersEntity> listByCpf(String cpf) {
    return find("cpf", cpf).list();
  }

  @Override
  public boolean existsByEmail(String email) {
    return find("email", email).firstResultOptional().isPresent();
  }

  @Override
  public boolean existsAnyByEmailIn(Collection<String> emails) {
    if (emails == null || emails.isEmpty()) {
      return false;
    }
    return find("email in ?1", emails).firstResultOptional().isPresent();
  }

  @Override
  public List<UsersEntity> searchByName(String key) {
    String[] tokens = key.split("\\s+");
    SearchSession s = Search.session(entityManager);
    return s.search(UsersEntity.class)
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
}
