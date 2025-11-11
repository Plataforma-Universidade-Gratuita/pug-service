package com.pug.identity.service;

import com.pug.academic.service.StudentService;
import com.pug.identity.domain.Account;
import com.pug.identity.domain.AccountRepository;
import com.pug.identity.domain.User;
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

/**
 * Service for managing accounts (login).
 */
@ApplicationScoped
public class AccountService {

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

  @Transactional
  public Account save(Cpf cpf, String name, Email email, AccountType type, String passwordHash) {
    String e = email.toString();
    if (repo.existsByEmail(e)) {
      throw new DuplicateResourceException(
              IdentityErrorCodes.USER_ALREADY_EXISTS, Map.of("email", e));
    }

    UUID userId = resolveUserId(cpf, name);
    Account account = Account.createNew(userId, email, type, passwordHash, time);
    return repo.persist(account);
  }

  @Transactional
  public List<Account> saveAll(Iterable<Account> accounts) {
    List<String> emails = toStream(accounts).map(a -> a.getEmail().toString()).toList();
    if (!emails.isEmpty() && repo.existsAnyByEmailIn(emails)) {
      throw new DuplicateResourceException(IdentityErrorCodes.USER_ALREADY_EXISTS);
    }
    List<Account> normalized =
            toStream(accounts)
                    .map(
                            a ->
                                    a.getCreatedAt() == null
                                            ? a.toBuilder().createdAt(OffsetDateTime.now(time.clock())).build()
                                            : a)
                    .toList();
    return repo.persistAll(normalized);
  }

  @Transactional
  public Account update(UUID id, Account data) {
    Account current =
            repo.findOptionalById(id)
                    .orElseThrow(
                            () ->
                                    new ResourceNotFoundException(
                                            IdentityErrorCodes.USER_NOT_FOUND, Map.of("id", id)));

    String newEmail = data.getEmail() != null ? data.getEmail().toString() : null;
    if (newEmail != null
            && !newEmail.equalsIgnoreCase(current.getEmail().toString())
            && repo.existsByEmail(newEmail)) {
      throw new DuplicateResourceException(
              IdentityErrorCodes.USER_ALREADY_EXISTS, Map.of("email", newEmail));
    }

    Account updated =
            current
                    .changeEmail(data.getEmail() != null ? data.getEmail() : current.getEmail())
                    .setPasswordHash(
                            data.getPasswordHash() != null ? data.getPasswordHash() : current.getPasswordHash());

    repo.update(updated);
    return repo
            .findOptionalById(id)
            .orElseThrow(() -> new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND));
  }

  @Transactional
  public long deleteAll(Iterable<UUID> ids) {
    if (ids == null || !ids.iterator().hasNext()) return 0L;

    if (adminService.existsAnyByAccountIdIn(ids)) {
      throw new ReferencedEntityException(IdentityErrorCodes.USER_REFERENCED_BY_ADMIN);
    }
    if (staffService.existsAnyByAccountIdIn(ids)) {
      throw new ReferencedEntityException(IdentityErrorCodes.USER_REFERENCED_BY_STAFF);
    }
    if (studentService.existsAnyByAccountIdIn(ids)) {
      throw new ReferencedEntityException(IdentityErrorCodes.USER_REFERENCED_BY_STUDENT);
    }
    return repo.deleteByIds(ids);
  }

  public List<Account> listAll() {
    return repo.listAllAccounts();
  }

  public Account getById(UUID id) {
    return repo.findOptionalById(id)
            .orElseThrow(() -> new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND));
  }

  public boolean existsByUserIdIn(Iterable<UUID> userIds) {
    return repo.existsAnyByUserIdIn(userIds);
  }

  private UUID resolveUserId(Cpf cpf, String name) {
    try {
      User u = userService.save(cpf, name); // create if not exists
      return u.getId();
    } catch (DuplicateResourceException alreadyExists) {
      return findExistingUserIdByCpf(cpf);
    }
  }

  private UUID findExistingUserIdByCpf(Cpf cpf) {
    String code = cpf.toString();
    return userService.listAll().stream()
            .filter(u -> u.getCpf().toString().equals(code))
            .map(User::getId)
            .findFirst()
            .orElseThrow(
                    () ->
                            new ResourceNotFoundException(
                                    IdentityErrorCodes.USER_NOT_FOUND, Map.of("cpf", code)));
  }

  private static <T> Stream<T> toStream(Iterable<T> it) {
    return (it == null) ? Stream.empty() : StreamSupport.stream(it.spliterator(), false);
  }
}
