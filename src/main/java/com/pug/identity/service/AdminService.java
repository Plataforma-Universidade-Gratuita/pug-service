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
import java.util.UUID;

/** Service for managing admins. */
@ApplicationScoped
public class AdminService {

  @Inject AdminRepository adminsRepo;
  @Inject UserService userService;
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
    var user = userService.save(cpf, name, email, AccountType.ADMIN, hash);
    var admin =
        Admin.builder().userId(user.getId()).grantedAt(OffsetDateTime.now(time.clock())).build();
    return adminsRepo.persist(admin);
  }

  /**
   * Revokes admin privileges and deletes the underlying user.
   *
   * @param userId The ID of the admin user to be revoked.
   */
  @Transactional
  public void revoke(UUID userId) {
    adminsRepo
        .findOptionalById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(IdentityErrorCodes.ADMIN_NOT_FOUND));
    adminsRepo.deleteByIds(List.of(userId));
    userService.deleteByIds(List.of(userId));
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
}
