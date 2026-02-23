package com.pug.identity.infra.persistence.impl;

import com.pug.identity.domain.User;
import com.pug.identity.domain.UserRepository;
import com.pug.identity.infra.UserMapper;
import com.pug.identity.infra.persistence.UserEntity;
import com.pug.shared.utils.CollectionUtils;
import com.pug.shared.utils.StringUtils;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Repository implementation for User aggregate. */
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
  public boolean deleteById(UUID id) {
    if (id == null) {
      return false;
    }
    boolean deleted = PanacheRepositoryBase.super.deleteById(id);
    flush();
    return deleted;
  }

  @Transactional
  @Override
  public long deleteAllByIds(List<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return 0;
    }
    return delete("id in ?1", ids);
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
  public boolean existsByCpf(String cpf) {
    if (StringUtils.isEmpty(cpf)) {
      return false;
    }
    return count("cpf = ?1", cpf) > 0;
  }
}
