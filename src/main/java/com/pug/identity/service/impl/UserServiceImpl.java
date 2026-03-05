package com.pug.identity.service.impl;

import com.pug.identity.domain.User;
import com.pug.identity.domain.UserRepository;
import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.service.UserService;
import com.pug.identity.service.dtos.UserCreateCommand;
import com.pug.identity.service.dtos.UserUpdateCommand;
import com.pug.identity.service.utils.ExceptionHelper;
import com.pug.identity.service.utils.UserProcessor;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.utils.CollectionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

/**
 * Implementation of the {@link UserService} command interface.
 *
 * <p>This application-scoped service acts as the orchestrator for user state mutations. It manages
 * transaction boundaries, invokes pure domain logic via {@link UserProcessor}, enforces
 * cross-cutting business rules (e.g., CPF uniqueness), and coordinates with the underlying {@link
 * UserRepository}.
 */
@ApplicationScoped
public class UserServiceImpl implements UserService {

  private static final Logger LOG = Logger.getLogger(UserServiceImpl.class);

  @Inject UserRepository repo;

  /** {@inheritDoc} */
  @Transactional
  @Override
  public boolean delete(UUID id) {
    LOG.debugf("Attempting to delete User ID: %s", id);

    boolean deleted = repo.deleteById(id);

    if (deleted) {
      LOG.infof("User deleted successfully. ID: %s", id);
    } else {
      LOG.debugf("Delete failed: User ID %s not found (idempotent response)", id);
    }

    return deleted;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public long deleteAll(List<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return 0;
    }
    LOG.debugf("Attempting to delete multiple Users. IDs: %s", ids);
    long deletedCount = repo.deleteAllByIds(ids);

    LOG.infof("Batch delete completed. Requested: %d, Deleted: %d", ids.size(), deletedCount);
    return deletedCount;
  }

  /** {@inheritDoc} */
  @Override
  public boolean existsByCpf(Cpf cpf) {
    if (cpf == null) {
      return false;
    }
    return repo.existsByCpf(cpf.toString());
  }

  /** {@inheritDoc} */
  @Override
  public User getByCpf(Cpf cpf) {
    User user =
        repo.findOptionalByCpf(cpf.toString())
            .orElseThrow(
                () -> {
                  LOG.debugf("User lookup failed: CPF %s not found", cpf);
                  return ExceptionHelper.userNotFound();
                });

    if (user.hasFieldErrors()) {
      LOG.errorf(
          "DATA CORRUPTION DETECTED: User with CPF %s violates domain rules: %s",
          cpf, user.getProblemsSummary());
      throw ExceptionHelper.userNotFound();
    }

    return user;
  }

  /** {@inheritDoc} */
  @Override
  public User getById(UUID id) {
    User user =
        repo.findOptionalById(id)
            .orElseThrow(
                () -> {
                  LOG.debugf("User lookup failed: ID %s not found", id);
                  return ExceptionHelper.userNotFound();
                });

    if (user.hasFieldErrors()) {
      LOG.errorf(
          "DATA CORRUPTION DETECTED: User %s violates domain rules: %s",
          id, user.getProblemsSummary());
      throw ExceptionHelper.userNotFound();
    }

    return user;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public User save(UserCreateCommand cmd) {
    LOG.debugf("Attempting to create User with name: '%s'", cmd.name());
    User userToPersist = UserProcessor.processCreateInput(cmd.cpfString(), cmd.name());

    if (userToPersist.hasFieldErrors()) {
      throw new AppValidationException(userToPersist.getFieldErrors());
    }

    if (existsByCpf(userToPersist.getCpf())) {
      LOG.warnf("Creation failed: User with CPF %s already exists", userToPersist.getCpf());
      throw ExceptionHelper.userAlreadyExists();
    }

    User savedUser = repo.persist(userToPersist);
    LOG.infof("User created successfully. ID: %s", savedUser.getId());

    return savedUser;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public User update(UUID id, UserUpdateCommand cmd) {
    LOG.debugf("Attempting to update User ID: %s", id);

    User current = getById(id);
    User updated = UserProcessor.processUpdateInput(current, cmd.name());

    if (updated.hasFieldErrors()) {
      throw new AppValidationException(updated.getFieldErrors());
    }

    if (!updated.getCpf().equals(current.getCpf()) && existsByCpf(updated.getCpf())) {
      LOG.warnf("Update failed: User ID %s tried to use existing CPF %s", id, updated.getCpf());
      throw ExceptionHelper.userAlreadyExists();
    }

    repo.update(updated);
    LOG.infof("User updated successfully. ID: %s", id);

    return getById(id);
  }
}
