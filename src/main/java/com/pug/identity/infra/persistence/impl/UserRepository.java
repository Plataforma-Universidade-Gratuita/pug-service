package com.pug.identity.infra.persistence.impl;

import com.pug.identity.domain.IUserRepository;
import com.pug.identity.domain.User;
import com.pug.identity.infra.UserMapper;
import com.pug.identity.infra.persistence.UserEntity;
import com.pug.shared.utils.CollectionUtils;
import com.pug.shared.utils.StringUtils;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Repository implementation for User aggregate. */
@ApplicationScoped
public class UserRepository implements IUserRepository, PanacheRepositoryBase<UserEntity, UUID> {

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
    if (CollectionUtils.isEmpty(entities)) {
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
    if (CollectionUtils.isEmpty(ids)) {
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
  public Optional<User> findOptionalByCpf(String cpf) {
    if (StringUtils.isEmpty(cpf)) {
      return Optional.empty();
    }
    return find("cpf", cpf).firstResultOptional().map(UserMapper::toDomain);
  }

  @Override
  public List<User> listAllUsers() {
    return listAll().stream().map(UserMapper::toDomain).toList();
  }

  @Override
  public List<User> listByCpfs(Iterable<String> cpfs) {
    if (CollectionUtils.isEmpty(cpfs)) {
      return List.of();
    }
    return find("cpf in ?1", cpfs).list().stream().map(UserMapper::toDomain).toList();
  }

  @Override
  public boolean existsByCpf(String cpf) {
    if (StringUtils.isEmpty(cpf)) {
      return false;
    }
    return count("cpf = ?1", cpf) > 0;
  }

  @Override
  public boolean existsAnyByCpfIn(Iterable<String> cpfs) {
    if (CollectionUtils.isEmpty(cpfs)) {
      return false;
    }
    return find("cpf in ?1", cpfs).firstResultOptional().isPresent();
  }
}
