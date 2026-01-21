package com.pug.identity.service.impl; // Pacote alterado

import com.pug.identity.domain.IUserRepository;
import com.pug.identity.domain.User;
import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.service.IAccountService;
import com.pug.identity.service.IUserService;
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
public class UserService implements IUserService { // Implementa IUserService

  private static final Logger LOG = Logger.getLogger(UserService.class);

  @Inject
  IUserRepository repo; // Injeta a interface do repositório
  @Inject
  TimeProvider time;
  @Inject
  IAccountService accountService; // Injeta a interface do AccountService

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

        User tempUser = existingUser;
        if (cpfVO != null && !effectiveCpf.equals(tempUser.getCpf())) {
          tempUser = tempUser.changeCpf(effectiveCpf);
        }
        if (name != null && !effectiveName.equals(tempUser.getName())) {
          tempUser = tempUser.changeName(effectiveName);
        }
        resultUser = tempUser;
      }
    } catch (AppValidationException e) {
      problems.addAll(e.getProblems());
    }
    return resultUser;
  }

  @Transactional
  @Override
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

  @Transactional
  @Override
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

  @Transactional
  @Override
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

  @Transactional
  @Override
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

  @Override
  public List<User> listAll() {
    try {
      return repo.listAllUsers();
    } catch (AppValidationException e) {
      LOG.errorf(e, "Data integrity error: Corrupted User entity found in DB. Problems: %s",
              e.getProblems().stream().map(p -> p.code().getBundleKey() + (p.fieldName() != null ? "(" + p.fieldName() + ")" : "")).collect(Collectors.joining(", ")));
      throw new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND);
    }
  }

  @Override
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

  @Override
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

  @Override
  public List<User> getAllByCpf(Iterable<Cpf> cpfs) {
    try {
      return repo.listByCpfs(CollectionUtils.toStream(cpfs).map(Cpf::toString).toList());
    } catch (AppValidationException e) {
      LOG.errorf(e, "Data integrity error: Corrupted User entity found in DB while listing by CPFs. Problems: %s",
              e.getProblems().stream().map(p -> p.code().getBundleKey() + (p.fieldName() != null ? "(" + p.fieldName() + ")" : "")).collect(Collectors.joining(", ")));
      throw new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND);
    }
  }

  @Override
  public boolean existsByCpf(Cpf cpf) {
    if (cpf == null) {
      return false;
    }
    return repo.existsByCpf(cpf.toString());
  }

  @Override
  public boolean existsAnyByCpfIn(List<String> cpfs) {
    return repo.existsAnyByCpfIn(cpfs);
  }

  @Override
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