package com.pug.identity.infra.read;

import com.pug.identity.infra.read.dtos.UserView;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * PersonQueries interface for querying person-related data.
 */
public interface UserQueries {

    /**
     * Finds a Person by their ID.
     *
     * @param id the ID of the Person to find
     * @return an Optional containing the Person if found, or empty if not found
     */
    Optional<UserView> findOptionalById(UUID id);

    /**
     * Finds a Person by their CPF.
     *
     * @param cpf the CPF of the Person to find
     * @return an Optional containing the Person if found, or empty if not found
     */
    Optional<UserView> findOptionalByCpf(String cpf);

    /**
     * Lists all People.
     *
     * @return a list of all People
     */
    List<UserView> listAllPeople();

    /**
     * Searches for People by their name.
     *
     * @param key the name key to search for
     * @return a list of People matching the name key
     */
    List<UserView> searchByName(String key);
}
