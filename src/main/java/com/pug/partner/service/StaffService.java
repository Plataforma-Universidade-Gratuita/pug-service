package com.pug.partner.service;

import com.pug.identity.domain.Account;
import com.pug.identity.service.AccountService;
import com.pug.partner.domain.Entity;
import com.pug.partner.domain.Staff;
import com.pug.partner.domain.StaffRepository;
import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.partner.domain.vos.Cnpj;
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
public class StaffService {

  private static final Logger LOG = Logger.getLogger(StaffService.class);

  @Inject
  StaffRepository repo;
  @Inject
  AccountService accountService;
  @Inject
  EntityService entityService;

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

  /**
   * Save a new staff member by creating an account and linking them to an entity.
   *
   * @param cmd the command containing staff creation details.
   * @return the created Staff object.
   * @throws DuplicateResourceException if a staff member with the same account ID already exists.
   * @throws ResourceNotFoundException  if the specified entity does not exist (or data corrupted in DB).
   * @throws AppValidationException     if input validation fails for account or staff data.
   */
  @Transactional
  public Staff save(StaffCreateCommand cmd) {
    List<AppValidationException.Problem> problems = new ArrayList<>();
    Account account = null;

    try {
      account = accountService.save(cmd.accountCommand());
    } catch (AppValidationException e) {
      problems.addAll(e.getProblems());
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

  /**
   * Saves multiple staff members in bulk by creating accounts and linking them to entities.
   *
   * @param cmds an iterable of commands containing staff creation details.
   * @return a list of created Staff objects.
   * @throws DuplicateResourceException if any staff member with the same account ID already exists or
   *                                    if there are duplicate account IDs in the input commands.
   * @throws ResourceNotFoundException  if any specified entity does not exist (or data corrupted in DB).
   * @throws AppValidationException     if input validation fails for any account or staff in the bulk.
   */
  @Transactional
  public List<Staff> saveAll(Iterable<StaffCreateBulkCommand> cmds) {
    if (CollectionUtils.isEmpty(cmds)) {
      return List.of();
    }

    List<AppValidationException.Problem> allCollectedProblems = new ArrayList<>();
    List<Staff> staffToPersist = new ArrayList<>();
    Set<UUID> processedAccountIds = new HashSet<>();

    for (StaffCreateBulkCommand cmd : cmds) {
      Cnpj entityCnpjVO = null;
      UUID entityId = null;
      try {
        if (cmd.entityCnpjString() != null && !cmd.entityCnpjString().isBlank()) {
          entityCnpjVO = new Cnpj(cmd.entityCnpjString());
          Entity entity = entityService.getByCnpj(entityCnpjVO);
          entityId = entity.getId();
        }
      } catch (AppValidationException e) {
        allCollectedProblems.addAll(e.getProblems());
      } catch (ResourceNotFoundException e) {
        allCollectedProblems.add(new AppValidationException.Problem(PartnerErrorCodes.ENTITY_NOT_FOUND, "entityCnpjString"));
      }

      List<Account> createdAccounts = new ArrayList<>();
      try {
        if (entityId != null) {
          createdAccounts = accountService.saveAll(cmd.accountCommands());
        }
      } catch (AppValidationException e) {
        allCollectedProblems.addAll(e.getProblems());
      }

      for (Account account : createdAccounts) {
        List<AppValidationException.Problem> currentStaffProblems = new ArrayList<>();
        Staff staff = processStaffInput(account.getId(), cmd.entityCnpjString(), currentStaffProblems);

        if (!currentStaffProblems.isEmpty()) {
          allCollectedProblems.addAll(currentStaffProblems);
        } else if (staff != null) {
          if (!processedAccountIds.add(staff.getAccountId())) {
            allCollectedProblems.add(new AppValidationException.Problem(PartnerErrorCodes.STAFF_ALREADY_EXISTS, "accountId"));
          }
          staffToPersist.add(staff);
        }
      }
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

  /**
   * Updates an existing staff member's account details.
   *
   * @param id  the ID of the staff user to update.
   * @param cmd the command containing updated staff details.
   * @return the updated Staff object.
   * @throws ResourceNotFoundException if the specified staff member does not exist (or data corrupted in DB).
   * @throws AppValidationException    if input validation fails for account or staff data.
   */
  @Transactional
  public Staff update(UUID id, StaffUpdateCommand cmd) {
    Staff current = getById(id);

    List<AppValidationException.Problem> problems = new ArrayList<>();
    Account updatedAccount = null;

    if (cmd.accountCommand() != null) {
      try {
        updatedAccount = accountService.update(id, cmd.accountCommand());
      } catch (AppValidationException e) {
        problems.addAll(e.getProblems());
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
      staffToUpdate = current.toBuilder()
              .entityId(effectiveEntityId)
              .accountId(current.getAccountId())
              .build();
      List<AppValidationException.Problem> staffProblems = staffToUpdate.collectValidationProblems();
      if (!staffProblems.isEmpty()) {
        problems.addAll(staffProblems);
      }
    } catch (AppValidationException e) {
      problems.addAll(e.getProblems());
    }

    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }

    repo.update(staffToUpdate);
    return getById(id);
  }

  /**
   * Deletes all staff members with the specified IDs.
   *
   * @param ids an iterable of staff user IDs to delete.
   * @return a map containing the count of deleted staff, accounts, and users.
   * @throws com.pug.shared.exceptions.ReferencedEntityException if any associated account is still referenced by other modules.
   */
  @Transactional
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

  /**
   * Retrieves a staff member by their account ID.
   *
   * @param id the ID of the staff account.
   * @return the Staff object.
   * @throws ResourceNotFoundException if the specified staff member does not exist (or data is corrupted in DB).
   */
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

  /**
   * Lists all staff members.
   *
   * @return a list of all Staff objects.
   * @throws AppValidationException if any Staff entity found is corrupted in the database.
   */
  public List<Staff> listAll() {
    try {
      return repo.listAllStaff();
    } catch (AppValidationException e) {
      LOG.errorf(e, "Data integrity error: Corrupted Staff entity found in DB. Problems: %s",
              e.getProblems().stream().map(p -> p.code().getBundleKey() + (p.fieldName() != null ? "(" + p.fieldName() + ")" : "")).collect(Collectors.joining(", ")));
      throw new ResourceNotFoundException(PartnerErrorCodes.STAFF_NOT_FOUND);
    }
  }

  /**
   * Lists all staff members associated with a specific entity.
   *
   * @param entityId the ID of the entity.
   * @return a list of Staff objects linked to the specified entity.
   * @throws AppValidationException if any Staff entity found is corrupted in the database.
   */
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

  /**
   * Checks if a staff member exists by their account ID.
   *
   * @param accountId the ID of the staff account.
   * @return true if the staff member exists, false otherwise.
   */
  public boolean existsByAccountId(UUID accountId) {
    if (accountId == null) {
      return false;
    }
    return repo.existsByAccountId(accountId);
  }

  /**
   * Checks if any staff members exist for the given account IDs.
   *
   * @param accountIds an iterable of account IDs to check.
   * @return true if any staff members exist for the provided account IDs, false otherwise.
   */
  public boolean existsAnyByAccountIdIn(Iterable<UUID> accountIds) {
    if (CollectionUtils.isEmpty(accountIds)) {
      return false;
    }
    return repo.existsAnyByAccountIdIn(accountIds);
  }
}