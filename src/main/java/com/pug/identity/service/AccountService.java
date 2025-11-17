package com.pug.identity.service;

import com.pug.academic.service.StudentService;
import com.pug.identity.domain.Account;
import com.pug.identity.domain.AccountRepository;
import com.pug.identity.domain.User;
import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.domain.vos.Email;
import com.pug.identity.service.dtos.AccountCreateCommand;
import com.pug.identity.service.dtos.UserCreateOrUpdateCommand;
import com.pug.identity.service.dtos.AccountUpdateCommand;
import com.pug.partner.service.StaffService;
import com.pug.shared.domain.enums.DeleteKeys;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ReferencedEntityException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.time.TimeProvider;
import com.pug.shared.utils.CollectionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/** Service for managing accounts. */
@ApplicationScoped
public class AccountService {

  @Inject AccountRepository repo;
  @Inject TimeProvider time;
  @Inject UserService userService;
  @Inject AdminService adminService;
  @Inject StaffService staffService;
  @Inject StudentService studentService;

  /**
   * Creates and saves a new Account.
   *
   * <p>If the associated {@link User} does not exist, it will be created.
   *
   * @param cmd the command containing the data to create the new Account.
   * @return the saved Account.
   * @throws DuplicateResourceException if an account with the given email already exists.
   */
  @Transactional
  public Account save(AccountCreateCommand cmd) {
    if (existsByEmail(cmd.email())) {
      throw new DuplicateResourceException(
          IdentityErrorCodes.ACCOUNT_ALREADY_EXISTS, Map.of("email", cmd.email()));
    }
    UUID userId;
    if (userService.existsByCpf(cmd.userCommand().cpf())) {
      userId = userService.getByCpf(cmd.userCommand().cpf()).getId();
    } else {
      userId =
          userService
              .save(
                  new UserCreateOrUpdateCommand(cmd.userCommand().cpf(), cmd.userCommand().name()))
              .getId();
    }
    Account account = Account.createNew(userId, cmd.email(), cmd.type(), cmd.passwordHash(), time);
    return repo.persist(account);
  }

  /**
   * Creates and saves multiple new Accounts.
   *
   * <p>If the associated {@link User} entities do not exist, they will be created.
   *
   * @param cmds the commands containing the data to create the new Accounts.
   * @return the list of saved Accounts.
   * @throws DuplicateResourceException if any account with the given emails already exists or if
   *     there are duplicate emails in the input commands.
   */
  @Transactional
  public List<Account> saveAll(Iterable<AccountCreateCommand> cmds) {
    if (CollectionUtils.isEmpty(cmds)) {
      return List.of();
    }

    var duplicateEmails = new HashSet<String>();
    var seen = new HashSet<String>();
    for (var c : cmds) {
      var e = c.email().toString();
      if (!seen.add(e)) {
        duplicateEmails.add(e);
      }
    }
    if (!duplicateEmails.isEmpty()) {
      throw new DuplicateResourceException(
          IdentityErrorCodes.ACCOUNT_ALREADY_EXISTS, Map.of("emails", duplicateEmails));
    }

    var emails = CollectionUtils.toStream(cmds).map(c -> c.email().toString()).toList();
    if (existsAnyByEmailIn(emails)) {
      throw new DuplicateResourceException(IdentityErrorCodes.ACCOUNT_ALREADY_EXISTS);
    }

    var cpfs = CollectionUtils.toStream(cmds).map(cmd -> cmd.userCommand().cpf()).distinct().toList();
    Map<Cpf, UUID> existing =
        userService.getAllByCpf(cpfs).stream().collect(Collectors.toMap(User::getCpf, User::getId));
    var namesByCpf = new LinkedHashMap<Cpf, String>();
    for (var c : cmds) {
      namesByCpf.putIfAbsent(c.userCommand().cpf(), c.userCommand().name());
    }
    var missingCpfs = cpfs.stream().filter(c -> !existing.containsKey(c)).toList();
    List<UserCreateOrUpdateCommand> toCreate =
        missingCpfs.stream()
            .map(cpf -> new UserCreateOrUpdateCommand(cpf, namesByCpf.get(cpf)))
            .toList();
    List<User> createdUsers = userService.saveAll(toCreate);
    Map<Cpf, UUID> userIdsByCpf = new HashMap<>(existing);
    userIdsByCpf.putAll(createdUsers.stream().collect(Collectors.toMap(User::getCpf, User::getId)));

    var accounts = new ArrayList<Account>();
    for (var c : cmds) {
      accounts.add(
          Account.createNew(
              userIdsByCpf.get(c.userCommand().cpf()),
              c.email(),
              c.type(),
              c.passwordHash(),
              time));
    }
    return repo.persistAll(accounts);
  }

  /**
   * Updates an existing Account with the given ID using the provided data.
   *
   * @param id the UUID of the Account to be updated.
   * @param cmd the command containing the data to update the Account.
   * @return the updated Account entity
   * @throws ResourceNotFoundException if the account with the given ID does not exist
   * @throws DuplicateResourceException if an account with the updated email already exists
   */
  @Transactional
  public Account update(UUID id, AccountUpdateCommand cmd) {
    Account current = getById(id);

    Email newEmail = cmd.email() != null ? cmd.email() : null;
    if (newEmail != null && !newEmail.equals(current.getEmail()) && existsByEmail(newEmail)) {
      throw new DuplicateResourceException(
          IdentityErrorCodes.USER_ALREADY_EXISTS, Map.of("email", newEmail));
    }

    if (cmd.userCommand() != null) {
      userService.update(current.getUserId(), cmd.userCommand());
    }

    String passwordHash =
        cmd.passwordHash() != null ? cmd.passwordHash() : current.getPasswordHash();
    Email email = cmd.email() != null ? cmd.email() : current.getEmail();
    Account updated = current.changeEmail(email).setPasswordHash(passwordHash);

    repo.update(updated);
    return getById(id);
  }

  /**
   * Deletes multiple Account entities by their IDs.
   *
   * <p>Also deletes associated User entities if they are not referenced elsewhere.
   *
   * @param ids the iterable of UUIDs representing the IDs of the Account entities to be deleted.
   * @return a map containing the count of deleted Accounts and Users.
   * @throws ReferencedEntityException if any account is still referenced by Admin, Staff, or
   *     Student entities.
   */
  @Transactional
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

    var toDeleteUserIds = new HashSet<>(repo.listAllAccountUserIdsByIds(ids));
    var stillReferencedUsersIds =
        new HashSet<>(repo.findUserIdsWithAccountsExcluding(ids, toDeleteUserIds));
    toDeleteUserIds.removeAll(stillReferencedUsersIds);

    long accounts = repo.deleteByIds(ids);
    long users = 0L;
    if (!toDeleteUserIds.isEmpty()) {
      users = userService.deleteAll(toDeleteUserIds).getOrDefault(DeleteKeys.USERS, 0L);
    }

    return Map.of(
        DeleteKeys.ACCOUNTS, accounts,
        DeleteKeys.USERS, users);
  }

  /**
   * Lists all Account entities.
   *
   * @return a list of all Account entities.
   */
  public List<Account> listAll() {
    return repo.listAllAccounts();
  }

  /**
   * Retrieves an Account by its ID.
   *
   * @param id the UUID of the Account.
   * @return the Account entity.
   * @throws ResourceNotFoundException if the account with the given ID does not exist.
   */
  public Account getById(UUID id) {
    return repo.findOptionalById(id)
        .orElseThrow(() -> new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND));
  }

  /**
   * Checks if any account exists with a user ID in the provided list.
   *
   * @param userIds the list of user IDs to check
   * @return true if any account exists with a user ID in the list, false otherwise
   */
  public boolean existsByUserIdIn(Iterable<UUID> userIds) {
    return repo.existsAnyByUserIdIn(userIds);
  }

  /**
   * Checks if any account exists with an email with the provided.
   *
   * @param e the emails to check
   * @return true if any account exists with the email, false otherwise
   */
  public boolean existsByEmail(Email e) {
    if (e == null) {
      return false;
    }
    return repo.existsByEmail(e.toString());
  }

  /**
   * Checks if any account exists with an email in the provided list.
   *
   * @param emails the list of emails to check
   * @return true if any account exists with an email in the list, false otherwise
   */
  public boolean existsAnyByEmailIn(Iterable<String> emails) {
    return repo.existsAnyByEmailIn(emails);
  }
}
