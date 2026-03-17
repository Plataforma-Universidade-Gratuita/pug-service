package com.pug.identity.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository interface for managing {@link Account} aggregate roots.
 *
 * <p>This interface defines the contract for persisting, retrieving, updating, and deleting account
 * entities. It abstracts the underlying data storage mechanism to maintain a pure,
 * infrastructure-agnostic domain model within the Identity context.
 */
public interface AccountRepository {

  /**
   * Calculates the total number of {@link Account} records associated with a specific user
   * identifier.
   *
   * @param userId the unique identifier of the user
   * @return the total count of accounts owned by the specified user
   */
  long countAllAccountsByUserId(UUID userId);

  /**
   * Removes multiple {@link Account} entities from the repository based on their unique
   * identifiers.
   *
   * @param ids a list of UUIDs representing the accounts to delete
   * @return the total number of accounts that were successfully deleted
   */
  long deleteAllByIds(List<UUID> ids);

  /**
   * Removes an {@link Account} from the repository based on its unique identifier.
   *
   * @param id the unique identifier (UUIDv7) of the account to delete
   * @return {@code true} if the account was successfully deleted, {@code false} if it was not found
   */
  boolean deleteById(UUID id);

  /**
   * Checks whether any of the specified email addresses already exist in the repository.
   *
   * <p>This bulk operation is heavily utilized during batch creations to validate uniqueness
   * payloads against the database in a single round-trip.
   *
   * @param emails a {@link List} of normalized email strings
   * @return {@code true} if at least one matching email exists, {@code false} otherwise
   */
  boolean existsAnyByEmails(List<String> emails);

  /**
   * Checks whether an {@link Account} with the specified email address already exists in the
   * repository.
   *
   * <p>This is used by domain services to enforce natural key uniqueness constraints (e.g.,
   * preventing duplicate account registrations with the same email).
   *
   * @param email the email address string to check
   * @return {@code true} if an account with the given email exists, {@code false} otherwise
   */
  boolean existsByEmail(String email);

  /**
   * Identifies and returns all user IDs from the provided list that are considered "orphaned"
   * (i.e., they have no associated {@link Account} records attached to them).
   *
   * @param userIds a list of user UUIDs to check for orphan status
   * @return a list of user UUIDs that currently have zero associated accounts
   */
  List<UUID> findAllOrphanUserIdsByUserIds(List<UUID> userIds);

  /**
   * Retrieves an {@link Account} by its email address.
   *
   * <p>When an account is reconstituted from the persistence layer, it typically undergoes the same
   * domain validations as a newly created entity. Therefore, the returned {@link Account} might
   * contain validation errors (verifiable via {@link Account#hasFieldErrors()}) if the stored data
   * violates current domain rules.
   *
   * @param email the email address string of the account to retrieve
   * @return an {@link Optional} containing the {@link Account} if found, or {@link
   *     Optional#empty()} if not
   */
  Optional<Account> findOptionalByEmail(String email);

  /**
   * Retrieves an {@link Account} by its unique identifier.
   *
   * <p>When an account is reconstituted from the persistence layer, it typically undergoes the same
   * domain validations as a newly created entity. Therefore, the returned {@link Account} might
   * contain validation errors (verifiable via {@link Account#hasFieldErrors()}) if the stored data
   * violates current domain rules.
   *
   * @param id the unique identifier (UUID) of the account
   * @return an {@link Optional} containing the {@link Account} if found, or {@link
   *     Optional#empty()} if not
   */
  Optional<Account> findOptionalById(UUID id);

  /**
   * Retrieves the linked user identifiers for a given list of account IDs.
   *
   * <p>This is primarily used to map backward from accounts to their owning users.
   *
   * @param ids a list of account UUIDs
   * @return a list of user UUIDs associated with the provided account IDs
   */
  List<UUID> findUserIdsByIds(List<UUID> ids);

  /**
   * Persists a newly created {@link Account} aggregate into the repository.
   *
   * @param entity the {@link Account} aggregate to persist
   * @return the fully persisted {@link Account} instance
   */
  Account persist(Account entity);

  /**
   * Persists a collection of newly created {@link Account} aggregates in a single batch.
   *
   * @param accounts a {@link List} of {@link Account} aggregates to persist
   * @return the fully persisted {@link List} of {@link Account} instances
   */
  List<Account> persistAll(List<Account> accounts);

  /**
   * Updates the state of an existing {@link Account} aggregate in the repository.
   *
   * @param entity the {@link Account} instance containing the updated state
   */
  void update(Account entity);
}
