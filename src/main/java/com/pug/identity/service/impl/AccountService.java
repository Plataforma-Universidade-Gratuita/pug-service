package com.pug.identity.service.impl;

import com.pug.academic.service.IStudentService;
import com.pug.identity.domain.Account;
import com.pug.identity.domain.IAccountRepository;
import com.pug.identity.domain.User;
import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.service.AccountProcessor;
import com.pug.identity.service.IAccountService;
import com.pug.identity.service.IAdminService;
import com.pug.identity.service.IUserService;
import com.pug.identity.service.dtos.AccountCreateCommand;
import com.pug.identity.service.dtos.AccountUpdateCommand;
import com.pug.partner.service.IStaffService;
import com.pug.shared.domain.enums.DeleteKeys;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ReferencedEntityException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.time.TimeProvider;
import com.pug.shared.utils.CollectionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.jboss.logging.Logger;

@ApplicationScoped
public class AccountService implements IAccountService {

  private static final Logger LOG = Logger.getLogger(AccountService.class);

  @Inject IAccountRepository repo;
  @Inject TimeProvider time;
  @Inject IUserService userService;
  @Inject IAdminService adminService;
  @Inject IStaffService staffService;
  @Inject IStudentService studentService;

  @Transactional
  @Override
  public Account save(AccountCreateCommand cmd) {
    UUID userId;

    Cpf cpf = Cpf.factory(cmd.userCommand().cpfString());

    if (userService.existsByCpf(cpf)) {
      userId = userService.getByCpf(cpf.toString()).getId();
    } else {
      User newUser = userService.save(cmd.userCommand());
      userId = newUser.getId();
    }

    Account account =
        AccountProcessor.processCreateInput(
            userId,
            cmd.emailString(),
            (cmd.type() != null ? cmd.type().name() : null),
            cmd.passwordHash(),
            time);

    if (account.hasErrors()) {
      throw new AppValidationException(account.getProblems());
    }

    if (existsByEmail(account.getEmail().toString())) {
      throw new DuplicateResourceException(
          IdentityErrorCodes.ACCOUNT_ALREADY_EXISTS,
          Map.of("email", account.getEmail().toString()));
    }

    return repo.persist(account);
  }

  @Transactional
  @Override
  public List<Account> saveAll(Iterable<AccountCreateCommand> cmds) {
    if (CollectionUtils.isEmpty(cmds)) {
      return List.of();
    }

    List<AppValidationException.Problem> allProblems = new ArrayList<>();
    List<Account> accountsToPersist = new ArrayList<>();
    Set<String> emailsInPayload = new HashSet<>();

    for (AccountCreateCommand cmd : cmds) {
      UUID userId = null;
      try {
        Cpf cpf = Cpf.factory(cmd.userCommand().cpfString());
        if (userService.existsByCpf(cpf)) {
          userId = userService.getByCpf(cpf.toString()).getId();
        } else {
          User newUser = userService.save(cmd.userCommand());
          userId = newUser.getId();
        }
      } catch (AppValidationException e) {
        allProblems.addAll(e.getProblems());
        continue;
      } catch (DuplicateResourceException e) {
        userId = userService.getByCpf(cmd.userCommand().cpfString()).getId();
      }

      if (userId == null) continue;

      Account account =
          AccountProcessor.processCreateInput(
              userId,
              cmd.emailString(),
              (cmd.type() != null ? cmd.type().name() : null),
              cmd.passwordHash(),
              time);

      if (account.hasErrors()) {
        allProblems.addAll(account.getProblems());
      } else {
        if (!emailsInPayload.add(account.getEmail().toString())) {
          allProblems.add(
              new AppValidationException.Problem(IdentityErrorCodes.ACCOUNT_ALREADY_EXISTS));
        } else {
          accountsToPersist.add(account);
        }
      }
    }

    if (!allProblems.isEmpty()) {
      throw new AppValidationException(allProblems);
    }

    List<String> emailsToCheck =
        accountsToPersist.stream().map(a -> a.getEmail().toString()).toList();

    if (repo.existsAnyByEmailIn(emailsToCheck)) {
      throw new DuplicateResourceException(IdentityErrorCodes.ACCOUNT_ALREADY_EXISTS);
    }

    return repo.persistAll(accountsToPersist);
  }

  @Transactional
  @Override
  public Account update(UUID id, AccountUpdateCommand cmd) {
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
      throw new DuplicateResourceException(
          IdentityErrorCodes.ACCOUNT_ALREADY_EXISTS,
          Map.of("email", updated.getEmail().toString()));
    }

    repo.update(updated);
    return getById(id);
  }

  @Transactional
  @Override
  public Map<DeleteKeys, Long> deleteAll(Iterable<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return Map.of(DeleteKeys.ACCOUNTS, 0L, DeleteKeys.USERS, 0L);
    }

    if (adminService.existsAnyByAccountIdIn(ids)) {
      throw new ReferencedEntityException(IdentityErrorCodes.ACCOUNT_STILL_REFERENCED_BY_ADMIN);
    }
    if (staffService.existsAnyByAccountIdIn(ids)) {
      throw new ReferencedEntityException(IdentityErrorCodes.ACCOUNT_STILL_REFERENCED_BY_STAFF);
    }
    if (studentService.existsAnyByAccountIdIn(ids)) {
      throw new ReferencedEntityException(IdentityErrorCodes.ACCOUNT_STILL_REFERENCED_BY_STUDENT);
    }

    Set<UUID> userIdsAssociated = new HashSet<>(repo.listAllAccountUserIdsByIds(ids));
    Set<UUID> userIdsWithOtherAccounts =
        new HashSet<>(repo.findUserIdsWithAccountsExcluding(ids, userIdsAssociated));

    Set<UUID> userIdsToDelete = new HashSet<>(userIdsAssociated);
    userIdsToDelete.removeAll(userIdsWithOtherAccounts);

    long deletedAccounts = repo.deleteByIds(ids);
    long deletedUsers = 0L;

    if (!userIdsToDelete.isEmpty()) {
      Map<DeleteKeys, Long> userRes = userService.deleteAll(userIdsToDelete);
      deletedUsers = userRes.getOrDefault(DeleteKeys.USERS, 0L);
    }

    return Map.of(DeleteKeys.ACCOUNTS, deletedAccounts, DeleteKeys.USERS, deletedUsers);
  }

  @Override
  public List<Account> listAll() {
    List<Account> accounts = repo.listAllAccounts();
    for (Account account : accounts) {
      if (account.hasErrors()) {
        LOG.errorf(
            "Corrupted Account found. ID: %s. Problems: %s",
            account.getId(), account.getProblemsSummary());
        throw new ResourceNotFoundException(IdentityErrorCodes.ACCOUNT_NOT_FOUND);
      }
    }
    return accounts;
  }

  @Override
  public Account getById(UUID id) {
    Account account =
        repo.findOptionalById(id)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        IdentityErrorCodes.ACCOUNT_NOT_FOUND, Map.of("id", id)));

    if (account.hasErrors()) {
      LOG.errorf("Corrupted Account found. ID: %s. Problems: %s", id, account.getProblemsSummary());
      throw new ResourceNotFoundException(IdentityErrorCodes.ACCOUNT_NOT_FOUND, Map.of("id", id));
    }
    return account;
  }

  @Override
  public boolean existsByUserIdIn(Iterable<UUID> userIds) {
    return repo.existsAnyByUserIdIn(userIds);
  }

  @Override
  public boolean existsByEmail(String email) {
    if (email == null) return false;
    return repo.existsByEmail(email);
  }

  @Override
  public boolean existsAnyByEmailIn(Iterable<String> emails) {
    if (CollectionUtils.isEmpty(emails)) {
      return false;
    }
    return repo.existsAnyByEmailIn(emails);
  }
}
