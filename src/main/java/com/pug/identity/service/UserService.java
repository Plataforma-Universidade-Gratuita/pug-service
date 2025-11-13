package com.pug.identity.service;

import com.pug.identity.domain.User;
import com.pug.identity.domain.UserRepository;
import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.service.dtos.CreateOrUpdateUserCommand;
import com.pug.shared.domain.enums.DeleteKeys;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ReferencedEntityException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.time.TimeProvider;
import com.pug.shared.utils.CollectionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Service for managing users table. */
@ApplicationScoped
public class UserService {

  @Inject UserRepository repo;
  @Inject TimeProvider time;
  @Inject AccountService accountService;

  /**
   * Creates and saves a new User with the given CPF and name.
   *
   * @param cmd the command containing the data to create the new User
   * @return the saved User entity
   * @throws DuplicateResourceException if a user with the same CPF already exists
   */
  @Transactional
  public User save(CreateOrUpdateUserCommand cmd) {
    if (existsByCpf(cmd.cpf())) {
      throw new DuplicateResourceException(
          IdentityErrorCodes.USER_ALREADY_EXISTS, Map.of("cpf", cmd.cpf()));
    }
    var user = User.createNew(cmd.cpf(), cmd.name(), time);
    return repo.persist(user);
  }

  /**
   * Saves multiple User entities.
   *
   * @param cmds the iterable of CreateNewUserCommand containing data to create new Users
   * @return a list of saved User entities
   * @throws DuplicateResourceException if any user CPF is duplicated in the input or already exists
   */
  @Transactional
  public List<User> saveAll(Iterable<CreateOrUpdateUserCommand> cmds) {
    if (CollectionUtils.isEmpty(cmds)) {
      return List.of();
    }

    var seen = new HashSet<String>();
    var dupCpfs = new HashSet<String>();
    for (var c : cmds) {
      var cpf = c.cpf().toString();
      if (!seen.add(cpf)) dupCpfs.add(cpf);
    }
    if (!dupCpfs.isEmpty()) {
      throw new DuplicateResourceException(
          IdentityErrorCodes.USER_ALREADY_EXISTS, Map.of("cpfs", dupCpfs));
    }

    var cpfs = CollectionUtils.toStream(cmds).map(c -> c.cpf().toString()).distinct().toList();
    if (existsAnyByCpfIn(cpfs)) {
      throw new DuplicateResourceException(IdentityErrorCodes.USER_ALREADY_EXISTS);
    }

    List<User> users =
        CollectionUtils.toStream(cmds).map(c -> User.createNew(c.cpf(), c.name(), time)).toList();

    return repo.persistAll(users);
  }

  /**
   * Updates an existing User with the given ID using the provided data.
   *
   * @param id the UUID of the user to update
   * @param cmd the command containing the data to update the User
   * @return the updated User entity
   * @throws ResourceNotFoundException if the user with the given ID does not exist
   * @throws DuplicateResourceException if a user with the updated CPF already exists
   */
  @Transactional
  public User update(UUID id, CreateOrUpdateUserCommand cmd) {
    var current = getById(id);

    Cpf cpf;
    if (cmd.cpf() != null) {
      if (!cmd.cpf().equals(current.getCpf()) && existsByCpf(cmd.cpf())) {
        throw new DuplicateResourceException(
            IdentityErrorCodes.USER_ALREADY_EXISTS, Map.of("cpf", cmd.cpf()));
      }
      cpf = cmd.cpf();
    } else {
      cpf = current.getCpf();
    }

    String name = cmd.name() != null ? cmd.name() : current.getName();
    var updated = current.changeName(name).changeCpf(cpf);
    repo.update(updated);

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
   */
  public List<User> listAll() {
    return repo.listAllUsers();
  }

  /**
   * Retrieves a User by its ID.
   *
   * @param id the UUID of the user
   * @return the User entity
   * @throws ResourceNotFoundException if the user with the given ID does not exist
   */
  public User getById(UUID id) {
    return repo.findOptionalById(id)
        .orElseThrow(
            () ->
                new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND, Map.of("id", id)));
  }

  /**
   * Retrieves a User by its CPF.
   *
   * @param cpf the CPF of the user
   * @return the User entity
   * @throws ResourceNotFoundException if the user with the given CPF does not exist
   */
  public User getByCpf(Cpf cpf) {
    return repo.findOptionalByCpf(cpf.toString())
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    IdentityErrorCodes.USER_NOT_FOUND, Map.of("cpf", cpf)));
  }

  /**
   * Retrieves all Users by their CPFs.
   *
   * @param cpfs an iterable of CPFs
   * @return a list of User entities
   */
  public List<User> getAllByCpf(Iterable<Cpf> cpfs) {
    return repo.listByCpfs(CollectionUtils.toStream(cpfs).map(Cpf::toString).toList());
  }

  /**
   * Checks if a user exists by CPF.
   *
   * @param cpf the CPF to check
   * @return true if a user with the given CPF exists, false otherwise
   */
  public boolean existsByCpf(Cpf cpf) {
    return repo.existsByCpf(cpf.toString());
  }

  /**
   * Checks if any user exists with the given CPFs.
   *
   * @param cpfs the list of CPFs to check
   * @return true if any user with the given CPFs exists, false otherwise
   */
  public boolean existsAnyByCpfIn(List<String> cpfs) {
    return repo.existsAnyByCpfIn(cpfs);
  }
}
