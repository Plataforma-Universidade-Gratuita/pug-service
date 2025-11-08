package com.pug.identity.service;

import com.pug.identity.domain.User;
import com.pug.identity.domain.UsersRepository;
import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.text.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/** Service for managing users. */
@ApplicationScoped
public class UsersService {

  @Inject UsersRepository repo;

  /**
   * Save a single user.
   *
   * @param user the user to save.
   * @return the saved user.
   * @throws DuplicateResourceException if a user with the same email already exists.
   */
  @Transactional
  public User save(User user) {
    String email = user.getEmail().toString();
    if (repo.existsByEmail(email)) {
      throw new DuplicateResourceException(IdentityErrorCodes.USER_ALREADY_EXISTS);
    }
    return repo.persist(user);
  }

  /**
   * Save users in bulk.
   *
   * @param users the users to save.
   * @throws DuplicateResourceException if any user with the same email already exists.
   */
  @Transactional
  public List<User> saveAll(Iterable<User> users) {
    List<String> emails = toStream(users).map(u -> u.getEmail().toString()).toList();
    if (!emails.isEmpty() && repo.existsAnyByEmailIn(emails)) {
      throw new DuplicateResourceException(IdentityErrorCodes.USER_ALREADY_EXISTS);
    }
    return repo.persistAll(users);
  }

  /**
   * Update an existing user.
   *
   * @param id the ID of the user to update.
   * @param data the new data for the user.
   * @return the updated user.
   * @throws ResourceNotFoundException if the user does not exist.
   * @throws DuplicateResourceException if a user with the same email already exists.
   */
  @Transactional
  public User update(UUID id, User data) {
    User current =
        repo.findOptionalById(id)
            .orElseThrow(() -> new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND));

    String newEmail = data.getEmail().toString();
    if (!newEmail.equalsIgnoreCase(current.getEmail().toString())) {
      repo.findOptionalByEmail(newEmail)
          .filter(found -> !found.getId().equals(id))
          .ifPresent(
              x -> {
                throw new DuplicateResourceException(IdentityErrorCodes.USER_ALREADY_EXISTS);
              });
    }

    User updated =
        current.toBuilder()
            .cpf(data.getCpf())
            .name(data.getName())
            .email(data.getEmail())
            .accountType(data.getAccountType())
            .passwordHash(data.getPasswordHash())
            .active(data.getActive())
            .build();

    repo.update(updated);
    return repo.findOptionalById(id)
        .orElseThrow(() -> new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND));
  }

  /**
   * Delete users by their IDs.
   *
   * @param ids the IDs of the users to delete.
   * @return the number of users deleted.
   */
  @Transactional
  public long deleteByIds(Iterable<UUID> ids) {
    return repo.deleteByIds(ids);
  }

  /**
   * Deactivate a user by ID.
   *
   * @param id the ID of the user to deactivate.
   * @throws ResourceNotFoundException if the user does not exist.
   */
  @Transactional
  public void deactivateById(UUID id) {
    repo.findOptionalById(id)
        .orElseThrow(() -> new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND));
    repo.deactivateById(id);
  }

  /**
   * Deactivate a user by ID.
   *
   * @param id the ID of the user to deactivate.
   * @throws ResourceNotFoundException if the user does not exist.
   */
  @Transactional
  public void activateById(UUID id) {
    repo.findOptionalById(id)
        .orElseThrow(() -> new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND));
    User u = getById(id);
    if (Boolean.FALSE.equals(u.getActive())) {
      repo.update(u.toBuilder().active(true).build());
    }
  }

  /**
   * List all users.
   *
   * @return the list of all users.
   */
  public List<User> listAll() {
    return repo.listAllUsers();
  }

  /**
   * Get a user by ID.
   *
   * @param id the ID of the user.
   * @return the user.
   * @throws ResourceNotFoundException if the user is not found.
   */
  public User getById(UUID id) {
    return repo.findOptionalById(id)
        .orElseThrow(() -> new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND));
  }

  /**
   * Get a user by email.
   *
   * @param email the email of the user.
   * @return the user.
   * @throws ResourceNotFoundException if the user is not found.
   */
  public User getByEmail(String email) {
    return repo.findOptionalByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND));
  }

  /**
   * List users by CPF.
   *
   * @param cpf the CPF to search for.
   * @return the list of users with the given CPF.
   */
  public List<User> listByCpf(String cpf) {
    return repo.listByCpf(cpf);
  }

  /**
   * Search users by name query.
   *
   * @param query the search query.
   * @return the list of users matching the query.
   */
  public List<User> search(String query) {
    String key = StringUtils.fold(query).toLowerCase(Locale.ROOT);
    return repo.searchByName(key);
  }

  /**
   * Convert an Iterable to a Stream.
   *
   * @param it the iterable.
   * @param <T> the type of the elements.
   * @return the stream.
   */
  private static <T> java.util.stream.Stream<T> toStream(Iterable<T> it) {
    return (it == null)
        ? java.util.stream.Stream.empty()
        : java.util.stream.StreamSupport.stream(it.spliterator(), false);
  }
}
