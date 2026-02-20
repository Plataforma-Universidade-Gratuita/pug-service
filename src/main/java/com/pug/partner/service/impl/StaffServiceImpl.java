package com.pug.partner.service.impl;

import com.pug.identity.domain.Account;
import com.pug.identity.service.AccountService;
import com.pug.partner.domain.Staff;
import com.pug.partner.domain.StaffRepository;
import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.partner.service.EntityService;
import com.pug.partner.service.StaffService;
import com.pug.partner.service.dtos.StaffCreateCommand;
import com.pug.partner.service.dtos.StaffUpdateCommand;
import com.pug.partner.service.utils.StaffProcessor;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.CollectionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
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
    entityService.getById(cmd.entityId());

    Account account = accountService.save(cmd.accountCommand());

    Staff staff = StaffProcessor.processCreateInput(account.getId(), cmd.entityId());

    if (staff.hasErrors()) {
      throw new AppValidationException(staff.getProblems());
    }

    return repo.persist(staff);
  }

  @Transactional
  @Override
  public List<Staff> saveAll(Iterable<StaffCreateCommand> cmds) {
    if (CollectionUtils.isEmpty(cmds)) {
      return List.of();
    }

    List<StaffCreateCommand> cmdList = CollectionUtils.toStream(cmds).toList();

    for (StaffCreateCommand cmd : cmdList) {
      if (cmd.entityId() != null) {
        entityService.getById(cmd.entityId());
      } else {
        throw new AppValidationException(PartnerErrorCodes.INVALID_STAFF_ENTITY_BLANK);
      }
    }

    List<Account> accounts =
        accountService.saveAll(
            cmdList.stream().map(StaffCreateCommand::accountCommand).collect(Collectors.toList()));

    List<Problem> problems = new ArrayList<>();
    List<Staff> staffToPersist = new ArrayList<>();

    if (accounts.size() != cmdList.size()) {
      throw new RuntimeException(
          "Mismatch between requested staff creations and created accounts.");
    }

    for (int i = 0; i < accounts.size(); i++) {
      Account account = accounts.get(i);
      StaffCreateCommand cmd = cmdList.get(i);

      Staff staff = StaffProcessor.processCreateInput(account.getId(), cmd.entityId());

      if (staff.hasErrors()) {
        problems.addAll(staff.getProblems());
      } else {
        staffToPersist.add(staff);
      }
    }

    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }

    return repo.persistAll(staffToPersist);
  }

  @Transactional
  @Override
  public Staff update(UUID id, StaffUpdateCommand cmd) {
    Staff current = getById(id);

    if (cmd.accountCommand() != null) {
      accountService.update(id, cmd.accountCommand());
    }

    if (cmd.entityId() != null) {
      entityService.getById(cmd.entityId());
    }

    Staff updatedStaff = StaffProcessor.processUpdateInput(current, cmd.entityId());

    if (updatedStaff.hasErrors()) {
      throw new AppValidationException(updatedStaff.getProblems());
    }

    repo.update(updatedStaff);
    return getById(id);
  }

  @Transactional
  @Override
  public Map<DeleteKeys, Long> deleteAll(Iterable<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return Map.of(DeleteKeys.STAFF, 0L, DeleteKeys.ACCOUNTS, 0L, DeleteKeys.USERS, 0L);
    }

    long deletedStaff = repo.deleteByIds(ids);

    Map<DeleteKeys, Long> accountDeleteResult = accountService.deleteAll(ids);

    return Map.of(
        DeleteKeys.STAFF, deletedStaff,
        DeleteKeys.ACCOUNTS, accountDeleteResult.getOrDefault(DeleteKeys.ACCOUNTS, 0L),
        DeleteKeys.USERS, accountDeleteResult.getOrDefault(DeleteKeys.USERS, 0L));
  }

  @Override
  public Staff getById(UUID accountId) {
    Staff staff =
        repo.findOptionalById(accountId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        PartnerErrorCodes.STAFF_NOT_FOUND, Map.of("accountId", accountId)));

    if (staff.hasErrors()) {
      LOG.errorf(
          "Data integrity error: "
              + "Staff with Account ID %s in DB violates domain rules. Problems: %s",
          accountId, staff.getProblemsSummary());
      throw new ResourceNotFoundException(
          PartnerErrorCodes.STAFF_NOT_FOUND, Map.of("accountId", accountId));
    }

    return staff;
  }

  @Override
  public List<Staff> listAll() {
    List<Staff> allStaff = repo.listAllStaff();

    for (Staff staff : allStaff) {
      if (staff.hasErrors()) {
        LOG.errorf(
            "Data integrity error: Corrupted Staff entity found in DB. Problems: %s",
            staff.getProblemsSummary());
        throw new ResourceNotFoundException(PartnerErrorCodes.STAFF_NOT_FOUND);
      }
    }

    return allStaff;
  }

  @Override
  public List<Staff> listByEntity(UUID entityId) {
    if (entityId == null) {
      return List.of();
    }

    List<Staff> staffList = repo.listAllByEntityId(entityId);

    for (Staff staff : staffList) {
      if (staff.hasErrors()) {
        LOG.errorf(
            "Data integrity error: Corrupted Staff entity found in DB. Problems: %s",
            staff.getProblemsSummary());
        throw new ResourceNotFoundException(PartnerErrorCodes.STAFF_NOT_FOUND);
      }
    }

    return staffList;
  }

  @Override
  public boolean existsByAccountId(UUID accountId) {
    if (accountId == null) {
      return false;
    }
    return repo.existsByAccountId(accountId);
  }

  @Override
  public boolean existsAnyByAccountIdIn(Iterable<UUID> accountIds) {
    if (CollectionUtils.isEmpty(accountIds)) {
      return false;
    }
    return repo.existsAnyByAccountIdIn(accountIds);
  }
}
