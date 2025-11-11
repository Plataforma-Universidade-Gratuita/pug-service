package com.pug.identity.infra.persistence;

import com.pug.identity.domain.User;
import com.pug.identity.domain.UserRepository;
import com.pug.identity.infra.UserMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Repository implementation for Person aggregate. */
@ApplicationScoped
public class UserRepositoryImpl implements UserRepository, PanacheRepositoryBase<UserEntity, UUID> {

  @Transactional
  @Override
  public User persist(User entity) {
    if (entity == null) {
      return null;
    }
    UserEntity e = UserMapper.toEntity(entity);
    persistAndFlush(e);
    return UserMapper.toDomain(e);
  }

  @Transactional
  @Override
  public List<User> persistAll(Iterable<User> entities) {
    if (entities == null || !entities.iterator().hasNext()) {
      return List.of();
    }
    var batch = new ArrayList<UserEntity>();
    for (User p : entities) {
      if (p != null) {
        batch.add(UserMapper.toEntity(p));
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
  public void update(User entity) {
    if (entity == null || entity.getId() == null) {
      return;
    }
    UserEntity e = findById(entity.getId());
    if (e == null) {
      return;
    }
    UserMapper.copy(entity, e);
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
  public Optional<User> findOptionalById(UUID id) {
    return findByIdOptional(id).map(UserMapper::toDomain);
  }

  @Override
  public List<User> listAllUsers() {
    return listAll().stream().map(UserMapper::toDomain).toList();
  }

  @Override
  public boolean existsByCpf(String cpf) {
    return find("cpf", cpf).firstResultOptional().isPresent();
  }

  @Override
  public boolean existsAnyByCpfIn(Iterable<String> cpfs) {
    if (cpfs == null || !cpfs.iterator().hasNext()) {
      return false;
    }
    return find("cpf in ?1", cpfs).firstResultOptional().isPresent();
  }
}
