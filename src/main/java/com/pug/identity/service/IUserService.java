package com.pug.identity.service;

import com.pug.identity.domain.User;
import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.service.dtos.UserCreateOrUpdateCommand;
import com.pug.shared.domain.enums.DeleteKeys;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Interface for managing user entities.
 */
public interface IUserService {

    /**
     * Helper method for AccountService to process users in bulk (find or create)
     * and collect validation problems.
     *
     * @param rawCpfStrings List of raw CPF strings from commands.
     * @param problems      List to collect AppValidationException.Problem instances.
     * @return Map of Cpf VO to User UUID for successfully processed users.
     */
    Map<Cpf, UUID> processUsersForAccounts(List<String> rawCpfStrings, List<com.pug.shared.exceptions.AppValidationException.Problem> problems);

    /**
     * Creates and saves a new User with the given CPF and name.
     *
     * @param cmd the command containing the data to create the new User
     * @return the saved User entity
     * @throws com.pug.shared.exceptions.DuplicateResourceException if a user with the same CPF already exists
     * @throws com.pug.shared.exceptions.AppValidationException     if input validation fails (e.g., blank name, invalid CPF).
     */
    User save(UserCreateOrUpdateCommand cmd);

    /**
     * Saves multiple User entities.
     *
     * @param cmds the iterable of CreateNewUserCommand containing data to create new Users
     * @return a list of saved User entities
     * @throws com.pug.shared.exceptions.DuplicateResourceException if any user CPF is duplicated in the input or already exists
     * @throws com.pug.shared.exceptions.AppValidationException     if input validation fails for any user in the bulk.
     */
    List<User> saveAll(Iterable<UserCreateOrUpdateCommand> cmds);

    /**
     * Updates an existing User with the given ID using the provided data.
     *
     * @param id  the UUID of the user to update
     * @param cmd the command containing the data to update the User
     * @return the updated User entity
     * @throws com.pug.shared.exceptions.ResourceNotFoundException  if the user with the given ID does not exist (or data is corrupted in DB).
     * @throws com.pug.shared.exceptions.DuplicateResourceException if a user with the updated CPF already exists.
     * @throws com.pug.shared.exceptions.AppValidationException     if input validation fails for user data.
     */
    User update(UUID id, UserCreateOrUpdateCommand cmd);

    /**
     * Deletes users by their IDs.
     *
     * @param ids the IDs of the users to delete
     * @return a map containing the count of deleted users
     * @throws com.pug.shared.exceptions.ReferencedEntityException if any user is still referenced by an account
     */
    Map<DeleteKeys, Long> deleteAll(Iterable<UUID> ids);

    /**
     * Lists all users.
     *
     * @return a list of all User entities
     * @throws com.pug.shared.exceptions.AppValidationException if any User entity found is corrupted in the database.
     */
    List<User> listAll();

    /**
     * Retrieves a User by its ID.
     *
     * @param id the UUID of the user
     * @return the User entity
     * @throws com.pug.shared.exceptions.ResourceNotFoundException if the user with the given ID does not exist (or data is corrupted in DB).
     */
    User getById(UUID id);

    /**
     * Retrieves a User by its CPF.
     *
     * @param cpf the CPF of the user (already a validated Value Object).
     * @return the User entity
     * @throws com.pug.shared.exceptions.ResourceNotFoundException if the user with the given CPF does not exist (or data is corrupted in DB).
     */
    User getByCpf(Cpf cpf);

    /**
     * Retrieves all Users by their CPFs.
     *
     * @param cpfs an iterable of CPFs (already validated Value Objects).
     * @return a list of User entities
     * @throws com.pug.shared.exceptions.AppValidationException if any User entity found is corrupted in the database.
     */
    List<User> getAllByCpf(Iterable<Cpf> cpfs);

    /**
     * Checks if a user exists by CPF.
     *
     * @param cpf the CPF to check (already a validated Value Object).
     * @return true if a user with the given CPF exists, false otherwise.
     */
    boolean existsByCpf(Cpf cpf);

    /**
     * Checks if any user exists with the given CPFs.
     *
     * @param cpfs the list of CPFs (as strings) to check.
     * @return true if any user with the given CPFs exists, false otherwise.
     */
    boolean existsAnyByCpfIn(List<String> cpfs);
}