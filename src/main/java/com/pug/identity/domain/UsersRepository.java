package com.pug.identity.domain;

import com.pug.identity.infra.persistence.UsersEntity;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Repository interface for managing UsersEntity objects. */
public interface UsersRepository {

  /**
   * Persists a UsersEntity object.
   *
   * @param entity the UsersEntity to persist.
   */
  void persist(UsersEntity entity);

  /**
   * Persists multiple UsersEntity objects.
   *
   * @param entities the iterable collection of UsersEntity objects to persist.
   */
  void persistAll(Iterable<UsersEntity> entities);

  /**
   * Deletes UsersEntity objects by their IDs.
   *
   * @param ids the iterable collection of UUIDs representing the IDs of the UsersEntity objects to
   *     delete.
   * @return the number of entities deleted.
   */
  long deleteByIds(Iterable<UUID> ids);

  /**
   * Finds a UsersEntity by its ID.
   *
   * @param id the UUID of the UsersEntity to find.
   * @return an Optional containing the found UsersEntity, or empty if not found.
   */
  Optional<UsersEntity> findOptionalById(UUID id);

  /**
   * Finds a UsersEntity by its email.
   *
   * @param email the email of the UsersEntity to find.
   * @return an Optional containing the found UsersEntity, or empty if not found.
   */
  Optional<UsersEntity> findOptionalByEmail(String email);

  /**
   * Lists all UsersEntity objects.
   *
   * @return a list of all UsersEntity objects.
   */
  List<UsersEntity> listAllUsers();

  /**
   * Lists UsersEntity objects by CPF.
   *
   * @param cpf the CPF to filter UsersEntity objects.
   * @return a list of UsersEntity objects matching the given CPF.
   */
  List<UsersEntity> listByCpf(String cpf);

  /**
   * Searches for UsersEntity objects by name.
   *
   * @param key the name key to search for.
   * @return a list of UsersEntity objects matching the search key.
   */
  List<UsersEntity> searchByName(String key);

  /**
   * Checks if a UsersEntity exists by email.
   *
   * @param email the email to check for existence.
   * @return true if a UsersEntity with the given email exists, false otherwise.
   */
  boolean existsByEmail(String email);

  /**
   * Checks if any UsersEntity exists with emails in the given collection.
   *
   * @param emails the collection of emails to check for existence.
   * @return true if any UsersEntity with emails in the collection exists, false otherwise.
   */
  boolean existsAnyByEmailIn(Collection<String> emails);

  /**
   * Deactivates a UsersEntity by its ID.
   *
   * @param id the UUID of the UsersEntity to deactivate.
   */
  void deactivateById(UUID id);
}
