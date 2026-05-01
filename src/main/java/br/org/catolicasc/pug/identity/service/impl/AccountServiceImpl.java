package br.org.catolicasc.pug.identity.service.impl;

import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.AccountRepository;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.identity.domain.vos.Cpf;
import br.org.catolicasc.pug.identity.domain.vos.Email;
import br.org.catolicasc.pug.identity.service.AccountService;
import br.org.catolicasc.pug.identity.service.UserService;
import br.org.catolicasc.pug.identity.service.dtos.AccountCreateCommand;
import br.org.catolicasc.pug.identity.service.dtos.AccountUpdateCommand;
import br.org.catolicasc.pug.identity.service.dtos.UserCreateCommand;
import br.org.catolicasc.pug.identity.service.utils.AccountProcessor;
import br.org.catolicasc.pug.identity.service.utils.ExceptionHelper;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import br.org.catolicasc.pug.shared.infra.audit.AuditPublisher;
import br.org.catolicasc.pug.shared.utils.CollectionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
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

  @Inject AuditPublisher auditPublisher;

  @Inject AccountRepository repo;

  @Inject UserService userService;

  /** {@inheritDoc} */
  @Transactional
  @Override
  public Account deactivate(UUID id) {
    LOG.debugf("Attempting to deactivate Account ID: %s", id);
    Account account = getById(id);

    Account deactivated = account.deactivate();
    repo.update(deactivated);

    LOG.infof("Account deactivated successfully. ID: %s", id);

    auditPublisher.fireUpdate(Account.class.getName(), id, account, deactivated);
    return deactivated;
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
      auditPublisher.fireDelete(Account.class.getName(), id);
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

  /** {@inheritDoc} */
  @Override
  public Account getByEmail(String email) {
    Account account =
        repo.findOptionalByEmail(email)
            .orElseThrow(
                () -> {
                  LOG.debugf("Account lookup failed: Email %s not found", email);
                  return ExceptionHelper.accountNotFound();
                });

    if (account.hasFieldErrors()) {
      LOG.errorf(
          "DATA CORRUPTION DETECTED: Account with email %s violates domain rules: %s",
          email, account.getProblemsSummary());
      throw ExceptionHelper.accountNotFound();
    }
    return account;
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

    if (existsByEmail(account.getEmail().getValue())) {
      LOG.warnf("Creation failed: Account with email %s already exists", account.getEmail());
      throw ExceptionHelper.accountAlreadyExists();
    }

    Account savedAccount = repo.persist(account);
    LOG.infof("Account created successfully. ID: %s", savedAccount.getId());

    auditPublisher.fireCreate(Account.class.getName(), savedAccount.getId());
    return savedAccount;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public List<Account> saveInBulk(List<AccountCreateCommand> cmds) {
    if (CollectionUtils.isEmpty(cmds)) {
      return List.of();
    }
    LOG.debugf("Attempting to bulk create %d Accounts", cmds.size());

    List<String> emails =
        cmds.stream().map(c -> Email.factory(c.emailString()).getValue()).toList();
    long uniqueEmails = emails.stream().distinct().count();

    if (uniqueEmails < cmds.size() || repo.existsAnyByEmails(emails)) {
      LOG.warn("Bulk creation failed: Duplicate emails detected in payload or database");
      throw ExceptionHelper.accountAlreadyExists();
    }

    List<String> cpfs =
        cmds.stream()
            .map(c -> Cpf.factory(c.userCommand().cpfString()).getValue())
            .filter(Objects::nonNull)
            .toList();

    List<User> existingUsers = userService.listByCpfs(cpfs);
    Map<String, UUID> userMap =
        existingUsers.stream().collect(Collectors.toMap(u -> u.getCpf().getValue(), User::getId));

    List<UserCreateCommand> missingUserCmds =
        AccountProcessor.extractMissingUserCommands(cmds, userMap);

    if (!missingUserCmds.isEmpty()) {
      List<User> createdUsers = userService.saveInBulk(missingUserCmds);
      for (User u : createdUsers) {
        userMap.put(u.getCpf().getValue(), u.getId());
      }
    }

    List<Account> accountsToPersist = AccountProcessor.processBulkCreateInput(cmds, userMap);

    List<Account> savedAccounts = repo.persistAll(accountsToPersist);
    LOG.infof("Successfully bulk created %d Accounts", savedAccounts.size());
    return savedAccounts;
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
        AccountProcessor.processUpdateInput(
            current, cmd.emailString(), cmd.passwordHash(), cmd.active());

    if (updated.hasFieldErrors()) {
      throw new AppValidationException(updated.getFieldErrors());
    }

    if (!updated.getEmail().equals(current.getEmail())
        && existsByEmail(updated.getEmail().getValue())) {
      LOG.warnf(
          "Update failed: Account ID %s tried to use existing email %s", id, updated.getEmail());
      throw ExceptionHelper.accountAlreadyExists();
    }

    repo.update(updated);
    LOG.infof("Account updated successfully. ID: %s", id);

    auditPublisher.fireUpdate(Account.class.getName(), id, current, updated);
    return getById(id);
  }
}
