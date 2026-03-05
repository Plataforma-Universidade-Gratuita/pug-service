package com.pug.identity.service;

import com.pug.identity.domain.User;
import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.service.dtos.UserCreateCommand;
import com.pug.identity.service.dtos.UserUpdateCommand;
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
public interface UserService {

  /**
   * Instantiates and persists a new {@link User} aggregate based on the provided command.
   *
   * <p>The command data is routed through the domain's factory methods to ensure all internal
   * validations (e.g., CPF mathematical validation) are strictly applied before persistence.
   *
   * @param cmd the structured command containing the data required to create a new user
   * @return the fully instantiated and persisted {@link User} aggregate
   * @throws com.pug.shared.exceptions.DuplicateResourceException if a user with the provided CPF
   *     already exists
   * @throws com.pug.shared.exceptions.AppValidationException if the input data violates domain
   *     constraints (e.g., blank name, malformed CPF)
   */
  User save(UserCreateCommand cmd);

  /**
   * Updates the state (name and/or CPF) of an existing {@link User} aggregate.
   *
   * <p>This method reconstitutes the aggregate from the repository, applies the requested mutations
   * through domain behaviors, and persists the updated state.
   *
   * @param id the unique identifier (UUIDv7) of the user to update
   * @param cmd the structured command containing the updated user data
   * @return the mutated and persisted {@link User} aggregate
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the user cannot be found in the
   *     repository
   * @throws com.pug.shared.exceptions.DuplicateResourceException if the updated CPF conflicts with
   *     an existing user
   * @throws com.pug.shared.exceptions.AppValidationException if the updated input data violates
   *     domain constraints
   */
  User update(UUID id, UserUpdateCommand cmd);

  /**
   * Removes a {@link User} from the system by its unique identifier.
   *
   * @param id the unique identifier (UUID) of the user to delete
   * @return {@code true} if the user was successfully deleted, {@code false} if the ID is null or
   *     if the actual database deletion was silently ignored (e.g., an idempotent concurrent
   *     delete)
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
   * Retrieves a full {@link User} domain aggregate by its unique identifier.
   *
   * <p><b>Note:</b> This method is intended strictly for internal domain orchestration (e.g.,
   * loading an aggregate to mutate it or link it). For API responses, use {@link
   * UserReadService#getViewById(UUID)} instead.
   *
   * @param id the unique identifier (UUID) of the user
   * @return the fully reconstituted {@link User} aggregate
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the user does not exist
   * @throws com.pug.shared.exceptions.AppValidationException if the user exists in the database but
   *     its stored state currently violates strict domain invariants (data corruption)
   */
  User getById(UUID id);

  /**
   * Retrieves a full {@link User} domain aggregate by its associated CPF.
   *
   * @param cpf the previously validated {@link Cpf} Value Object
   * @return the fully reconstituted {@link User} aggregate
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the user does not exist
   * @throws com.pug.shared.exceptions.AppValidationException if the user exists in the database but
   *     its stored state currently violates strict domain invariants (data corruption)
   */
  User getByCpf(Cpf cpf);

  /**
   * Checks whether a user exists with the specified CPF.
   *
   * @param cpf the previously validated {@link Cpf} Value Object to check
   * @return {@code true} if a matching user exists, {@code false} otherwise
   */
  boolean existsByCpf(Cpf cpf);
}
