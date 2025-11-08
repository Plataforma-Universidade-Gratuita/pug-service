package com.pug.identity.service;

import com.pug.identity.domain.Admin;
import com.pug.identity.domain.AdminsRepository;
import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/** Service for managing admins. */
@ApplicationScoped
public class AdminsService {

  @Inject AdminsRepository adminsRepo;
  @Inject UsersService usersService;

  /**
   * Grant admin rights to a user.
   *
   * @param userId the ID of the user to grant admin rights to.
   * @return the granted admin.
   * @throws DuplicateResourceException if the user is already an admin.
   */
  @Transactional
  public Admin grant(UUID userId) {
    var user = usersService.getById(userId);
    if (adminsRepo.existsByUserId(userId)) {
      throw new DuplicateResourceException(IdentityErrorCodes.ADMIN_ALREADY_EXISTS);
    }
    var admin = Admin.builder().user(user).grantedAt(OffsetDateTime.now()).build();
    return adminsRepo.persist(admin);
  }

  /**
   * Revoke admin rights from a user.
   *
   * @param userId the ID of the user to revoke admin rights from.
   * @throws ResourceNotFoundException if the admin is not found.
   */
  @Transactional
  public void revoke(UUID userId) {
    adminsRepo
        .findOptionalById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(IdentityErrorCodes.ADMIN_NOT_FOUND));
    adminsRepo.deleteByIds(List.of(userId));
    usersService.deactivateById(userId);
  }

  /**
   * Reactivate admin rights for a user.
   *
   * @param userId the ID of the user to reactivate admin rights for.
   * @return the reactivated admin.
   */
  @Transactional
  public Admin reactivate(UUID userId) {
    usersService.activateById(userId);
    return adminsRepo
        .findOptionalById(userId)
        .orElseGet(
            () ->
                adminsRepo.persist(
                    Admin.builder()
                        .user(usersService.getById(userId))
                        .grantedAt(OffsetDateTime.now())
                        .build()));
  }

  /**
   * Get an admin by user ID.
   *
   * @param userId the ID of the user.
   * @return the admin.
   * @throws ResourceNotFoundException if the admin is not found.
   */
  public Admin get(UUID userId) {
    return adminsRepo
        .findOptionalById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(IdentityErrorCodes.ADMIN_NOT_FOUND));
  }

  /**
   * List all admins.
   *
   * @return the list of all admins.
   */
  public List<Admin> listAll() {
    return adminsRepo.listAllAdmins();
  }
}
