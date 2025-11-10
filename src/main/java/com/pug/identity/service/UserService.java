package com.pug.identity.service;

import com.pug.identity.domain.User;
import com.pug.identity.domain.UserRepository;
import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.domain.vos.Email;
import com.pug.shared.domain.enums.AccountType;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.text.StringUtils;
import com.pug.shared.time.TimeProvider;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/** Service for managing users. */
@ApplicationScoped
public class UserService {

  @Inject UserRepository repo;
  @Inject TimeProvider time;

  /**
   * Saves a new user.
   *
   * @param cpf The user's CPF.
   * @param name The user's name.
   * @param email The user's email.
   * @param type The account type.
   * @param passwordHash The password hash.
   * @return The saved user.
   * @throws DuplicateResourceException if a user with the same email already exists.
   */
  @Transactional
  public User save(Cpf cpf, String name, Email email, AccountType type, String passwordHash) {
    String e = email.toString();
    if (repo.existsByEmail(e)) {
      throw new DuplicateResourceException(IdentityErrorCodes.USER_ALREADY_EXISTS);
    }
    User user = User.createNew(cpf, name, email, type, passwordHash, time);
    return repo.persist(user);
  }

  /**
   * Saves multiple users.
   *
   * @param users The users to save.
   * @return The saved users.
   * @throws DuplicateResourceException if any user with the same email already exists.
   */
  @Transactional
  public List<User> saveAll(Iterable<User> users) {
    List<String> emails = toStream(users).map(u -> u.getEmail().toString()).toList();
    if (!emails.isEmpty() && repo.existsAnyByEmailIn(emails)) {
      throw new DuplicateResourceException(IdentityErrorCodes.USER_ALREADY_EXISTS);
    }
    List<User> normalized =
        toStream(users)
            .map(
                u ->
                    u.getCreatedAt() == null
                        ? u.toBuilder().createdAt(OffsetDateTime.now(time.clock())).build()
                        : u)
            .toList();
    return repo.persistAll(normalized);
  }

  /**
   * Updates an existing user.
   *
   * @param id The ID of the user to update.
   * @param data The new user data.
   * @return The updated user.
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
        current
            .changeName(data.getName())
            .changeEmail(data.getEmail())
            .setPasswordHash(data.getPasswordHash())
            .toBuilder()
            .accountType(data.getAccountType())
            .cpf(data.getCpf())
            .build();

    repo.update(updated);
    return repo.findOptionalById(id)
        .orElseThrow(() -> new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND));
  }

  /**
   * Deletes users by their IDs.
   *
   * @param ids The IDs of the users to delete.
   * @return The number of users deleted.
   */
  @Transactional
  public long deleteByIds(Iterable<UUID> ids) {
    return repo.deleteByIds(ids);
  }

  /**
   * Lists all users.
   *
   * @return A list of all users.
   */
  public List<User> listAll() {
    return repo.listAllUsers();
  }

  /**
   * Gets a user by ID.
   *
   * @param id The ID of the user.
   * @return The user.
   * @throws ResourceNotFoundException if the user does not exist.
   */
  public User getById(UUID id) {
    return repo.findOptionalById(id)
        .orElseThrow(() -> new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND));
  }

  /**
   * Gets a user by email.
   *
   * @param email The email of the user.
   * @return The user.
   * @throws ResourceNotFoundException if the user does not exist.
   */
  public User getByEmail(String email) {
    return repo.findOptionalByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND));
  }

  /**
   * Lists users by CPF.
   *
   * @param cpf The CPF to filter by.
   * @return A list of users with the given CPF.
   */
  public List<User> listByCpf(String cpf) {
    return repo.listByCpf(cpf);
  }

  /**
   * Searches users by name.
   *
   * @param query The search query.
   * @return A list of users matching the query.
   */
  public List<User> search(String query) {
    String key = StringUtils.fold(query).toLowerCase(Locale.ROOT);
    return repo.searchByName(key);
  }

  /**
   * Converts an Iterable to a Stream.
   *
   * @param it The iterable to convert.
   * @param <T> The type of elements.
   * @return A stream of the iterable's elements.
   */
  private static <T> Stream<T> toStream(Iterable<T> it) {
    return (it == null) ? Stream.empty() : StreamSupport.stream(it.spliterator(), false);
  }
}
