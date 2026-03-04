package com.pug.identity.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository interface for managing {@link Account} aggregate roots.
 * <p>
 * This interface defines the contract for persisting, retrieving, updating, and deleting
 * account entities. It abstracts the underlying data storage mechanism to maintain
 * a pure, infrastructure-agnostic domain model within the Identity context.
 */
public interface AccountRepository {

  /**
   * Persists a newly created {@link Account} aggregate into the repository.
   *
   * @param entity the {@link Account} aggregate to persist
   * @return the fully persisted {@link Account} instance
   */
  Account persist(Account entity);

  /**
   * Updates the state of an existing {@link Account} aggregate in the repository.
   *
   * @param entity the {@link Account} instance containing the updated state
   */
  void update(Account entity);

  /**
   * Removes an {@link Account} from the repository based on its unique identifier.
   *
   * @param id the unique identifier (UUIDv7) of the account to delete
   * @return {@code true} if the account was successfully deleted, {@code false} if it was not found
   */
  boolean deleteById(UUID id);

  /**
   * Removes multiple {@link Account} entities from the repository based on their unique identifiers.
   *
   * @param ids a list of UUIDs representing the accounts to delete
   * @return the total number of accounts that were successfully deleted
   */
  long deleteAllByIds(List<UUID> ids);

  /**
   * Retrieves an {@link Account} by its unique identifier.
   * <p>
   * When an account is reconstituted from the persistence layer, it typically undergoes
   * the same domain validations as a newly created entity. Therefore, the returned {@link Account}
   * might contain validation errors (verifiable via {@link Account#hasFieldErrors()})
   * if the stored data violates current domain rules.
   *
   * @param id the unique identifier (UUID) of the account
   * @return an {@link Optional} containing the {@link Account} if found, or {@link Optional#empty()} if not
   */
  Optional<Account> findOptionalById(UUID id);

  /**
   * Retrieves the linked user identifiers for a given list of account IDs.
   * <p>
   * This is primarily used to map backward from accounts to their owning users.
   *
   * @param ids a list of account UUIDs
   * @return a list of user UUIDs associated with the provided account IDs
   */
  List<UUID> findUserIdsByIds(List<UUID> ids);

  /**
   * Identifies and returns all user IDs from the provided list that are considered
   * "orphaned" (i.e., they have no associated {@link Account} records attached to them).
   *
   * @param userIds a list of user UUIDs to check for orphan status
   * @return a list of user UUIDs that currently have zero associated accounts
   */
  List<UUID> findAllOrphanUserIdsByUserIds(List<UUID> userIds);

  /**
   * Calculates the total number of {@link Account} records associated with a specific user identifier.
   *
   * @param userId the unique identifier of the user
   * @return the total count of accounts owned by the specified user
   */
  long countAllAccountsByUserId(UUID userId);

  /**
   * Checks whether an {@link Account} with the specified email address already exists in the repository.
   * <p>
   * This is used by domain services to enforce natural key uniqueness constraints
   * (e.g., preventing duplicate account registrations with the same email).
   *
   * @param email the email address string to check
   * @return {@code true} if an account with the given email exists, {@code false} otherwise
   */
  boolean existsByEmail(String email);
}