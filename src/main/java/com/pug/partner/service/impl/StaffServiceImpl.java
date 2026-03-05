package com.pug.partner.service.impl;

import com.pug.identity.domain.Account;
import com.pug.identity.service.AccountService;
import com.pug.partner.domain.Staff;
import com.pug.partner.domain.StaffRepository;
import com.pug.partner.service.EntityService;
import com.pug.partner.service.StaffService;
import com.pug.partner.service.dtos.StaffCreateCommand;
import com.pug.partner.service.dtos.StaffUpdateCommand;
import com.pug.partner.service.utils.ExceptionHelper;
import com.pug.partner.service.utils.StaffProcessor;
import com.pug.projects.service.AttendanceService;
import com.pug.projects.service.ProjectService;
import com.pug.shared.exceptions.AppValidationException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

/**
 * Implementation of the {@link StaffService} command interface.
 *
 * <p>This application-scoped service orchestrates state mutations for staff privileges. Because a
 * staff member is inherently an extension of an authentication account tied to a specific partner
 * organization, this service delegates identity concerns down to the {@link AccountService} and
 * structural validations to the {@link EntityService}.
 */
@ApplicationScoped
public class StaffServiceImpl implements StaffService {

  private static final Logger LOG = Logger.getLogger(StaffServiceImpl.class);

  @Inject StaffRepository repo;

  @Inject AccountService accountService;

  @Inject EntityService entityService;

  @Inject ProjectService projectService;

  @Inject AttendanceService attendanceService;

  /** {@inheritDoc} */
  @Transactional
  @Override
  public boolean deactivate(UUID accountId) {
    LOG.debugf("Attempting to deactivate Staff Account ID: %s", accountId);
    if (accountId == null) {
      return false;
    }

    accountService.deactivate(accountId);

    LOG.infof("Staff account deactivated successfully. Account ID: %s", accountId);
    return true;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public boolean delete(UUID accountId) {
    LOG.debugf("Attempting to hard-delete Staff Account ID: %s", accountId);
    if (accountId == null) {
      return false;
    }

    if (projectService.existsByCreatedBy(accountId)) {
      LOG.warnf("Hard delete failed: Staff ID %s created projects", accountId);
      throw ExceptionHelper.staffHasProjects();
    }
    if (attendanceService.existsByValidatedBy(accountId)) {
      LOG.warnf("Hard delete failed: Staff ID %s validated attendances", accountId);
      throw ExceptionHelper.staffHasAttendances();
    }

    boolean deleted = repo.deleteByAccountId(accountId);
    if (deleted) {
      LOG.infof("Staff deleted successfully. Account ID: %s", accountId);
      accountService.delete(accountId);
    }
    return deleted;
  }

  /** {@inheritDoc} */
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

  /**
   * Checks if a Staff assignment already exists for the given account and entity.
   *
   * @param accountId the unique identifier of the linked authentication account
   * @param entityId the unique identifier of the partner entity
   * @return {@code true} if the staff assignment exists, {@code false} otherwise
   */
  private boolean existsByAccountIdAndEntityId(UUID accountId, UUID entityId) {
    if (accountId == null || entityId == null) {
      return false;
    }
    return repo.existsByAccountIdAndEntityId(accountId, entityId);
  }

  /** {@inheritDoc} */
  @Override
  public Staff getByAccountId(UUID accountId) {
    Staff staff =
        repo.findOptionalByAccountId(accountId)
            .orElseThrow(
                () -> {
                  LOG.debugf("Staff lookup failed: Account ID %s not found", accountId);
                  return ExceptionHelper.staffNotFound();
                });

    if (staff.hasFieldErrors()) {
      LOG.errorf(
          "DATA CORRUPTION DETECTED: Staff %s violates domain rules: %s",
          accountId, staff.getProblemsSummary());
      throw ExceptionHelper.staffNotFound();
    }

    return staff;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public Staff save(StaffCreateCommand cmd) {
    LOG.debugf("Attempting to create Staff for Entity ID: %s", cmd.entityId());

    entityService.getById(cmd.entityId());
    Account account = accountService.save(cmd.accountCommand());

    repo.findOptionalByAccountId(account.getId())
        .ifPresent(
            existingStaff -> {
              if (existingStaff.getEntityId().equals(cmd.entityId())) {
                LOG.warnf(
                    "Creation failed: "
                        + "Staff role already exists for Account ID: %s in Entity ID: %s",
                    account.getId(), cmd.entityId());
                throw ExceptionHelper.staffAlreadyExists();
              } else {
                LOG.warnf(
                    "Creation failed: "
                        + "Account ID: %s is already assigned to a different Entity ID: %s",
                    account.getId(), existingStaff.getEntityId());
                throw ExceptionHelper.staffAssignedToOtherEntity();
              }
            });

    Staff staff = StaffProcessor.processCreateInput(account.getId(), cmd.entityId());

    if (staff.hasFieldErrors()) {
      throw new AppValidationException(staff.getFieldErrors());
    }

    Staff savedStaff = repo.persist(staff);
    LOG.infof(
        "Staff role granted successfully. Account ID: %s, Entity ID: %s",
        savedStaff.getAccountId(), savedStaff.getEntityId());
    return savedStaff;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public Staff update(UUID accountId, StaffUpdateCommand cmd) {
    LOG.debugf("Attempting to update Staff underlying Account ID: %s", accountId);

    Staff current = getByAccountId(accountId);
    accountService.update(accountId, cmd.accountCommand());

    LOG.infof("Staff account updated successfully. Account ID: %s", accountId);
    return current;
  }
}
