package com.pug.identity.service.impl;

import com.pug.identity.domain.IUserRepository;
import com.pug.identity.domain.User;
import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.service.IAccountService;
import com.pug.identity.service.IUserService;
import com.pug.identity.service.UserProcessor;
import com.pug.identity.service.dtos.UserCreateCommand;
import com.pug.identity.service.dtos.UserUpdateCommand;
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

/** Service for managing users. */
@ApplicationScoped
public class UserService implements IUserService {

  private static final Logger LOG = Logger.getLogger(UserService.class);

  @Inject IUserRepository repo;
  @Inject TimeProvider time;
  @Inject IAccountService accountService;

  @Transactional
  @Override
  public User save(UserCreateCommand cmd) {
    User userToPersist = UserProcessor.processCreateInput(cmd.cpfString(), cmd.name(), time);

    if (userToPersist.hasErrors()) {
      throw new AppValidationException(userToPersist.getProblems());
    }

    if (existsByCpf(userToPersist.getCpf())) {
      throw new DuplicateResourceException(
          IdentityErrorCodes.USER_ALREADY_EXISTS, Map.of("cpf", userToPersist.getCpf().toString()));
    }

    return repo.persist(userToPersist);
  }

  @Transactional
  @Override
  public List<User> saveAll(Iterable<UserCreateCommand> cmds) {
    if (CollectionUtils.isEmpty(cmds)) {
      return List.of();
    }

    List<AppValidationException.Problem> allCollectedProblems = new ArrayList<>();
    List<User> usersToPersist = new ArrayList<>();
    Set<String> cpfsInPayload = new HashSet<>();

    for (UserCreateCommand cmd : cmds) {
      User user = UserProcessor.processCreateInput(cmd.cpfString(), cmd.name(), time);

      if (user.hasErrors()) {
        allCollectedProblems.addAll(user.getProblems());
      } else {
        String cpfStr = user.getCpf().toString();

        if (!cpfsInPayload.add(cpfStr)) {
          allCollectedProblems.add(
              new AppValidationException.Problem(IdentityErrorCodes.USER_ALREADY_EXISTS));
        }
        usersToPersist.add(user);
      }
    }

    if (!allCollectedProblems.isEmpty()) {
      throw new AppValidationException(allCollectedProblems);
    }

    List<String> cpfsToPersist = usersToPersist.stream().map(u -> u.getCpf().toString()).toList();

    if (repo.existsAnyByCpfIn(cpfsToPersist)) {
      throw new DuplicateResourceException(IdentityErrorCodes.USER_ALREADY_EXISTS);
    }

    return repo.persistAll(usersToPersist);
  }

  @Transactional
  @Override
  public User update(UUID id, UserUpdateCommand cmd) {
    User current = getById(id);

    User updated = UserProcessor.processUpdateInput(current, cmd.cpfString(), cmd.name());

    if (updated.hasErrors()) {
      throw new AppValidationException(updated.getProblems());
    }

    if (!updated.getCpf().equals(current.getCpf()) && existsByCpf(updated.getCpf())) {
      throw new DuplicateResourceException(
          IdentityErrorCodes.USER_ALREADY_EXISTS, Map.of("cpf", updated.getCpf().toString()));
    }

    repo.update(updated);
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

    long deletedCount = repo.deleteByIds(ids);
    return Map.of(DeleteKeys.USERS, deletedCount);
  }

  @Override
  public List<User> listAll() {
    List<User> users = repo.listAllUsers();

    for (User user : users) {
      if (user.hasErrors()) {
        LOG.errorf(
            "Data integrity error: Corrupted User entity found in DB during list. ID: %s. Problems: %s",
            user.getId(), user.getProblemsSummary());
        throw new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND);
      }
    }
    return users;
  }

  @Override
  public User getById(UUID id) {
    User user =
        repo.findOptionalById(id)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        IdentityErrorCodes.USER_NOT_FOUND, Map.of("id", id)));

    if (user.hasErrors()) {
      LOG.errorf(
          "Data integrity error: User with ID %s in DB violates domain rules. Problems: %s",
          id, user.getProblemsSummary());
      throw new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND, Map.of("id", id));
    }

    return user;
  }

  @Override
  public User getByCpf(String cpfString) {
    User user =
        repo.findOptionalByCpf(cpfString)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        IdentityErrorCodes.USER_NOT_FOUND, Map.of("cpf", cpfString)));

    if (user.hasErrors()) {
      LOG.errorf(
          "Data integrity error: User with CPF %s in DB violates domain rules. Problems: %s",
          cpfString, user.getProblemsSummary());
      throw new ResourceNotFoundException(
          IdentityErrorCodes.USER_NOT_FOUND, Map.of("cpf", cpfString));
    }

    return user;
  }

  @Override
  public List<User> getAllByCpf(Iterable<Cpf> cpfs) {
    List<String> cpfStrings = CollectionUtils.toStream(cpfs).map(Cpf::toString).toList();

    List<User> users = repo.listByCpfs(cpfStrings);

    for (User user : users) {
      if (user.hasErrors()) {
        LOG.errorf(
            "Data integrity error: Corrupted User entity found in DB while listing by CPFs. ID: %s. Problems: %s",
            user.getId(), user.getProblemsSummary());
        throw new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND);
      }
    }

    return users;
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
}
