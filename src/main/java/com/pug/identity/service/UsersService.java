package com.pug.identity.service;

import com.pug.identity.domain.User;
import com.pug.identity.domain.UsersRepository;
import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.infra.UserMapper;
import com.pug.identity.infra.persistence.UsersEntity;
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
    UsersEntity e = UserMapper.toEntity(user);
    repo.persist(e);
    return UserMapper.toDomain(e);
  }

  /**
   * Save users in bulk.
   *
   * @param users the users to save.
   * @throws DuplicateResourceException if any user with the same email already exists.
   */
  @Transactional
  public void saveAll(Iterable<User> users) {
    List<String> emails = toStream(users).map(u -> u.getEmail().toString()).toList();
    if (!emails.isEmpty() && repo.existsAnyByEmailIn(emails)) {
      throw new DuplicateResourceException(IdentityErrorCodes.USER_ALREADY_EXISTS);
    }
    List<UsersEntity> entities = toStream(users).map(UserMapper::toEntity).toList();
    repo.persistAll(entities);
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
    UsersEntity e =
        repo.findOptionalById(id)
            .orElseThrow(() -> new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND));

    String newEmail = data.getEmail().toString();
    if (!newEmail.equalsIgnoreCase(e.getEmail())) {
      repo.findOptionalByEmail(newEmail)
          .filter(found -> !found.getId().equals(id))
          .ifPresent(
              x -> {
                throw new DuplicateResourceException(IdentityErrorCodes.USER_ALREADY_EXISTS);
              });
    }

    UserMapper.copy(data, e);
    return UserMapper.toDomain(e);
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
    UsersEntity e =
        repo.findOptionalById(id)
            .orElseThrow(() -> new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND));
    if (Boolean.TRUE.equals(e.getActive())) {
      e.setActive(false); // managed; flushed at tx end
    }
  }

  /**
   * Deactivate a user by ID.
   *
   * @param id the ID of the user to deactivate.
   * @throws ResourceNotFoundException if the user does not exist.
   */
  @Transactional
  public void activateById(UUID id) {
    UsersEntity e =
        repo.findOptionalById(id)
            .orElseThrow(() -> new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND));
    if (Boolean.FALSE.equals(e.getActive())) {
      e.setActive(true);
    }
  }

  /**
   * List all users.
   *
   * @return the list of all users.
   */
  public List<User> listAll() {
    return repo.listAllUsers().stream().map(UserMapper::toDomain).toList();
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
        .map(UserMapper::toDomain)
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
        .map(UserMapper::toDomain)
        .orElseThrow(() -> new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND));
  }

  /**
   * List users by CPF.
   *
   * @param cpf the CPF to search for.
   * @return the list of users with the given CPF.
   */
  public List<User> listByCpf(String cpf) {
    return repo.listByCpf(cpf).stream().map(UserMapper::toDomain).toList();
  }

  /**
   * Search users by name query.
   *
   * @param query the search query.
   * @return the list of users matching the query.
   */
  public List<User> search(String query) {
    String key = StringUtils.fold(query).toLowerCase(Locale.ROOT);
    return repo.searchByName(key).stream().map(UserMapper::toDomain).toList();
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
