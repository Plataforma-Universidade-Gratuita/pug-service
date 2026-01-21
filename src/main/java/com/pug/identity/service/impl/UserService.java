package com.pug.identity.service.impl;

import com.pug.identity.domain.User;
import com.pug.identity.domain.IUserRepository;
import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.service.dtos.UserCreateOrUpdateCommand;
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
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing users table.
 */
@ApplicationScoped
public class UserService {

  private static final Logger LOG = Logger.getLogger(UserService.class);

  @Inject
  IUserRepository repo;
  @Inject
  TimeProvider time;
  @Inject
  AccountService accountService;

  /**
   * Helper method to process DTO input and build User domain object (or update existing),
   * collecting all validation problems.
   *
   * @param cpfString    The CPF string from DTO.
   * @param name         The name string from DTO.
   * @param existingUser Optional existing user for updates (null for creation).
   * @param problems     List to collect AppValidationException.Problem instances.
   * @return The constructed or updated User domain object if no problems, or null if problems occurred.
   */
  private User processUserInput(
          String cpfString,
          String name,
          User existingUser,
          List<AppValidationException.Problem> problems) {

    Cpf cpfVO = null;
    try {
      if (cpfString != null && !cpfString.isBlank()) {
        cpfVO = new Cpf(cpfString);
      }
    } catch (AppValidationException e) {
      problems.addAll(e.getProblems());
    }

    User resultUser = null;
    try {
      if (existingUser == null) {
        resultUser = User.createNew(cpfVO, name, time);
      } else {
        String effectiveName = (name != null) ? name : existingUser.getName();
        Cpf effectiveCpf = (cpfVO != null) ? cpfVO : existingUser.getCpf();

        resultUser = existingUser.changeName(effectiveName).changeCpf(effectiveCpf);
      }
    } catch (AppValidationException e) {
      problems.addAll(e.getProblems());
    }
    return resultUser;
  }

  /**
   * Creates and saves a new User with the given CPF and name.
   *
   * @param cmd the command containing the data to create the new User
   * @return the saved User entity
   * @throws DuplicateResourceException if a user with the same CPF already exists
   * @throws AppValidationException     if input validation fails (e.g., blank name, invalid CPF).
   */
  @Transactional
  public User save(UserCreateOrUpdateCommand cmd) {
    List<AppValidationException.Problem> problems = new ArrayList<>();
    User userToPersist = processUserInput(cmd.cpfString(), cmd.name(), null, problems);

    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }

    if (existsByCpf(userToPersist.getCpf())) {
      throw new DuplicateResourceException(
              IdentityErrorCodes.USER_ALREADY_EXISTS, Map.of("cpf", userToPersist.getCpf().toString()));
    }
    return repo.persist(userToPersist);
  }

  /**
   * Saves multiple User entities.
   *
   * @param cmds the iterable of CreateNewUserCommand containing data to create new Users
   * @return a list of saved User entities
   * @throws DuplicateResourceException if any user CPF is duplicated in the input or already exists
   * @throws AppValidationException     if input validation fails for any user in the bulk.
   */
  @Transactional
  public List<User> saveAll(Iterable<UserCreateOrUpdateCommand> cmds) {
    if (CollectionUtils.isEmpty(cmds)) {
      return List.of();
    }

    List<AppValidationException.Problem> allCollectedProblems = new ArrayList<>();
    List<User> usersToPersist = new ArrayList<>();
    Set<String> processedCpfs = new HashSet<>();

    for (UserCreateOrUpdateCommand cmd : cmds) {
      List<AppValidationException.Problem> currentUserProblems = new ArrayList<>();
      User user = processUserInput(cmd.cpfString(), cmd.name(), null, currentUserProblems);

      if (!currentUserProblems.isEmpty()) {
        allCollectedProblems.addAll(currentUserProblems);
      } else {
        String cpfStr = user.getCpf().toString();
        if (!processedCpfs.add(cpfStr)) {
          allCollectedProblems.add(new AppValidationException.Problem(IdentityErrorCodes.USER_ALREADY_EXISTS, "cpf"));
        }
        usersToPersist.add(user);
      }
    }

    if (!allCollectedProblems.isEmpty()) {
      throw new AppValidationException(allCollectedProblems);
    }

    List<String> cpfsToPersist = usersToPersist.stream()
            .map(u -> u.getCpf().toString())
            .toList();

    if (repo.existsAnyByCpfIn(cpfsToPersist)) {
      throw new DuplicateResourceException(IdentityErrorCodes.USER_ALREADY_EXISTS);
    }

    return repo.persistAll(usersToPersist);
  }

  /**
   * Updates an existing User with the given ID using the provided data.
   *
   * @param id  the UUID of the user to update
   * @param cmd the command containing the data to update the User
   * @return the updated User entity
   * @throws ResourceNotFoundException  if the user with the given ID does not exist (or data is corrupted in DB).
   * @throws DuplicateResourceException if a user with the updated CPF already exists.
   * @throws AppValidationException     if input validation fails for user data.
   */
  @Transactional
  public User update(UUID id, UserCreateOrUpdateCommand cmd) {
    User current = getById(id);

    List<AppValidationException.Problem> problems = new ArrayList<>();

    User userToUpdate = processUserInput(
            cmd.cpfString(), cmd.name(), current, problems);

    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }

    if (cmd.cpfString() != null) {
      try {
        Cpf newCpfVO = new Cpf(cmd.cpfString());
        if (!newCpfVO.equals(current.getCpf()) && existsByCpf(newCpfVO)) {
          throw new DuplicateResourceException(
                  IdentityErrorCodes.USER_ALREADY_EXISTS, Map.of("cpf", newCpfVO.toString()));
        }
      } catch (AppValidationException e) {
        problems.addAll(e.getProblems());
        throw new AppValidationException(problems);
      }
    }

    repo.update(userToUpdate);
    return getById(id);
  }

  /**
   * Deletes users by their IDs.
   *
   * @param ids the IDs of the users to delete
   * @return a map containing the count of deleted users
   * @throws ReferencedEntityException if any user is still referenced by an account
   */
  @Transactional
  public Map<DeleteKeys, Long> deleteAll(Iterable<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return Map.of(DeleteKeys.USERS, 0L);
    }

    if (accountService.existsByUserIdIn(ids)) {
      throw new ReferencedEntityException(IdentityErrorCodes.USER_STILL_REFERENCED);
    }
    long users = repo.deleteByIds(ids);
    return Map.of(DeleteKeys.USERS, users);
  }

  /**
   * Lists all users.
   *
   * @return a list of all User entities
   * @throws AppValidationException if any User entity found is corrupted in the database.
   */
  public List<User> listAll() {
    try {
      return repo.listAllUsers();
    } catch (AppValidationException e) {
      LOG.errorf(e, "Data integrity error: Corrupted User entity found in DB. Problems: %s",
              e.getProblems().stream().map(p -> p.code().getBundleKey() + (p.fieldName() != null ? "(" + p.fieldName() + ")" : "")).collect(Collectors.joining(", ")));
      throw new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND);
    }
  }

  /**
   * Retrieves a User by its ID.
   *
   * @param id the UUID of the user
   * @return the User entity
   * @throws ResourceNotFoundException if the user with the given ID does not exist (or data is corrupted in DB).
   */
  public User getById(UUID id) {
    try {
      return repo.findOptionalById(id)
              .orElseThrow(
                      () ->
                              new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND, Map.of("id", id)));
    } catch (AppValidationException e) {
      LOG.errorf(e, "Data integrity error: User with ID %s in DB violates domain rules. Problems: %s",
              id, e.getProblems().stream().map(p -> p.code().getBundleKey() + (p.fieldName() != null ? "(" + p.fieldName() + ")" : "")).collect(Collectors.joining(", ")));
      throw new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND, Map.of("id", id));
    }
  }

  /**
   * Retrieves a User by its CPF.
   *
   * @param cpf the CPF of the user (already a validated Value Object).
   * @return the User entity
   * @throws ResourceNotFoundException if the user with the given CPF does not exist (or data is corrupted in DB).
   */
  public User getByCpf(Cpf cpf) {
    try {
      return repo.findOptionalByCpf(cpf.toString())
              .orElseThrow(
                      () ->
                              new ResourceNotFoundException(
                                      IdentityErrorCodes.USER_NOT_FOUND, Map.of("cpf", cpf.toString())));
    } catch (AppValidationException e) {
      LOG.errorf(e, "Data integrity error: User with CPF %s in DB violates domain rules. Problems: %s",
              cpf.toString(), e.getProblems().stream().map(p -> p.code().getBundleKey() + (p.fieldName() != null ? "(" + p.fieldName() + ")" : "")).collect(Collectors.joining(", ")));
      throw new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND, Map.of("cpf", cpf.toString()));
    }
  }

  /**
   * Retrieves all Users by their CPFs.
   *
   * @param cpfs an iterable of CPFs (already validated Value Objects).
   * @return a list of User entities
   * @throws AppValidationException if any User entity found is corrupted in the database.
   */
  public List<User> getAllByCpf(Iterable<Cpf> cpfs) {
    try {
      return repo.listByCpfs(CollectionUtils.toStream(cpfs).map(Cpf::toString).toList());
    } catch (AppValidationException e) {
      LOG.errorf(e, "Data integrity error: Corrupted User entity found in DB while listing by CPFs. Problems: %s",
              e.getProblems().stream().map(p -> p.code().getBundleKey() + (p.fieldName() != null ? "(" + p.fieldName() + ")" : "")).collect(Collectors.joining(", ")));
      throw new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND);
    }
  }

  /**
   * Checks if a user exists by CPF.
   *
   * @param cpf the CPF to check (already a validated Value Object).
   * @return true if a user with the given CPF exists, false otherwise.
   */
  public boolean existsByCpf(Cpf cpf) {
    if (cpf == null) {
      return false;
    }
    return repo.existsByCpf(cpf.toString());
  }

  /**
   * Checks if any user exists with the given CPFs.
   *
   * @param cpfs the list of CPFs (as strings) to check.
   * @return true if any user with the given CPFs exists, false otherwise.
   */
  public boolean existsAnyByCpfIn(List<String> cpfs) {
    return repo.existsAnyByCpfIn(cpfs);
  }

  /**
   * Helper method for AccountService to process users in bulk (find or create)
   * and collect validation problems.
   *
   * @param rawCpfStrings List of raw CPF strings from commands.
   * @param problems      List to collect AppValidationException.Problem instances.
   * @return Map of Cpf VO to User UUID for successfully processed users.
   */
  public Map<Cpf, UUID> processUsersForAccounts(List<String> rawCpfStrings, List<AppValidationException.Problem> problems) {
    Map<Cpf, UUID> userIdsByCpfVO = new HashMap<>();
    List<Cpf> validCpfVOs = new ArrayList<>();
    List<String> distinctRawCpfStrings = rawCpfStrings.stream().distinct().toList();

    for (String rawCpf : distinctRawCpfStrings) {
      try {
        if (rawCpf != null && !rawCpf.isBlank()) {
          Cpf cpfVO = new Cpf(rawCpf);
          validCpfVOs.add(cpfVO);
        }
      } catch (AppValidationException e) {
        problems.addAll(e.getProblems());
      }
    }

    if (problems.isEmpty()) {
      Map<Cpf, UUID> existingUserIds = getAllByCpf(validCpfVOs).stream()
              .collect(Collectors.toMap(User::getCpf, User::getId));
      userIdsByCpfVO.putAll(existingUserIds);

      List<UserCreateOrUpdateCommand> usersToCreateCmds = validCpfVOs.stream()
              .filter(cpfVO -> !existingUserIds.containsKey(cpfVO))
              .map(cpfVO ->
                      new UserCreateOrUpdateCommand(cpfVO.toString(), "Default User Name"))
              .toList();

      if (!usersToCreateCmds.isEmpty()) {
        try {
          List<User> createdUsers = saveAll(usersToCreateCmds);
          createdUsers.forEach(user -> userIdsByCpfVO.put(user.getCpf(), user.getId()));
        } catch (AppValidationException e) {
          problems.addAll(e.getProblems());
        } catch (DuplicateResourceException e) {
          problems.add(new AppValidationException.Problem(e.getErrorCode(), "cpf"));
        }
      }
    }
    return userIdsByCpfVO;
  }
}