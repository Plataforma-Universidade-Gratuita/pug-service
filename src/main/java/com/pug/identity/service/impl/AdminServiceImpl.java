package com.pug.identity.service.impl;

import com.pug.identity.domain.Account;
import com.pug.identity.domain.Admin;
import com.pug.identity.domain.AdminRepository;
import com.pug.identity.service.AccountService;
import com.pug.identity.service.AdminService;
import com.pug.identity.service.dtos.AdminCreateCommand;
import com.pug.identity.service.dtos.AdminUpdateCommand;
import com.pug.identity.service.utils.AdminProcessor;
import com.pug.identity.service.utils.ExceptionHelper;
import com.pug.shared.exceptions.AppValidationException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.UUID;

/**
 * Implementation of the {@link AdminService} command interface.
 * <p>
 * This application-scoped service orchestrates state mutations for administrator profiles.
 * Because an admin is inherently an extension of an account, this service delegates
 * authentication and identity concerns down to the {@link AccountService}, ensuring
 * proper transaction boundaries and lifecycle cascading (e.g., deleting the account when
 * admin privileges are revoked).
 */
@ApplicationScoped
public class AdminServiceImpl implements AdminService {

  private static final Logger LOG = Logger.getLogger(AdminServiceImpl.class);

  @Inject
  AdminRepository repo;

  @Inject
  AccountService accountService;

  /**
   * {@inheritDoc}
   */
  @Transactional
  @Override
  public Admin save(AdminCreateCommand cmd) {
    LOG.debugf("Attempting to create Admin for email: %s", cmd.accountCommand().emailString());
    Account account = accountService.save(cmd.accountCommand());

    Admin admin = AdminProcessor.processCreateInput(account.getId(), cmd.campus());
    if (admin.hasFieldErrors()) {
      throw new AppValidationException(admin.getFieldErrors());
    }

    Admin savedAdmin = repo.persist(admin);
    LOG.infof("Admin role granted successfully. Account ID: %s", savedAdmin.getAccountId());
    return savedAdmin;
  }

  /**
   * {@inheritDoc}
   */
  @Transactional
  @Override
  public Admin update(UUID accountId, AdminUpdateCommand cmd) {
    LOG.debugf("Attempting to update Admin details for Account ID: %s", accountId);
    Admin current = getByAccountId(accountId);

    if (cmd.accountCommand() != null) {
      accountService.update(accountId, cmd.accountCommand());
    }

    Admin updatedAdmin = AdminProcessor.processUpdateInput(current, cmd.campus());

    if (updatedAdmin.hasFieldErrors()) {
      throw new AppValidationException(updatedAdmin.getFieldErrors());
    }

    repo.update(updatedAdmin);
    LOG.infof("Admin details updated. Account ID: %s", accountId);
    return getByAccountId(accountId);
  }

  /**
   * {@inheritDoc}
   */
  @Transactional
  @Override
  public boolean delete(UUID accountId) {
    LOG.debugf("Attempting to revoke Admin role for Account ID: %s", accountId);
    boolean deleted = repo.deleteByAccountId(accountId);

    if (deleted) {
      LOG.infof("Admin role revoked successfully. Account ID: %s", accountId);
      accountService.delete(accountId);
    } else {
      LOG.debugf("Revoke failed: Admin ID %s not found (idempotent)", accountId);
    }

    return deleted;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Admin getByAccountId(UUID accountId) {
    Admin admin =
            repo.findOptionalByAccountId(accountId)
                    .orElseThrow(
                            () -> {
                              LOG.debugf("Admin lookup failed: Account ID %s not found", accountId);
                              return ExceptionHelper.adminNotFound();
                            });

    if (admin.hasFieldErrors()) {
      LOG.errorf(
              "DATA CORRUPTION DETECTED: Admin %s violates domain rules: %s",
              accountId, admin.getProblemsSummary());
      throw ExceptionHelper.adminNotFound();
    }

    return admin;
  }
}