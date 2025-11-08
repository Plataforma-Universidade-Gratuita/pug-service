package com.pug.identity.infra.persistence;

import com.pug.identity.domain.User;
import com.pug.identity.domain.UsersRepository;
import com.pug.identity.infra.UserMapper;
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
  public User persist(User user) {
    if (user == null) {
      return null;
    }
    UsersEntity e = UserMapper.toEntity(user);
    persistAndFlush(e);
    return UserMapper.toDomain(e);
  }

  @Transactional
  @Override
  public List<User> persistAll(Iterable<User> users) {
    if (users == null || !users.iterator().hasNext()) {
      return List.of();
    }
    var batch = new ArrayList<UsersEntity>();
    for (User d : users) {
      if (d != null) {
        batch.add(UserMapper.toEntity(d));
      }
    }
    if (batch.isEmpty()) {
      return List.of();
    }
    persist(batch);
    flush();
    return batch.stream().map(UserMapper::toDomain).toList();
  }

  @Transactional
  @Override
  public long deleteByIds(Iterable<UUID> ids) {
    if (!ids.iterator().hasNext()) {
      return 0L;
    }
    long deleted = delete("id in ?1", ids);
    flush();
    getEntityManager().clear();
    return deleted;
  }

  @Transactional
  @Override
  public void deactivateById(UUID id) {
    UsersEntity e = findById(id);
    if (e == null) {
      return;
    }
    if (Boolean.TRUE.equals(e.getActive())) {
      e.setActive(false);
    }
  }

  @Override
  public Optional<User> findOptionalById(UUID id) {
    return findByIdOptional(id).map(UserMapper::toDomain);
  }

  @Override
  public Optional<User> findOptionalByEmail(String email) {
    return find("email", email).firstResultOptional().map(UserMapper::toDomain);
  }

  @Override
  public List<User> listAllUsers() {
    return listAll().stream().map(UserMapper::toDomain).toList();
  }

  @Override
  public List<User> listByCpf(String cpf) {
    return find("cpf", cpf).list().stream().map(UserMapper::toDomain).toList();
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
  public List<User> searchByName(String key) {
    if (key == null || key.isBlank()) {
      return List.of();
    }
    String[] tokens = key.split("\\s+");
    SearchSession s = Search.session(entityManager);

    List<UsersEntity> hits =
        s.search(UsersEntity.class)
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

    return hits.stream().map(UserMapper::toDomain).toList();
  }

  @Override
  public void update(User user) {
    if (user == null || user.getId() == null) {
      return;
    }
    UsersEntity managed = findById(user.getId());
    if (managed == null) {
      return;
    }
    UserMapper.copy(user, managed);
  }
}
