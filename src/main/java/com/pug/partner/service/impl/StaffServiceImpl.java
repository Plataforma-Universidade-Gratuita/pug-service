package com.pug.partner.service.impl;

import com.pug.identity.domain.Account;
import com.pug.identity.service.AccountService;
import com.pug.partner.domain.Staff;
import com.pug.partner.domain.StaffRepository;
import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.partner.service.EntityService;
import com.pug.partner.service.StaffService;
import com.pug.partner.service.dtos.StaffCreateCommand;
import com.pug.partner.service.utils.StaffProcessor;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

/** Service for managing staff assignments to partner entities. */
@ApplicationScoped
public class StaffServiceImpl implements StaffService {

  private static final Logger LOG = Logger.getLogger(StaffServiceImpl.class);

  @Inject StaffRepository repo;

  @Inject AccountService accountService;

  @Inject EntityService entityService;

  @Transactional
  @Override
  public Staff save(StaffCreateCommand cmd) {
    LOG.debugf("Attempting to create Staff for Entity ID: %s", cmd.entityId());
    entityService.getById(cmd.entityId());

    Account account = accountService.save(cmd.accountCommand());
    Staff staff = StaffProcessor.processCreateInput(account.getId(), cmd.entityId());

    if (staff.hasErrors()) {
      throw new AppValidationException(staff.getProblems());
    }

    Staff savedStaff = repo.persist(staff);
    LOG.infof(
        "Staff role granted successfully. Account ID: %s, Entity ID: %s",
        savedStaff.getAccountId(), savedStaff.getEntityId());
    return savedStaff;
  }

  @Transactional
  @Override
  public boolean delete(UUID accountId) {
    LOG.debugf("Attempting to revoke Staff role for Account ID: %s", accountId);
    if (accountId == null) {
      return false;
    }

    boolean deleted = repo.deleteByAccountId(accountId);
    if (deleted) {
      LOG.infof("Staff role revoked successfully. Account ID: %s", accountId);
      accountService.delete(accountId);
    } else {
      LOG.debugf("Revoke failed: Staff ID %s not found (idempotent)", accountId);
    }

    return deleted;
  }

  @Transactional
  @Override
  public long deleteAllByEntityId(UUID entityId) {
    if (entityId == null) {
      return 0;
    }
    LOG.debugf("Attempting to BATCH delete all Staff for Entity ID: %s", entityId);

    List<Staff> staffList = repo.listAllByEntityId(entityId);
    if (staffList.isEmpty()) {
      return 0;
    }

    List<UUID> accountIds = staffList.stream().map(Staff::getAccountId).toList();
    long deletedCount = repo.deleteByEntityId(entityId);

    accountService.deleteAll(accountIds);
    LOG.infof(
        "Batch delete complete. Removed %d staff members (and their accounts) for Entity ID: %s",
        deletedCount, entityId);
    return deletedCount;
  }

  @Override
  public Staff getByAccountId(UUID accountId) {
    Staff staff =
        repo.findOptionalByAccountId(accountId)
            .orElseThrow(
                () -> {
                  LOG.debugf("Staff lookup failed: Account ID %s not found", accountId);
                  return new ResourceNotFoundException(
                      PartnerErrorCodes.STAFF_NOT_FOUND, "accountId", accountId.toString());
                });

    if (staff.hasErrors()) {
      LOG.errorf(
          "DATA CORRUPTION DETECTED: Staff %s violates domain rules: %s",
          accountId, staff.getProblemsSummary());
      throw new ResourceNotFoundException(
          PartnerErrorCodes.STAFF_NOT_FOUND, "accountId", accountId.toString());
    }

    return staff;
  }

  @Override
  public List<Staff> listAll() {
    LOG.debug("Listing all staff");
    List<Staff> allStaff = repo.listAllStaff();

    return allStaff.stream()
        .filter(
            staff -> {
              if (staff.hasErrors()) {
                LOG.errorf(
                    "DATA CORRUPTION DETECTED: Staff %s violates domain rules: %s",
                    staff.getAccountId(), staff.getProblemsSummary());
                return false;
              }
              return true;
            })
        .toList();
  }
}
