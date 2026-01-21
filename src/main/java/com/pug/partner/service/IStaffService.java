package com.pug.partner.service;

import com.pug.partner.domain.Staff;
import com.pug.partner.service.dtos.StaffCreateBulkCommand;
import com.pug.partner.service.dtos.StaffCreateCommand;
import com.pug.partner.service.dtos.StaffUpdateCommand;
import com.pug.shared.domain.enums.DeleteKeys;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Interface for managing staff assignments to partner entities.
 */
public interface IStaffService {

    /**
     * Save a new staff member by creating an account and linking them to an entity.
     *
     * @param cmd the command containing staff creation details.
     * @return the created Staff object.
     * @throws com.pug.shared.exceptions.DuplicateResourceException if a staff member with the same account ID already exists.
     * @throws com.pug.shared.exceptions.ResourceNotFoundException  if the specified entity does not exist (or data corrupted in DB).
     * @throws com.pug.shared.exceptions.AppValidationException     if input validation fails for account or staff data.
     */
    Staff save(StaffCreateCommand cmd);

    /**
     * Saves multiple staff members in bulk by creating accounts and linking them to entities.
     *
     * @param cmds an iterable of commands containing staff creation details.
     * @return a list of created Staff objects.
     * @throws com.pug.shared.exceptions.DuplicateResourceException if any staff member with the same account ID already exists or
     *                                                              if there are duplicate account IDs in the input commands.
     * @throws com.pug.shared.exceptions.ResourceNotFoundException  if any specified entity does not exist (or data corrupted in DB).
     * @throws com.pug.shared.exceptions.AppValidationException     if input validation fails for any account or staff in the bulk.
     */
    List<Staff> saveAll(Iterable<StaffCreateBulkCommand> cmds);

    /**
     * Updates an existing staff member's account details.
     *
     * @param id  the ID of the staff user to update.
     * @param cmd the command containing updated staff details.
     * @return the updated Staff object.
     * @throws com.pug.shared.exceptions.ResourceNotFoundException if the specified staff member does not exist (or data corrupted in DB).
     * @throws com.pug.shared.exceptions.AppValidationException    if input validation fails for account or staff data.
     */
    Staff update(UUID id, StaffUpdateCommand cmd);

    /**
     * Deletes all staff members with the specified IDs.
     *
     * @param ids an iterable of staff user IDs to delete.
     * @return a map containing the count of deleted staff, accounts, and users.
     * @throws com.pug.shared.exceptions.ReferencedEntityException if any associated account is still referenced by other modules.
     */
    Map<DeleteKeys, Long> deleteAll(Iterable<UUID> ids);

    /**
     * Retrieves a staff member by their account ID.
     *
     * @param id the ID of the staff account.
     * @return the Staff object.
     * @throws com.pug.shared.exceptions.ResourceNotFoundException if the specified staff member does not exist (or data is corrupted in DB).
     */
    Staff getById(UUID id);

    /**
     * Lists all staff members.
     *
     * @return a list of all Staff objects.
     * @throws com.pug.shared.exceptions.AppValidationException if any Staff entity found is corrupted in the database.
     */
    List<Staff> listAll();

    /**
     * Lists all staff members associated with a specific entity.
     *
     * @param entityId the ID of the entity.
     * @return a list of Staff objects linked to the specified entity.
     * @throws com.pug.shared.exceptions.AppValidationException if any Staff entity found is corrupted in the database.
     */
    List<Staff> listByEntity(UUID entityId);

    /**
     * Checks if a staff member exists by their account ID.
     *
     * @param accountId the ID of the staff account.
     * @return true if the staff member exists, false otherwise.
     */
    boolean existsByAccountId(UUID accountId);

    /**
     * Checks if any staff members exist for the given account IDs.
     *
     * @param accountIds an iterable of account IDs to check.
     * @return true if any staff members exist for the provided account IDs, false otherwise.
     */
    boolean existsAnyByAccountIdIn(Iterable<UUID> accountIds);
}