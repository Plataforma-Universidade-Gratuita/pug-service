package com.pug.identity.service;

import com.pug.identity.domain.User;
import com.pug.identity.domain.UserRepository;
import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.domain.vos.Cpf;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ReferencedEntityException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.time.TimeProvider;
import com.pug.shared.utils.CollectionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
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
   * @param cpf the CPF of the user
   * @param name the name of the user
   * @return the saved User entity
   * @throws DuplicateResourceException if a user with the same CPF already exists
   */
  @Transactional
  public User save(Cpf cpf, String name) {
    existsByCpf(cpf);
    var user = User.createNew(cpf, name, time);
    return repo.persist(user);
  }

  /**
   * Saves multiple User entities.
   *
   * @param users an iterable of User entities to be saved
   * @return a list of saved User entities
   * @throws DuplicateResourceException if any user CPF is duplicated in the input or already
   *     exists
   */
  @Transactional
  public List<User> saveAll(Iterable<User> users) {
    existsAnyByCpfIn(CollectionUtils.toStream(users).map(u -> u.getCpf().toString()).toList());
    List<User> normalized =
        CollectionUtils.toStream(users)
            .map(
                u ->
                    u.getCreatedAt() == null
                        ? u.toBuilder().createdAt(OffsetDateTime.now(time.clock())).build()
                        : u)
            .toList();
    return repo.persistAll(normalized);
  }

  /**
   * Updates an existing User with the given ID using the provided data.
   *
   * @param id the UUID of the user to be updated
   * @param data the User entity containing updated data
   * @return the updated User entity
   * @throws ResourceNotFoundException if the user with the given ID does not exist
   * @throws DuplicateResourceException if a user with the updated CPF already exists
   */
  @Transactional
  public User update(UUID id, User data) {
    var current = getById(id);

    if (!data.getCpf().equals(current.getCpf())) {
        existsByCpf(data.getCpf());
    }

    var updated = current.changeName(data.getName()).changeCpf(data.getCpf());
    repo.update(updated);

    return getById(id);
  }

  /**
   * Deletes users by their IDs.
   *
   * @param ids the IDs of the users to delete
   * @return the number of deleted users
   * @throws ReferencedEntityException if any user is still referenced by an account
   */
  @Transactional
  public long deleteAll(Iterable<UUID> ids) {
    if (ids == null || !ids.iterator().hasNext()) {
        return 0L;
    }
    if (accountService.existsByUserIdIn(ids)) {
        throw new ReferencedEntityException(IdentityErrorCodes.USER_STILL_REFERENCED);
    }
    return repo.deleteByIds(ids);
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
        .orElseThrow(() -> new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND, Map.of("id", id)));
  }

  /**
   * Checks if a user with the given CPF already exists.
   *
   * @param cpf the CPF to check
   * @throws DuplicateResourceException if a user with the given CPF already exists
   */
  public void existsByCpf(Cpf cpf) {
    String code = cpf.toString();
    if (repo.existsByCpf(code)) {
        throw new DuplicateResourceException(
                IdentityErrorCodes.USER_ALREADY_EXISTS, Map.of("cpf", code));
    }
  }

  /**
   * Checks if any user with the given CPFs already exists.
   *
   * @param cpfs the list of CPFs to check
   * @throws DuplicateResourceException if any user with the given CPFs already exists
   */
  public void existsAnyByCpfIn(List<String> cpfs) {
    if (repo.existsAnyByCpfIn(cpfs)) {
        throw new DuplicateResourceException(IdentityErrorCodes.USER_ALREADY_EXISTS);
    }
  }
}
