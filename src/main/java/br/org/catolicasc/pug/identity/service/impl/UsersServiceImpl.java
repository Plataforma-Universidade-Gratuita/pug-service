package br.org.catolicasc.pug.identity.service.impl;

import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.identity.domain.UserRepository;
import br.org.catolicasc.pug.identity.domain.vos.Cpf;
import br.org.catolicasc.pug.identity.service.UsersService;
import br.org.catolicasc.pug.identity.service.dtos.UserCreateCommand;
import br.org.catolicasc.pug.identity.service.dtos.UserUpdateCommand;
import br.org.catolicasc.pug.identity.service.utils.ExceptionHelper;
import br.org.catolicasc.pug.identity.service.utils.UserProcessor;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import br.org.catolicasc.pug.shared.infra.audit.AuditPublisher;
import br.org.catolicasc.pug.shared.utils.CollectionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

/**
 * Implementation of the {@link UsersService} command interface.
 *
 * <p>This application-scoped service acts as the orchestrator for user state mutations. It manages
 * transaction boundaries, invokes pure domain logic via {@link UserProcessor}, enforces
 * cross-cutting business rules (e.g., CPF uniqueness), and coordinates with the underlying {@link
 * UserRepository}.
 */
@ApplicationScoped
public class UsersServiceImpl implements UsersService {

  private static final Logger LOG = Logger.getLogger(UsersServiceImpl.class);

  @Inject AuditPublisher auditPublisher;

  @Inject UserRepository repo;

  /** {@inheritDoc} */
  @Transactional
  @Override
  public boolean delete(UUID id) {
    LOG.debugf("Attempting to delete User ID: %s", id);

    boolean deleted = repo.deleteById(id);

    if (deleted) {
      LOG.infof("User deleted successfully. ID: %s", id);
      auditPublisher.fireDelete(User.class.getName(), id);
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
    return repo.existsByCpf(cpf.getValue());
  }

  /** {@inheritDoc} */
  @Override
  public User getByCpf(Cpf cpf) {
    User user =
        repo.findOptionalByCpf(cpf.getValue())
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
  @Override
  public List<User> listByCpfs(List<String> cpfs) {
    if (CollectionUtils.isEmpty(cpfs)) {
      return List.of();
    }
    return repo.listByCpfs(cpfs);
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

    auditPublisher.fireCreate(User.class.getName(), savedUser.getId());
    return savedUser;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public List<User> saveInBulk(List<UserCreateCommand> cmds) {
    if (CollectionUtils.isEmpty(cmds)) {
      return List.of();
    }
    LOG.debugf("Attempting to bulk create %d Users", cmds.size());

    List<String> cpfs = cmds.stream().map(UserCreateCommand::cpfString).toList();
    long uniqueCount = cpfs.stream().distinct().count();

    if (uniqueCount < cmds.size() || repo.existsAnyByCpfs(cpfs)) {
      LOG.warn("Bulk creation failed: Duplicate CPFs detected in payload or database");
      throw ExceptionHelper.userAlreadyExists();
    }

    List<User> usersToPersist = UserProcessor.processBulkCreateInput(cmds);

    List<User> savedUsers = repo.persistAll(usersToPersist);
    LOG.infof("Successfully bulk created %d Users", savedUsers.size());
    return savedUsers;
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

    auditPublisher.fireUpdate(User.class.getName(), id, current, updated);
    return getById(id);
  }
}
