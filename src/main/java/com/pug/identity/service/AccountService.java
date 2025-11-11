package com.pug.identity.service;

import com.pug.academic.service.StudentService;
import com.pug.identity.domain.Account;
import com.pug.identity.domain.AccountRepository;
import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.domain.vos.Email;
import com.pug.partner.service.StaffService;
import com.pug.shared.domain.enums.AccountType;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ReferencedEntityException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.time.TimeProvider;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/** Service for managing users. */
@ApplicationScoped
public class AccountService {

  @Inject
  AccountRepository repo;
  @Inject TimeProvider time;
  @Inject AdminService adminService;
  @Inject
  StaffService staffService;
  @Inject
  StudentService studentService;

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
  public Account save(Cpf cpf, String name, Email email, AccountType type, String passwordHash) {
    String e = email.toString();
    if (repo.existsByEmail(e)) {
      throw new DuplicateResourceException(IdentityErrorCodes.USER_ALREADY_EXISTS, Map.of("email", e));
    }
    Account account = Account.createNew(cpf, name, email, type, passwordHash, time);
    return repo.persist(account);
  }

  /**
   * Saves multiple users.
   *
   * @param users The users to save.
   * @return The saved users.
   * @throws DuplicateResourceException if any user with the same email already exists.
   */
  @Transactional
  public List<Account> saveAll(Iterable<Account> users) {
    List<String> emails = toStream(users).map(u -> u.getEmail().toString()).toList();
    if (!emails.isEmpty() && repo.existsAnyByEmailIn(emails)) {
      throw new DuplicateResourceException(IdentityErrorCodes.USER_ALREADY_EXISTS);
    }
    List<Account> normalized =
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
   * @throws DuplicateResourceException if a user with the same email or cpf already exists.
   */
  @Transactional
  public Account update(UUID id, Account data) {
    Account current =
        repo.findOptionalById(id)
            .orElseThrow(() -> new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND, Map.of("id", id)));

    String newEmail = data.getEmail().toString();
    if (!newEmail.equalsIgnoreCase(current.getEmail().toString())
          && repo.existsByEmail(newEmail)) {
      throw new DuplicateResourceException(IdentityErrorCodes.USER_ALREADY_EXISTS, Map.of("email", newEmail));
    }
    if (!data.getCpf().equals(current.getCpf())
          && repo.existsByCpf(data.getCpf())) {
      throw new DuplicateResourceException(IdentityErrorCodes.USER_ALREADY_EXISTS, Map.of("cpf", data.getCpf().toString()));
    }
    Account updated =
        current
            .changeName(data.getName())
            .changeEmail(data.getEmail())
            .setPasswordHash(data.getPasswordHash())
            .changeCpf(data.getCpf());

    repo.update(updated);
    return repo.findOptionalById(id)
        .orElseThrow(() -> new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND));
  }

  /**
   * Deletes users by their IDs.
   *
   * @param ids The IDs of the users to delete.
   * @return The number of users deleted.
   * @throws ReferencedEntityException if any user is referenced by other entities.
   */
  @Transactional
  public long deleteAll(Iterable<UUID> ids) {
    if (ids == null || !ids.iterator().hasNext()) {
      return 0L;
    }
    if (adminService.existsAnyByUserIdIn(ids)) {
      throw new ReferencedEntityException(IdentityErrorCodes.USER_REFERENCED_BY_ADMIN);
    }
    if (staffService.existsAnyByUserIdIn(ids)) {
      throw new ReferencedEntityException(IdentityErrorCodes.USER_REFERENCED_BY_STAFF);
    }
    if (studentService.existsAnyByUserIdIn(ids)) {
      throw new ReferencedEntityException(IdentityErrorCodes.USER_REFERENCED_BY_STUDENT);
    }
    return repo.deleteByIds(ids);
  }

  /**
   * Lists all users.
   *
   * @return A list of all users.
   */
  public List<Account> listAll() {
    return repo.listAllUsers();
  }

  /**
   * Gets a user by ID.
   *
   * @param id The ID of the user.
   * @return The user.
   * @throws ResourceNotFoundException if the user does not exist.
   */
  public Account getById(UUID id) {
    return repo.findOptionalById(id)
        .orElseThrow(() -> new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND));
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
