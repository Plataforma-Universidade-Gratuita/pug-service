package com.pug.partner.service.impl;

import com.pug.identity.domain.Account;
import com.pug.identity.service.impl.AccountService;
import com.pug.partner.domain.Entity;
import com.pug.partner.domain.IStaffRepository;
import com.pug.partner.domain.Staff;
import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.partner.domain.vos.Cnpj;
import com.pug.partner.service.IEntityService;
import com.pug.partner.service.IStaffService;
import com.pug.partner.service.dtos.StaffCreateBulkCommand;
import com.pug.partner.service.dtos.StaffCreateCommand;
import com.pug.partner.service.dtos.StaffUpdateCommand;
import com.pug.shared.domain.enums.DeleteKeys;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.CollectionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing staff assignments to partner entities.
 */
@ApplicationScoped
public class StaffService implements IStaffService {

  private static final Logger LOG = Logger.getLogger(StaffService.class);

  @Inject
  IStaffRepository repo;
  @Inject
  AccountService accountService;
  @Inject
  IEntityService entityService;

  /**
   * Helper method to process input and build Staff domain object,
   * collecting all validation problems.
   *
   * @param accountId        The ID of the associated account.
   * @param entityCnpjString The CNPJ string of the associated entity.
   * @param problems         List to collect AppValidationException.Problem instances.
   * @return The constructed Staff domain object if no problems, or null if problems occurred.
   */
  private Staff processStaffInput(UUID accountId, String entityCnpjString, List<AppValidationException.Problem> problems) {
    UUID entityId = null;
    Cnpj entityCnpjVO = null;

    try {
      if (entityCnpjString != null && !entityCnpjString.isBlank()) {
        entityCnpjVO = new Cnpj(entityCnpjString);
        Entity entity = entityService.getByCnpj(entityCnpjVO);
        entityId = entity.getId();
      }
    } catch (AppValidationException e) {
      problems.addAll(e.getProblems());
    } catch (ResourceNotFoundException e) {
      problems.add(new AppValidationException.Problem(PartnerErrorCodes.ENTITY_NOT_FOUND, "entityCnpjString"));
    }

    Staff staff = null;
    try {
      staff = Staff.createNew(accountId, entityId);
    } catch (AppValidationException e) {
      problems.addAll(e.getProblems());
    }
    return staff;
  }

  @Transactional
  @Override
  public Staff save(StaffCreateCommand cmd) {
    List<AppValidationException.Problem> problems = new ArrayList<>();
    Account account = null;

    try {
      account = accountService.save(cmd.accountCommand());
    } catch (AppValidationException e) {
      problems.addAll(e.getProblems());
    } catch (DuplicateResourceException e) {
      problems.add(new AppValidationException.Problem(e.getErrorCode(), "accountCommand.emailString"));
    }


    Staff staffToPersist = null;
    if (problems.isEmpty() && account != null) {
      staffToPersist = processStaffInput(account.getId(), cmd.entityCnpjString(), problems);
    } else {
      staffToPersist = processStaffInput(null, cmd.entityCnpjString(), problems);
    }

    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }

    if (existsByAccountId(staffToPersist.getAccountId())) {
      throw new DuplicateResourceException(PartnerErrorCodes.STAFF_ALREADY_EXISTS, Map.of("accountId", staffToPersist.getAccountId()));
    }

    return repo.persist(staffToPersist);
  }

  @Transactional
  @Override
  public List<Staff> saveAll(Iterable<StaffCreateBulkCommand> cmds) {
    if (CollectionUtils.isEmpty(cmds)) {
      return List.of();
    }

    List<AppValidationException.Problem> allCollectedProblems = new ArrayList<>();
    List<Staff> staffToPersist = new ArrayList<>();
    Set<UUID> processedAccountIds = new HashSet<>();

    Map<String, UUID> entityIdsByCnpj = new HashMap<>();
    Set<String> uniqueEntityCnpjs = new HashSet<>();

    CollectionUtils.toStream(cmds).forEach(cmd -> uniqueEntityCnpjs.add(cmd.entityCnpjString()));

    for (String cnpjString : uniqueEntityCnpjs) {
      if (!CollectionUtils.isEmpty(Collections.singleton(cnpjString))) {
        try {
          Cnpj cnpjVO = new Cnpj(cnpjString);
          Entity entity = entityService.getByCnpj(cnpjVO);
          entityIdsByCnpj.put(cnpjString, entity.getId());
        } catch (AppValidationException e) {
          allCollectedProblems.addAll(e.getProblems());
        } catch (ResourceNotFoundException e) {
          allCollectedProblems.add(new AppValidationException.Problem(PartnerErrorCodes.ENTITY_NOT_FOUND, "entityCnpjString"));
        }
      } else {
        allCollectedProblems.add(new AppValidationException.Problem(PartnerErrorCodes.INVALID_CNPJ_BLANK, "entityCnpjString"));
      }
    }

    if (!allCollectedProblems.isEmpty()) {
      throw new AppValidationException(allCollectedProblems);
    }

    List<StaffCreateCommand> staffCreateCommands = new ArrayList<>();
    CollectionUtils.toStream(cmds).forEach(bulkCmd -> {
      bulkCmd.accountCommands().forEach(accCmd -> {
        UUID entityIdForStaff = entityIdsByCnpj.get(bulkCmd.entityCnpjString());
        staffCreateCommands.add(new StaffCreateCommand(bulkCmd.entityCnpjString(), accCmd));
      });
    });

    List<Account> createdAccounts = new ArrayList<>();
    Map<StaffCreateCommand, AppValidationException.Problem> accountCreationProblemsMap = new HashMap<>();

    try {
      List<com.pug.identity.service.dtos.AccountCreateCommand> allAccountCreateCommands = staffCreateCommands.stream()
              .map(StaffCreateCommand::accountCommand)
              .toList();
      createdAccounts = accountService.saveAll(allAccountCreateCommands);
    } catch (AppValidationException e) {
      allCollectedProblems.addAll(e.getProblems());
    }

    Map<Integer, Account> accountByIndex = new HashMap<>();
    for (int i = 0; i < createdAccounts.size(); i++) {
      accountByIndex.put(i, createdAccounts.get(i));
    }

    int staffCmdIndex = 0;
    for (StaffCreateCommand staffCmd : staffCreateCommands) {
      List<AppValidationException.Problem> currentStaffProblems = new ArrayList<>();

      UUID staffAccountId = null;
      if (accountByIndex.containsKey(staffCmdIndex)) {
        staffAccountId = accountByIndex.get(staffCmdIndex).getId();
      } else {
        allCollectedProblems.add(new AppValidationException.Problem(PartnerErrorCodes.INVALID_STAFF_ACCOUNT_BLANK, "accountCommand"));
      }

      Staff staff = processStaffInput(staffAccountId, staffCmd.entityCnpjString(), currentStaffProblems);

      if (!currentStaffProblems.isEmpty()) {
        allCollectedProblems.addAll(currentStaffProblems);
      } else if (staff != null) {
        if (!processedAccountIds.add(staff.getAccountId())) {
          allCollectedProblems.add(new AppValidationException.Problem(PartnerErrorCodes.STAFF_ALREADY_EXISTS, "accountId"));
        }
        staffToPersist.add(staff);
      }
      staffCmdIndex++;
    }

    if (!allCollectedProblems.isEmpty()) {
      throw new AppValidationException(allCollectedProblems);
    }

    List<UUID> accountIdsToPersist = staffToPersist.stream()
            .map(Staff::getAccountId)
            .toList();

    if (repo.existsAnyByAccountIdIn(accountIdsToPersist)) {
      throw new DuplicateResourceException(PartnerErrorCodes.STAFF_ALREADY_EXISTS);
    }

    return repo.persistAll(staffToPersist);
  }

  @Transactional
  @Override
  public Staff update(UUID id, StaffUpdateCommand cmd) {
    Staff current = getById(id);

    List<AppValidationException.Problem> problems = new ArrayList<>();

    if (cmd.accountCommand() != null) {
      try {
        accountService.update(id, cmd.accountCommand());
      } catch (AppValidationException e) {
        problems.addAll(e.getProblems());
      } catch (DuplicateResourceException e) {
        problems.add(new AppValidationException.Problem(e.getErrorCode(), "accountCommand.emailString"));
      }
    }

    UUID effectiveEntityId = null;
    if (cmd.entityCnpjString() != null) {
      Cnpj newEntityCnpjVO = null;
      try {
        if (!cmd.entityCnpjString().isBlank()) {
          newEntityCnpjVO = new Cnpj(cmd.entityCnpjString());
          Entity entity = entityService.getByCnpj(newEntityCnpjVO);
          effectiveEntityId = entity.getId();
        }
      } catch (AppValidationException e) {
        problems.addAll(e.getProblems());
      } catch (ResourceNotFoundException e) {
        problems.add(new AppValidationException.Problem(PartnerErrorCodes.ENTITY_NOT_FOUND, "entityCnpjString"));
      }
    }
    if (effectiveEntityId == null) {
      effectiveEntityId = current.getEntityId();
    }

    Staff staffToUpdate = null;
    try {
      Staff currentWithUpdates = current.toBuilder()
              .entityId(effectiveEntityId)
              .build();
      List<AppValidationException.Problem> domainProblems = currentWithUpdates.collectValidationProblems();
      if (!domainProblems.isEmpty()) problems.addAll(domainProblems);

      staffToUpdate = currentWithUpdates;
    } catch (AppValidationException e) {
      problems.addAll(e.getProblems());
    }

    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }

    repo.update(staffToUpdate);
    return getById(id);
  }

  @Transactional
  @Override
  public Map<DeleteKeys, Long> deleteAll(Iterable<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return Map.of(
              DeleteKeys.STAFF, 0L,
              DeleteKeys.ACCOUNTS, 0L,
              DeleteKeys.USERS, 0L);
    }

    long deletedStaff = repo.deleteByIds(ids);

    Map<DeleteKeys, Long> deletedAccountsAndUsers = accountService.deleteAll(ids);

    return Map.of(
            DeleteKeys.STAFF, deletedStaff,
            DeleteKeys.ACCOUNTS, deletedAccountsAndUsers.getOrDefault(DeleteKeys.ACCOUNTS, 0L),
            DeleteKeys.USERS, deletedAccountsAndUsers.getOrDefault(DeleteKeys.USERS, 0L));
  }

  @Override
  public Staff getById(UUID id) {
    try {
      return repo.findOptionalById(id)
              .orElseThrow(() -> new ResourceNotFoundException(PartnerErrorCodes.STAFF_NOT_FOUND, Map.of("accountId", id)));
    } catch (AppValidationException e) {
      LOG.errorf(e, "Data integrity error: Staff with Account ID %s in DB violates domain rules. Problems: %s",
              id, e.getProblems().stream().map(p -> p.code().getBundleKey() + (p.fieldName() != null ? "(" + p.fieldName() + ")" : "")).collect(Collectors.joining(", ")));
      throw new ResourceNotFoundException(PartnerErrorCodes.STAFF_NOT_FOUND, Map.of("accountId", id));
    }
  }

  @Override
  public List<Staff> listAll() {
    try {
      return repo.listAllStaff();
    } catch (AppValidationException e) {
      LOG.errorf(e, "Data integrity error: Corrupted Staff entity found in DB. Problems: %s",
              e.getProblems().stream().map(p -> p.code().getBundleKey() + (p.fieldName() != null ? "(" + p.fieldName() + ")" : "")).collect(Collectors.joining(", ")));
      throw new ResourceNotFoundException(PartnerErrorCodes.STAFF_NOT_FOUND);
    }
  }

  @Override
  public List<Staff> listByEntity(UUID entityId) {
    if (entityId == null) {
      return List.of();
    }
    try {
      return repo.listAllByEntityId(entityId);
    } catch (AppValidationException e) {
      LOG.errorf(e, "Data integrity error: Corrupted Staff entity found in DB while listing by entity ID %s. Problems: %s",
              entityId, e.getProblems().stream().map(p -> p.code().getBundleKey() + (p.fieldName() != null ? "(" + p.fieldName() + ")" : "")).collect(Collectors.joining(", ")));
      throw new ResourceNotFoundException(PartnerErrorCodes.STAFF_NOT_FOUND);
    }
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