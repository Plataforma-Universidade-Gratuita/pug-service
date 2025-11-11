package com.pug.identity.service;

import com.pug.identity.domain.Admin;
import com.pug.identity.domain.AdminRepository;
import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.domain.vos.Email;
import com.pug.shared.domain.enums.AccountType;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.time.TimeProvider;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/** Service for managing admins. */
@ApplicationScoped
public class AdminService {

  @Inject AdminRepository adminsRepo;
  @Inject
  AccountService accountService;
  @Inject TimeProvider time;
  @Inject PasswordService passwords;

  /**
   * Creates and saves a new admin user.
   *
   * @param cpf The CPF of the admin.
   * @param name The name of the admin.
   * @param email The email of the admin.
   * @param rawPassword The raw password for the admin.
   * @return The saved Admin entity.
   */
  @Transactional
  public Admin save(Cpf cpf, String name, Email email, String rawPassword) {
    String hash = passwords.hash(rawPassword);
    var user = accountService.save(cpf, name, email, AccountType.ADMIN, hash);
    var admin =
        Admin.builder().userId(user.getId()).grantedAt(OffsetDateTime.now(time.clock())).build();
    return adminsRepo.persist(admin);
  }

  /**
   * Revokes admin privileges for all given userIds and deletes the underlying users.
   *
   * @param ids Iterable of admin userIds to delete.
   * @return A map with counts of deleted admins and users.
   */
  @Transactional
  public Map<String, Long> deleteAll(Iterable<UUID> ids) {
    List<UUID> list = toStream(ids).filter(Objects::nonNull).toList();
    if (list.isEmpty()) {
      return Map.of();
    }
    var admins = adminsRepo.deleteByIds(list);
    var users = accountService.deleteAll(list);
    return Map.of("admins", admins, "users", users);
  }

  /**
   * Retrieves an admin by user ID.
   *
   * @param userId The ID of the admin user.
   * @return The Admin entity.
   */
  public Admin get(UUID userId) {
    return adminsRepo
        .findOptionalById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(IdentityErrorCodes.ADMIN_NOT_FOUND));
  }

  /**
   * Lists all admins.
   *
   * @return A list of all Admin entities.
   */
  public List<Admin> listAll() {
    return adminsRepo.listAllAdmins();
  }

  /**
   * Checks if any admin exists with the given user IDs.
   *
   * @param ids Iterable of user IDs to check.
   * @return true if any admin exists with the given user IDs, false otherwise.
   */
  public boolean existsAnyByUserIdIn(Iterable<UUID> ids) {
    return adminsRepo.existsAnyByUserIdIn(ids);
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
