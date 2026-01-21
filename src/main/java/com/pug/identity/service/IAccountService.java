package com.pug.identity.service;

import com.pug.identity.domain.Account;
import com.pug.identity.domain.User;
import com.pug.identity.domain.vos.Email;
import com.pug.identity.service.dtos.AccountCreateCommand;
import com.pug.identity.service.dtos.AccountUpdateCommand;
import com.pug.shared.domain.enums.DeleteKeys;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Interface for managing accounts.
 */
public interface IAccountService {

    /**
     * Creates and saves a new Account.
     *
     * <p>If the associated {@link User} does not exist, it will be created.
     *
     * @param cmd the command containing the data to create the new Account.
     * @return the saved Account.
     * @throws com.pug.shared.exceptions.DuplicateResourceException if an account with the given email already exists.
     * @throws com.pug.shared.exceptions.AppValidationException     if input validation fails (e.g., blank email, invalid CPF).
     */
    Account save(AccountCreateCommand cmd);

    /**
     * Creates and saves multiple new Accounts.
     *
     * <p>If the associated {@link User} entities do not exist, they will be created.
     *
     * @param cmds the commands containing the data to create the new Accounts.
     * @return the list of saved Accounts.
     * @throws com.pug.shared.exceptions.DuplicateResourceException if any account with the given emails already exists or if
     *                                                              there are duplicate emails in the input commands.
     * @throws com.pug.shared.exceptions.AppValidationException     if input validation fails for any account or user in the bulk.
     */
    List<Account> saveAll(Iterable<AccountCreateCommand> cmds);

    /**
     * Updates an existing Account with the given ID using the provided data.
     *
     * @param id  the UUID of the Account to be updated.
     * @param cmd the command containing the data to update the Account.
     * @return the updated Account entity
     * @throws com.pug.shared.exceptions.ResourceNotFoundException  if the account with the given ID does not exist (or data is corrupted in DB).
     * @throws com.pug.shared.exceptions.DuplicateResourceException if an account with the updated email already exists.
     * @throws com.pug.shared.exceptions.AppValidationException     if input validation fails for account or user data.
     */
    Account update(UUID id, AccountUpdateCommand cmd);

    /**
     * Deletes multiple Account entities by their IDs.
     *
     * <p>Also deletes associated User entities if they are not referenced elsewhere.
     *
     * @param ids the iterable of UUIDs representing the IDs of the Account entities to be deleted.
     * @return a map containing the count of deleted Accounts and Users.
     * @throws com.pug.shared.exceptions.ReferencedEntityException if any account is still referenced by Admin, Staff, or
     *                                                             Student entities.
     */
    Map<DeleteKeys, Long> deleteAll(Iterable<UUID> ids);

    /**
     * Lists all Account entities.
     *
     * @return a list of all Account entities.
     * @throws com.pug.shared.exceptions.AppValidationException if any Account entity found is corrupted in the database.
     */
    List<Account> listAll();

    /**
     * Retrieves an Account by its ID.
     *
     * @param id the UUID of the Account.
     * @return the Account entity.
     * @throws com.pug.shared.exceptions.ResourceNotFoundException if the account with the given ID does not exist (or data is corrupted in DB).
     * @throws com.pug.shared.exceptions.AppValidationException    if the account is found but its data is corrupted in the database.
     */
    Account getById(UUID id);

    /**
     * Checks if any account exists with a user ID in the provided list.
     *
     * @param userIds the list of user IDs to check
     * @return true if any account exists with a user ID in the list, false otherwise
     */
    boolean existsByUserIdIn(Iterable<UUID> userIds);

    /**
     * Checks if any account exists with an email with the provided.
     *
     * @param e the emails to check
     * @return true if any account exists with the email, false otherwise
     */
    boolean existsByEmail(Email e);

    /**
     * Checks if any account exists with an email in the provided list.
     *
     * @param emails the list of emails to check
     * @return true if any account exists with an email in the list, false otherwise.
     */
    boolean existsAnyByEmailIn(Iterable<String> emails);
}