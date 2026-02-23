package com.pug.identity.service.impl;

import com.pug.identity.domain.Account;
import com.pug.identity.domain.AccountRepository;
import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.service.AccountService;
import com.pug.identity.service.UserService;
import com.pug.identity.service.dtos.AccountCreateCommand;
import com.pug.identity.service.dtos.AccountUpdateCommand;
import com.pug.identity.service.utils.AccountProcessor;
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

/** Service class for managing Account entities. */
@ApplicationScoped
public class AccountServiceImpl implements AccountService {

  private static final Logger LOG = Logger.getLogger(AccountServiceImpl.class);

  @Inject AccountRepository repo;

  @Inject UserService userService;

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

    if (account.hasErrors()) {
      throw new AppValidationException(account.getProblems());
    }

    if (existsByEmail(account.getEmail().toString())) {
      LOG.warnf("Creation failed: Account with email %s already exists", account.getEmail());
      throw new DuplicateResourceException(
          IdentityErrorCodes.ACCOUNT_ALREADY_EXISTS, "email", account.getEmail().toString());
    }

    Account savedAccount = repo.persist(account);
    LOG.infof("Account created successfully. ID: %s", savedAccount.getId());
    return savedAccount;
  }

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

    if (updated.hasErrors()) {
      throw new AppValidationException(updated.getProblems());
    }

    if (!updated.getEmail().equals(current.getEmail())
        && existsByEmail(updated.getEmail().toString())) {
      LOG.warnf(
          "Update failed: Account ID %s tried to use existing email %s", id, updated.getEmail());
      throw new DuplicateResourceException(
          IdentityErrorCodes.ACCOUNT_ALREADY_EXISTS, "email", updated.getEmail().toString());
    }

    repo.update(updated);
    LOG.infof("Account updated successfully. ID: %s", id);
    return getById(id);
  }

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

  @Override
  public List<Account> listAll() {
    LOG.debug("Listing all accounts");
    List<Account> accounts = repo.listAllAccounts();

    return accounts.stream()
        .filter(
            account -> {
              if (account.hasErrors()) {
                LOG.errorf(
                    "DATA CORRUPTION DETECTED: Account %s violates domain rules: %s",
                    account.getId(), account.getProblemsSummary());
                return false;
              }
              return true;
            })
        .toList();
  }

  @Override
  public Account getById(UUID id) {
    Account account =
        repo.findOptionalById(id)
            .orElseThrow(
                () -> {
                  LOG.debugf("Account lookup failed: ID %s not found", id);
                  return new ResourceNotFoundException(
                      IdentityErrorCodes.ACCOUNT_NOT_FOUND, "id", id.toString());
                });

    if (account.hasErrors()) {
      LOG.errorf(
          "DATA CORRUPTION DETECTED: Account %s violates domain rules: %s",
          id, account.getProblemsSummary());
      throw new ResourceNotFoundException(
          IdentityErrorCodes.ACCOUNT_NOT_FOUND, "id", id.toString());
    }
    return account;
  }

  /* --------------- INTERNAL HELPER METHODS --------------- */

  /**
   * Checks if an Account with the given email already exists.
   *
   * @param email the email to check for existence.
   * @return true if an Account with the email exists, false otherwise.
   */
  private boolean existsByEmail(String email) {
    if (email == null) {
      return false;
    }
    return repo.existsByEmail(email);
  }
}
