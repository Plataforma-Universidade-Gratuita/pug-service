package com.pug.identity.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Repository interface for managing Person objects. */
public interface UserRepository {

    /**
     * Persists a Person entity.
     *
     * @param entity the Person entity to persist
     * @return the persisted Person entity
     */
    User persist(User entity);

    /**
     * Persists multiple Person entities.
     *
     * @param entities the Person entities to persist
     * @return a list of persisted Person entities
     */
    List<User> persistAll(Iterable<User> entities);

    /**
     * Updates a Person entity.
     *
     * @param entity the Person entity to update
     */
    void update(User entity);

    /**
     * Deletes Person entities by their IDs.
     *
     * @param ids the IDs of the Person entities to delete
     * @return the number of entities deleted
     */
    long deleteByIds(Iterable<UUID> ids);

    /**
     * Finds a Person by their ID.
     *
     * @param id the ID of the Person to find
     * @return an Optional containing the Person if found, or empty if not found
     */
    Optional<User> findOptionalById(UUID id);

    /**
     * Lists all People by their IDs.
     *
     * @return a list of all People
     */
    List<User> listAllPeople();

    /**
     * Checks if a Person exists with the given CPF.
     *
     * @param cpf the CPF to check
     * @return true if a Person exists with the given CPF, false otherwise
     */
    boolean existsByCpf(String cpf);

    /**
     * Checks if any Person exists with a CPF in the given collection.
     *
     * @param cpfs the collection of CPFs to check
     * @return true if any Person exists with a CPF in the collection, false otherwise
     */
    boolean existsAnyByCpfIn(Iterable<String> cpfs);
}
