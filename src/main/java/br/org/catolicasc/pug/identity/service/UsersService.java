/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.identity.service;

import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.identity.domain.vos.Cpf;
import br.org.catolicasc.pug.identity.service.dtos.users.UserCreateCommand;
import br.org.catolicasc.pug.identity.service.dtos.users.UserUpdateCommand;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import br.org.catolicasc.pug.shared.exceptions.DuplicateResourceException;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import java.util.List;
import java.util.UUID;

/**
 * Application service interface for managing the state of {@link User} domain aggregates.
 *
 * <p>Following CQRS principles, this service handles the "Command" operations (Create, Update,
 * Delete) and strict domain-level retrievals. It orchestrates domain logic, enforces business
 * constraints (such as CPF uniqueness), and coordinates with the persistence layer to ensure the
 * integrity of user identity data.
 */
public interface UsersService {

  /**
   * Removes a {@link User} from the system by its unique identifier.
   *
   * @param id the unique identifier (UUID) of the user to delete
   * @return {@code true} if the user was successfully deleted, {@code false} if the ID is null or
   *     if the actual database deletion was silently ignored
   */
  boolean delete(UUID id);

  /**
   * Removes multiple {@link User} entities from the system based on their unique identifiers.
   *
   * @param ids a list of UUIDs representing the users to delete
   * @return the total number of users successfully deleted
   */
  long deleteAll(List<UUID> ids);

  /**
   * Checks whether a user exists with the specified CPF.
   *
   * @param cpf the previously validated {@link Cpf} Value Object to check
   * @return {@code true} if a matching user exists, {@code false} otherwise
   */
  boolean existsByCpf(Cpf cpf);

  /**
   * Retrieves a full {@link User} domain aggregate by its associated CPF.
   *
   * @param cpf the previously validated {@link Cpf} Value Object
   * @return the fully reconstituted {@link User} aggregate
   * @throws ResourceNotFoundException if the user does not exist
   * @throws AppValidationException if the user violates domain rules
   */
  User getByCpf(Cpf cpf);

  /**
   * Retrieves a full {@link User} domain aggregate by its unique identifier.
   *
   * @param id the unique identifier (UUID) of the user
   * @return the fully reconstituted {@link User} aggregate
   * @throws ResourceNotFoundException if the user does not exist
   * @throws AppValidationException if the user violates domain rules
   */
  User getById(UUID id);

  /**
   * Retrieves a collection of {@link User} aggregates corresponding to the provided CPFs.
   *
   * @param cpfs a {@link List} of exact 11-digit numeric CPF strings
   * @return a {@link List} of matching {@link User} instances
   */
  List<User> listByCpfs(List<String> cpfs);

  /**
   * Instantiates and persists a new {@link User} aggregate based on the provided command.
   *
   * @param cmd the structured command containing the data required to create a new user
   * @return the fully instantiated and persisted {@link User} aggregate
   * @throws DuplicateResourceException if a user with the provided CPF already exists
   * @throws AppValidationException if the input data violates domain constraints
   */
  User save(UserCreateCommand cmd);

  /**
   * Instantiates and persists multiple {@link User} aggregates in a single batch transaction.
   *
   * <p>This method drastically reduces JDBC round-trips by pre-validating domain constraints and
   * dispatching a single flush command to the underlying repository.
   *
   * @param cmds a {@link List} of structured commands for the batch users
   * @return a {@link List} of the fully instantiated and persisted {@link User} aggregates
   * @throws DuplicateResourceException if any CPF already exists
   * @throws AppValidationException if input validation fails for any user
   */
  List<User> saveInBulk(List<UserCreateCommand> cmds);

  /**
   * Updates the state (name and/or CPF) of an existing {@link User} aggregate.
   *
   * @param id the unique identifier (UUIDv7) of the user to update
   * @param cmd the structured command containing the updated user data
   * @return the mutated and persisted {@link User} aggregate
   * @throws ResourceNotFoundException if the user cannot be found
   * @throws DuplicateResourceException if the updated CPF conflicts
   * @throws AppValidationException if the updated input data violates domain constraints
   */
  User update(UUID id, UserUpdateCommand cmd);
}
