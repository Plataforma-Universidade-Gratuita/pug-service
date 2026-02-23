package com.pug.identity.service;

import com.pug.identity.domain.User;
import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.service.dtos.UserCreateCommand;
import com.pug.identity.service.dtos.UserUpdateCommand;
import java.util.List;
import java.util.UUID;

/** Interface for managing account entities. */
public interface UserService {

  /**
   * Creates and saves a new User with the given CPF and name.
   *
   * @param cmd the command containing the data to create the new User
   * @return the saved User entity
   * @throws com.pug.shared.exceptions.DuplicateResourceException if a account with the same CPF
   *     already exists
   * @throws com.pug.shared.exceptions.AppValidationException if input validation fails (e.g., blank
   *     name, invalid CPF).
   */
  User save(UserCreateCommand cmd);

  /**
   * Updates an existing User with the given ID using the provided data.
   *
   * @param id the UUID of the account to update
   * @param cmd the command containing the data to update the User
   * @return the updated User entity
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the account with the given ID
   *     does not exist (or data is corrupted in DB).
   * @throws com.pug.shared.exceptions.DuplicateResourceException if a account with the updated CPF
   *     already exists.
   * @throws com.pug.shared.exceptions.AppValidationException if input validation fails for account
   *     data.
   */
  User update(UUID id, UserUpdateCommand cmd);

  /**
   * Deletes a User by its ID.
   *
   * @param id the UUID of the account to delete
   * @return true if the account was successfully deleted, false if the account was not found
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the account with the given ID
   *     does not exist (or data is corrupted in DB).
   */
  boolean delete(UUID id);

  /**
   * Deletes multiple Users by their IDs.
   *
   * @param ids a list of UUIDs of the users to delete
   * @return the number of users successfully deleted
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if any account with the given IDs
   *     does not exist (or data is corrupted in DB).
   */
  long deleteAll(List<UUID> ids);

  /**
   * Retrieves a User by its ID.
   *
   * @param id the UUID of the account
   * @return the User entity
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the account with the given ID
   *     does not exist (or data is corrupted in DB).
   */
  User getById(UUID id);

  /**
   * Retrieves a User by its CPF.
   *
   * @param cpf the CPF of the account (already a validated Value Object).
   * @return the User entity
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the account with the given CPF
   *     does not exist (or data is corrupted in DB).
   */
  User getByCpf(Cpf cpf);

  /**
   * Checks if a account exists by CPF.
   *
   * @param cpf the CPF to check (already a validated Value Object).
   * @return true if a account with the given CPF exists, false otherwise.
   */
  boolean existsByCpf(Cpf cpf);
}
