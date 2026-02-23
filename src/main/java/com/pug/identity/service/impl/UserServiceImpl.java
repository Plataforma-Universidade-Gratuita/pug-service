package com.pug.identity.service.impl;

import com.pug.identity.domain.User;
import com.pug.identity.domain.UserRepository;
import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.service.UserService;
import com.pug.identity.service.dtos.UserCreateCommand;
import com.pug.identity.service.dtos.UserUpdateCommand;
import com.pug.identity.service.utils.UserProcessor;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.CollectionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

/**
 * Implementation of the {@link UserService} interface for managing account-related operations.
 *
 * <p>This service provides methods to create, update, delete, list, and retrieve users. It ensures
 * that all operations adhere to the domain rules and handles validation and error scenarios
 * appropriately.
 */
@ApplicationScoped
public class UserServiceImpl implements UserService {

  private static final Logger LOG = Logger.getLogger(UserServiceImpl.class);

  @Inject UserRepository repo;

  @Transactional
  @Override
  public User save(UserCreateCommand cmd) {
    LOG.debugf("Attempting to create User with name: '%s'", cmd.name());
    User userToPersist = UserProcessor.processCreateInput(cmd.cpfString(), cmd.name());

    if (userToPersist.hasErrors()) {
      throw new AppValidationException(userToPersist.getProblems());
    }

    if (existsByCpf(userToPersist.getCpf())) {
      LOG.warnf("Creation failed: User with CPF %s already exists", userToPersist.getCpf());
      throw new DuplicateResourceException(
          IdentityErrorCodes.USER_ALREADY_EXISTS, "cpf", userToPersist.getCpf().toString());
    }

    User savedUser = repo.persist(userToPersist);
    LOG.infof("User created successfully. ID: %s", savedUser.getId());

    return savedUser;
  }

  @Transactional
  @Override
  public User update(UUID id, UserUpdateCommand cmd) {
    LOG.debugf("Attempting to update User ID: %s", id);

    User current = getById(id);
    User updated = UserProcessor.processUpdateInput(current, cmd.cpfString(), cmd.name());

    if (updated.hasErrors()) {
      throw new AppValidationException(updated.getProblems());
    }

    if (!updated.getCpf().equals(current.getCpf()) && existsByCpf(updated.getCpf())) {
      LOG.warnf("Update failed: User ID %s tried to use existing CPF %s", id, updated.getCpf());
      throw new DuplicateResourceException(
          IdentityErrorCodes.USER_ALREADY_EXISTS, "cpf", updated.getCpf().toString());
    }

    repo.update(updated);
    LOG.infof("User updated successfully. ID: %s", id);

    return getById(id);
  }

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

  @Override
  public List<User> listAll() {
    LOG.debug("Listing all users");
    List<User> users = repo.listAllUsers();

    return users.stream()
        .filter(
            user -> {
              if (user.hasErrors()) {
                LOG.errorf(
                    "DATA CORRUPTION DETECTED: User %s violates domain rules: %s",
                    user.getId(), user.getProblemsSummary());
                return false;
              }
              return true;
            })
        .toList();
  }

  @Override
  public User getById(UUID id) {
    User user =
        repo.findOptionalById(id)
            .orElseThrow(
                () -> {
                  LOG.debugf("User lookup failed: ID %s not found", id);
                  return new ResourceNotFoundException(
                      IdentityErrorCodes.USER_NOT_FOUND, "id", id.toString());
                });

    if (user.hasErrors()) {
      LOG.errorf(
          "DATA CORRUPTION DETECTED: User %s violates domain rules: %s",
          id, user.getProblemsSummary());
      throw new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND, "id", id.toString());
    }

    return user;
  }

  @Override
  public User getByCpf(Cpf cpf) {
    User user =
        repo.findOptionalByCpf(cpf.toString())
            .orElseThrow(
                () -> {
                  LOG.debugf("User lookup failed: CPF %s not found", cpf);
                  return new ResourceNotFoundException(
                      IdentityErrorCodes.USER_NOT_FOUND, "cpf", cpf.toString());
                });

    if (user.hasErrors()) {
      LOG.errorf(
          "DATA CORRUPTION DETECTED: User with CPF %s violates domain rules: %s",
          cpf, user.getProblemsSummary());
      throw new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND, "cpf", cpf.toString());
    }

    return user;
  }

  @Override
  public boolean existsByCpf(Cpf cpf) {
    if (cpf == null) {
      return false;
    }
    return repo.existsByCpf(cpf.toString());
  }
}
