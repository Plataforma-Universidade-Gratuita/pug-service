package com.pug.identity.service.impl;

import com.pug.identity.domain.Account;
import com.pug.identity.domain.Admin;
import com.pug.identity.domain.AdminRepository;
import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.service.AccountService;
import com.pug.identity.service.AdminService;
import com.pug.identity.service.dtos.AdminCreateCommand;
import com.pug.identity.service.dtos.AdminUpdateCommand;
import com.pug.identity.service.utils.AdminProcessor;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.jboss.logging.Logger;

/** Service for managing admins. */
@ApplicationScoped
public class AdminServiceImpl implements AdminService {

  private static final Logger LOG = Logger.getLogger(AdminServiceImpl.class);

  @Inject AdminRepository repo;

  @Inject AccountService accountService;

  @Transactional
  @Override
  public Admin save(AdminCreateCommand cmd) {
    LOG.debugf("Attempting to create Admin for email: %s", cmd.accountCommand().emailString());
    Account account = accountService.save(cmd.accountCommand());

    Admin admin = AdminProcessor.processCreateInput(account.getId(), cmd.campus());
    if (admin.hasErrors()) {
      throw new AppValidationException(admin.getProblems());
    }

    Admin savedAdmin = repo.persist(admin);
    LOG.infof("Admin role granted successfully. Account ID: %s", savedAdmin.getAccountId());
    return savedAdmin;
  }

  @Transactional
  @Override
  public Admin update(UUID accountId, AdminUpdateCommand cmd) {
    LOG.debugf("Attempting to update Admin details for Account ID: %s", accountId);
    Admin current = getByAccountId(accountId);

    if (cmd.accountCommand() != null) {
      accountService.update(accountId, cmd.accountCommand());
    }

    Admin updatedAdmin = AdminProcessor.processUpdateInput(current, cmd.campus());

    if (updatedAdmin.hasErrors()) {
      throw new AppValidationException(updatedAdmin.getProblems());
    }

    repo.update(updatedAdmin);
    LOG.infof("Admin details updated. Account ID: %s", accountId);
    return getByAccountId(accountId);
  }

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

  @Override
  public Admin getByAccountId(UUID accountId) {
    Admin admin =
        repo.findOptionalByAccountId(accountId)
            .orElseThrow(
                () -> {
                  LOG.debugf("Admin lookup failed: Account ID %s not found", accountId);
                  return new ResourceNotFoundException(
                      IdentityErrorCodes.ADMIN_NOT_FOUND, "accountId", accountId.toString());
                });

    if (admin.hasErrors()) {
      LOG.errorf(
          "DATA CORRUPTION DETECTED: Admin %s violates domain rules: %s",
          accountId, admin.getProblemsSummary());
      throw new ResourceNotFoundException(
          IdentityErrorCodes.ADMIN_NOT_FOUND, "accountId", accountId.toString());
    }

    return admin;
  }
}
