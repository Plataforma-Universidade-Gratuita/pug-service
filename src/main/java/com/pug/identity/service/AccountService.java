package com.pug.identity.service;

import com.pug.academic.service.impl.StudentService;
import com.pug.identity.domain.Account;
import com.pug.identity.domain.AccountRepository;
import com.pug.identity.domain.User;
import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.partner.service.StaffService;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ReferencedEntityException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.domain.vos.Email;
import com.pug.identity.service.dtos.AccountCreateCommand;
import com.pug.identity.service.dtos.AccountUpdateCommand;
import com.pug.identity.service.dtos.UserCreateOrUpdateCommand;
import com.pug.shared.domain.enums.AccountType;
import com.pug.shared.domain.enums.DeleteKeys;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.time.TimeProvider;
import com.pug.shared.utils.CollectionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing accounts.
 */
@ApplicationScoped
public class AccountService {

  private static final Logger LOG = Logger.getLogger(AccountService.class);

  @Inject
  AccountRepository repo;
  @Inject
  TimeProvider time;
  @Inject
  UserService userService;
  @Inject
  AdminService adminService;
  @Inject
  StaffService staffService;
  @Inject
  StudentService studentService;

  /**
   * Helper method to process DTO input and build Account domain object (or update existing),
   * collecting all validation problems.
   *
   * @param emailString     The email string from DTO.
   * @param accountType     The account type from DTO.
   * @param passwordHash    The password hash from DTO.
   * @param userId          The user ID associated with the account.
   * @param existingAccount Optional existing account for updates (null for creation).
   * @param problems        List to collect AppValidationException.Problem instances.
   * @return The constructed or updated Account domain object if no problems, or null if problems occurred.
   */
  private Account processAccountInput(
          String emailString,
          AccountType accountType,
          String passwordHash,
          UUID userId,
          Account existingAccount,
          List<AppValidationException.Problem> problems) {

    Email emailVO = null;
    try {
      if (emailString != null && !emailString.isBlank()) {
        emailVO = new Email(emailString);
      }
    } catch (AppValidationException e) {
      problems.addAll(e.getProblems());
    }

    Account resultAccount = null;
    try {
      if (existingAccount == null) {
        resultAccount =
                Account.createNew(userId, emailVO, accountType, passwordHash, time);
      } else {
        Email effectiveEmail = (emailVO != null) ? emailVO : existingAccount.getEmail();
        String effectivePasswordHash = (passwordHash != null) ? passwordHash : existingAccount.getPasswordHash();

        resultAccount = existingAccount.changeEmail(effectiveEmail).setPasswordHash(effectivePasswordHash);
      }
    } catch (AppValidationException e) {
      problems.addAll(e.getProblems());
    }
    return resultAccount;
  }

  /**
   * Creates and saves a new Account.
   *
   * <p>If the associated {@link User} does not exist, it will be created.
   *
   * @param cmd the command containing the data to create the new Account.
   * @return the saved Account.
   * @throws DuplicateResourceException if an account with the given email already exists.
   * @throws AppValidationException     if input validation fails (e.g., blank email, invalid CPF).
   */
  @Transactional
  public Account save(AccountCreateCommand cmd) {
    List<AppValidationException.Problem> problems = new ArrayList<>();
    UUID userId = null;
    User user = null;
    Cpf cpfVO = null;

    try {
      if (cmd.userCommand().cpfString() != null && !cmd.userCommand().cpfString().isBlank()) {
        cpfVO = new Cpf(cmd.userCommand().cpfString());
      }
    } catch (AppValidationException e) {
      problems.addAll(e.getProblems());
    }

    if (problems.isEmpty()) {
      if (cpfVO != null && userService.existsByCpf(cpfVO)) {
        user = userService.getByCpf(cpfVO);
        userId = user.getId();
      } else {
        try {
          user = userService.save(new UserCreateOrUpdateCommand(cmd.userCommand().cpfString(), cmd.userCommand().name()));
          userId = user.getId();
        } catch (AppValidationException e) {
          problems.addAll(e.getProblems());
        }
      }
    }

    Account accountToPersist = processAccountInput(
            cmd.emailString(), cmd.type(), cmd.passwordHash(), userId, null, problems);

    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }

    if (existsByEmail(accountToPersist.getEmail())) {
      throw new DuplicateResourceException(
              IdentityErrorCodes.ACCOUNT_ALREADY_EXISTS, Map.of("email", accountToPersist.getEmail().toString()));
    }

    return repo.persist(accountToPersist);
  }

  /**
   * Creates and saves multiple new Accounts.
   *
   * <p>If the associated {@link User} entities do not exist, they will be created.
   *
   * @param cmds the commands containing the data to create the new Accounts.
   * @return the list of saved Accounts.
   * @throws DuplicateResourceException if any account with the given emails already exists or if
   *                                    there are duplicate emails in the input commands.
   * @throws AppValidationException     if input validation fails for any account or user in the bulk.
   */
  @Transactional
  public List<Account> saveAll(Iterable<AccountCreateCommand> cmds) {
    if (CollectionUtils.isEmpty(cmds)) {
      return List.of();
    }

    List<AppValidationException.Problem> allCollectedProblems = new ArrayList<>();
    List<Account> accountsToPersist = new ArrayList<>();
    Set<String> processedEmails = new HashSet<>();
    Map<String, UserCreateOrUpdateCommand> userCommandsByCpfString = new LinkedHashMap<>();

    List<String> rawCpfStringsFromCmds = CollectionUtils.toStream(cmds)
            .map(c -> c.userCommand().cpfString())
            .filter(Objects::nonNull)
            .toList();

    Map<Cpf, UUID> userIdsByCpfVO = userService.processUsersForAccounts(rawCpfStringsFromCmds, allCollectedProblems);

    for (AccountCreateCommand cmd : cmds) {
      List<AppValidationException.Problem> currentAccountProblems = new ArrayList<>();

      Email emailVO = null;
      try {
        if (cmd.emailString() != null && !cmd.emailString().isBlank()) {
          emailVO = new Email(cmd.emailString());
        }
      } catch (AppValidationException e) {
        currentAccountProblems.addAll(e.getProblems());
      }

      if (emailVO != null) {
        if (!processedEmails.add(emailVO.toString())) {
          currentAccountProblems.add(new AppValidationException.Problem(IdentityErrorCodes.ACCOUNT_ALREADY_EXISTS, "email"));
        }
      }

      UUID userIdForAccount = null;
      Cpf currentCpfVO = null;
      try {
        if (cmd.userCommand().cpfString() != null && !cmd.userCommand().cpfString().isBlank()) {
          currentCpfVO = new Cpf(cmd.userCommand().cpfString());
        }
      } catch (AppValidationException e) {
        // Problems already added during userService.processUsersForAccounts, ignore here.
      }

      if (currentCpfVO != null && userIdsByCpfVO.containsKey(currentCpfVO)) {
        userIdForAccount = userIdsByCpfVO.get(currentCpfVO);
      } else if (currentCpfVO != null) {
        currentAccountProblems.add(new AppValidationException.Problem(IdentityErrorCodes.INVALID_USER_BLANK, "userCommand.cpfString"));
      }

      Account account = processAccountInput(
              cmd.emailString(), cmd.type(), cmd.passwordHash(), userIdForAccount, null, currentAccountProblems);

      if (!currentAccountProblems.isEmpty()) {
        allCollectedProblems.addAll(currentAccountProblems);
      } else {
        accountsToPersist.add(account);
      }
    }

    if (!allCollectedProblems.isEmpty()) {
      throw new AppValidationException(allCollectedProblems);
    }

    List<String> emailsToPersist = accountsToPersist.stream()
            .map(a -> a.getEmail().toString())
            .toList();

    if (repo.existsAnyByEmailIn(emailsToPersist)) {
      throw new DuplicateResourceException(IdentityErrorCodes.ACCOUNT_ALREADY_EXISTS);
    }

    return repo.persistAll(accountsToPersist);
  }

  /**
   * Updates an existing Account with the given ID using the provided data.
   *
   * @param id  the UUID of the Account to be updated.
   * @param cmd the command containing the data to update the Account.
   * @return the updated Account entity
   * @throws ResourceNotFoundException  if the account with the given ID does not exist (or data is corrupted in DB).
   * @throws DuplicateResourceException if an account with the updated email already exists.
   * @throws AppValidationException     if input validation fails for account or user data.
   */
  @Transactional
  public Account update(UUID id, AccountUpdateCommand cmd) {
    Account current = getById(id);

    List<AppValidationException.Problem> problems = new ArrayList<>();

    if (cmd.userCommand() != null) {
      try {
        userService.update(current.getUserId(), cmd.userCommand());
      } catch (AppValidationException e) {
        problems.addAll(e.getProblems());
      }
    }

    Account accountToUpdate = processAccountInput(
            cmd.emailString(), current.getAccountType(), cmd.passwordHash(),
            current.getUserId(), current, problems);

    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }

    if (cmd.emailString() != null) {
      try {
        Email newEmailVO = new Email(cmd.emailString());
        if (!newEmailVO.equals(current.getEmail()) && existsByEmail(newEmailVO)) {
          throw new DuplicateResourceException(
                  IdentityErrorCodes.ACCOUNT_ALREADY_EXISTS, Map.of("email", newEmailVO.toString()));
        }
      } catch (AppValidationException e) {
        problems.addAll(e.getProblems());
        throw new AppValidationException(problems);
      }
    }

    repo.update(accountToUpdate);
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
   *                                   Student entities.
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
   * @throws AppValidationException if any Account entity found is corrupted in the database.
   */
  public List<Account> listAll() {
    try {
      return repo.listAllAccounts();
    } catch (AppValidationException e) {
      LOG.errorf(e, "Data integrity error: Corrupted Account entity found in DB. Problems: %s",
              e.getProblems().stream().map(p -> p.code().getBundleKey() + (p.fieldName() != null ? "(" + p.fieldName() + ")" : "")).collect(Collectors.joining(", ")));
      throw new ResourceNotFoundException(IdentityErrorCodes.ACCOUNT_NOT_FOUND);
    }
  }

  /**
   * Retrieves an Account by its ID.
   *
   * @param id the UUID of the Account.
   * @return the Account entity.
   * @throws ResourceNotFoundException if the account with the given ID does not exist (or data is corrupted in DB).
   * @throws AppValidationException    if the account is found but its data is corrupted in the database.
   */
  public Account getById(UUID id) {
    try {
      return repo.findOptionalById(id)
              .orElseThrow(() -> new ResourceNotFoundException(IdentityErrorCodes.ACCOUNT_NOT_FOUND, Map.of("id", id)));
    } catch (AppValidationException e) {
      LOG.errorf(e, "Data integrity error: Account with ID %s in DB violates domain rules. Problems: %s",
              id, e.getProblems().stream().map(p -> p.code().getBundleKey() + (p.fieldName() != null ? "(" + p.fieldName() + ")" : "")).collect(Collectors.joining(", ")));
      throw new ResourceNotFoundException(IdentityErrorCodes.ACCOUNT_NOT_FOUND, Map.of("id", id));
    }
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