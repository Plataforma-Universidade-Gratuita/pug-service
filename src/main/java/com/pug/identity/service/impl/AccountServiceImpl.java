package com.pug.identity.service.impl;

import com.pug.identity.domain.Account;
import com.pug.identity.domain.AccountRepository;
import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.service.AccountService;
import com.pug.identity.service.UserService;
import com.pug.identity.service.dtos.AccountCreateCommand;
import com.pug.identity.service.dtos.AccountUpdateCommand;
import com.pug.identity.service.utils.AccountProcessor;
import com.pug.identity.service.utils.ExceptionHelper;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.utils.CollectionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

/**
 * Implementation of the {@link AccountService} command interface.
 *
 * <p>This application-scoped service orchestrates state mutations for authentication accounts. It
 * coordinates with the {@link UserService} to ensure that user identity records are properly
 * provisioned or pruned in tandem with account lifecycles, and relies on {@link AccountProcessor}
 * to isolate complex domain instantiation logic.
 */
@ApplicationScoped
public class AccountServiceImpl implements AccountService {

  private static final Logger LOG = Logger.getLogger(AccountServiceImpl.class);

  @Inject AccountRepository repo;

  @Inject UserService userService;

  /** {@inheritDoc} */
  @Transactional
  @Override
  public Account save(AccountCreateCommand cmd) {
    LOG.debugf("Attempting to create Account for email: '%s'", cmd.emailString());

    UUID userId;
    Cpf cpf = Cpf.factory(cmd.userCommand().cpfString());

    if (userService.existsByCpf(cpf)) {
      userId = userService.getByCpf(cpf).getId();
      LOG.debugf("Associating new Account with existing User ID: %s", userId);
    } else {
      userId = userService.save(cmd.userCommand()).getId();
      LOG.debugf("Created new User ID: %s for Account", userId);
    }

    Account account =
        AccountProcessor.processCreateInput(
            userId, cmd.emailString(), cmd.type().name(), cmd.passwordHash());

    if (account.hasFieldErrors()) {
      throw new AppValidationException(account.getFieldErrors());
    }

    if (existsByEmail(account.getEmail().toString())) {
      LOG.warnf("Creation failed: Account with email %s already exists", account.getEmail());
      throw ExceptionHelper.accountAlreadyExists();
    }

    Account savedAccount = repo.persist(account);
    LOG.infof("Account created successfully. ID: %s", savedAccount.getId());
    return savedAccount;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public Account update(UUID id, AccountUpdateCommand cmd) {
    LOG.debugf("Attempting to update Account ID: %s", id);
    Account current = getById(id);

    if (cmd.userCommand() != null) {
      userService.update(current.getUserId(), cmd.userCommand());
    }

    Account updated =
        AccountProcessor.processUpdateInput(current, cmd.emailString(), cmd.passwordHash());

    if (updated.hasFieldErrors()) {
      throw new AppValidationException(updated.getFieldErrors());
    }

    if (!updated.getEmail().equals(current.getEmail())
        && existsByEmail(updated.getEmail().toString())) {
      LOG.warnf(
          "Update failed: Account ID %s tried to use existing email %s", id, updated.getEmail());
      throw ExceptionHelper.accountAlreadyExists();
    }

    repo.update(updated);
    LOG.infof("Account updated successfully. ID: %s", id);
    return getById(id);
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public boolean delete(UUID id) {
    LOG.debugf("Attempting to delete Account ID: %s", id);
    Account account = getById(id);

    long count = repo.countAllAccountsByUserId(account.getUserId());
    boolean deleted = repo.deleteById(account.getId());

    if (deleted) {
      LOG.infof("Account deleted successfully. ID: %s", id);
      if (count <= 1) {
        LOG.infof("Auto-deleting Orphan User ID: %s", account.getUserId());
        userService.delete(account.getUserId());
      }
    }

    return deleted;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public long deleteAll(List<UUID> ids) {
    LOG.debugf("Attempting to delete multiple Accounts. IDs: %s", ids);
    List<UUID> userIds = repo.findUserIdsByIds(ids);

    long deletedCount = repo.deleteAllByIds(ids);
    if (deletedCount > 0) {
      LOG.infof("Deleted %d Accounts successfully.", deletedCount);
      var userIdsToDelete = repo.findAllOrphanUserIdsByUserIds(userIds);
      if (CollectionUtils.isNotEmpty(userIdsToDelete)) {
        LOG.infof("Auto-deleting Orphan User IDs: %s", userIdsToDelete);
        userService.deleteAll(userIdsToDelete);
      }
    }

    return deletedCount;
  }

  /** {@inheritDoc} */
  @Override
  public Account getById(UUID id) {
    Account account =
        repo.findOptionalById(id)
            .orElseThrow(
                () -> {
                  LOG.debugf("Account lookup failed: ID %s not found", id);
                  return ExceptionHelper.accountNotFound();
                });

    if (account.hasFieldErrors()) {
      LOG.errorf(
          "DATA CORRUPTION DETECTED: Account %s violates domain rules: %s",
          id, account.getProblemsSummary());
      throw ExceptionHelper.accountNotFound();
    }
    return account;
  }

  /* --------------- INTERNAL HELPER METHODS --------------- */

  /**
   * Checks if an Account with the given email already exists.
   *
   * @param email the email address to check
   * @return {@code true} if an Account with the email exists, {@code false} otherwise
   */
  private boolean existsByEmail(String email) {
    if (email == null) {
      return false;
    }
    return repo.existsByEmail(email);
  }
}
