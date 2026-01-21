package com.pug.identity.service;

import com.pug.identity.domain.Admin;
import com.pug.identity.service.dtos.AdminCreateCommand;
import com.pug.identity.service.dtos.AdminUpdateCommand;
import com.pug.shared.domain.enums.DeleteKeys;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Interface for managing admins.
 */
public interface IAdminService {

    /**
     * Creates and saves a new Admin.
     *
     * <p>This method also creates and saves the associated Account.
     *
     * @param cmd the command containing the data to create the new Admin.
     * @return the saved Admin.
     * @throws com.pug.shared.exceptions.AppValidationException if input validation fails for account or admin data.
     */
    Admin save(AdminCreateCommand cmd);

    /**
     * Creates and saves multiple new Admins.
     *
     * <p>This method also creates and saves the associated Accounts.
     *
     * @param cmds the commands containing the data to create the new Admins.
     * @return the list of saved Admins.
     * @throws com.pug.shared.exceptions.AppValidationException if input validation fails for any admin or account in the bulk.
     */
    List<Admin> saveAll(Iterable<AdminCreateCommand> cmds);

    /**
     * Updates an existing Admin.
     *
     * <p>This method also updates the associated Account.
     *
     * @param id  the ID of the Admin to update.
     * @param cmd the command containing the data to update the Admin.
     * @return the updated Admin.
     * @throws com.pug.shared.exceptions.ResourceNotFoundException if the Admin with the given ID does not exist (or data is corrupted in DB).
     * @throws com.pug.shared.exceptions.AppValidationException    if input validation fails for account data.
     */
    Admin update(UUID id, AdminUpdateCommand cmd);

    /**
     * Deletes all Admins with the given IDs.
     *
     * <p>This method also deletes the associated Accounts.
     *
     * @param ids the IDs of the Admins to delete.
     * @return a map containing the count of deleted Admins and Accounts.
     * @throws com.pug.shared.exceptions.ReferencedEntityException if any account is still referenced by Admin, Staff, or
     *                                                             Student entities (this check is done by AccountService).
     */
    Map<DeleteKeys, Long> deleteAll(Iterable<UUID> ids);

    /**
     * Retrieves an Admin by account ID.
     *
     * @param accountId the UUID of the account.
     * @return the Admin entity.
     * @throws com.pug.shared.exceptions.ResourceNotFoundException if the Admin with the given account ID does not exist (or data is corrupted in DB).
     */
    Admin getById(UUID accountId);

    /**
     * Lists all Admin entities.
     *
     * @return a list of all Admin entities.
     * @throws com.pug.shared.exceptions.AppValidationException if any Admin entity found is corrupted in the database.
     */
    List<Admin> listAll();

    /**
     * Checks if any Admin exists with account IDs in the provided iterable.
     *
     * @param ids the iterable of account IDs to check.
     * @return true if any Admin exists with the given account IDs, false otherwise.
     */
    boolean existsAnyByAccountIdIn(Iterable<UUID> ids);
}