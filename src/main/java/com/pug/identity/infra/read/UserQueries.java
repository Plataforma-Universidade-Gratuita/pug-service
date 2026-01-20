package com.pug.identity.infra.read;

import com.pug.identity.infra.read.dtos.UserView;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * UserQueries interface for querying user-related data.
 */
public interface UserQueries {

  /**
   * Finds a UserView by its ID.
   *
   * @param id the ID of the User to find
   * @return an Optional containing the UserView if found, or empty if not found
   */
  Optional<UserView> findOptionalById(UUID id);

  /**
   * Finds a UserView by its CPF.
   *
   * @param cpf the CPF of the User to find
   * @return an Optional containing the UserView if found, or empty if not found
   */
  Optional<UserView> findOptionalByCpf(String cpf);

  /**
   * Lists all UserViews.
   *
   * @return a list of all UserViews
   */
  List<UserView> listAllUsers();

  /**
   * Searches for UserViews by their name.
   *
   * @param key the name key to search for
   * @return a list of UserViews matching the name key
   */
  List<UserView> searchByName(String key);
}