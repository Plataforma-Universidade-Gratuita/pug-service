package br.org.catolicasc.pug.identity.infra.persistence.impl;

import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.identity.domain.UserRepository;
import br.org.catolicasc.pug.identity.infra.UserMapper;
import br.org.catolicasc.pug.identity.infra.persistence.UserEntity;
import br.org.catolicasc.pug.shared.utils.CollectionUtils;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the {@link UserRepository} utilizing Hibernate ORM with Panache.
 *
 * <p>This application-scoped bean bridges the pure domain repository interface with the underlying
 * database infrastructure. It manages transaction boundaries, entity state transitions, and the
 * mapping between domain aggregates and JPA persistence entities.
 */
@ApplicationScoped
public class UserRepositoryImpl implements UserRepository, PanacheRepositoryBase<UserEntity, UUID> {

  /** {@inheritDoc} */
  @Override
  @Transactional
  public long deleteAllByIds(List<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return 0;
    }
    return delete("id in ?1", ids);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public boolean deleteById(UUID id) {
    if (id == null) {
      return false;
    }
    boolean deleted = delete("id", id) > 0;
    flush();
    return deleted;
  }

  /** {@inheritDoc} */
  @Override
  public boolean existsAnyByCpfs(List<String> cpfs) {
    if (CollectionUtils.isEmpty(cpfs)) {
      return false;
    }
    return count("cpf in ?1", cpfs) > 0;
  }

  /** {@inheritDoc} */
  @Override
  public boolean existsByCpf(String cpf) {
    if (StringUtils.isEmpty(cpf)) {
      return false;
    }
    return count("cpf = ?1", cpf) > 0;
  }

  /** {@inheritDoc} */
  @Override
  public Optional<User> findOptionalByCpf(String cpf) {
    if (StringUtils.isEmpty(cpf)) {
      return Optional.empty();
    }
    return find("cpf", cpf).firstResultOptional().map(UserMapper::toDomain);
  }

  /** {@inheritDoc} */
  @Override
  public Optional<User> findOptionalById(UUID id) {
    return findByIdOptional(id).map(UserMapper::toDomain);
  }

  /** {@inheritDoc} */
  @Override
  public List<User> listByCpfs(List<String> cpfs) {
    if (CollectionUtils.isEmpty(cpfs)) {
      return List.of();
    }
    return find("cpf in ?1", cpfs).stream().map(UserMapper::toDomain).toList();
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public User persist(User entity) {
    if (entity == null) {
      return null;
    }
    UserEntity e = UserMapper.toEntity(entity);
    persistAndFlush(e);
    return UserMapper.toDomain(e);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public List<User> persistAll(List<User> users) {
    if (CollectionUtils.isEmpty(users)) {
      return List.of();
    }
    List<UserEntity> entities = users.stream().map(UserMapper::toEntity).toList();
    persist(entities);
    flush();
    return entities.stream().map(UserMapper::toDomain).toList();
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
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
}
