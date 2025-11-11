package com.pug.identity.infra.read;

import com.pug.identity.infra.read.dtos.UserView;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** UserQueries interface for querying user-related data. */
public interface UserQueries {

  /**
   * Finds a User by their ID.
   *
   * @param id the ID of the User to find
   * @return an Optional containing the User if found, or empty if not found
   */
  Optional<UserView> findOptionalById(UUID id);

  /**
   * Finds a User by their CPF.
   *
   * @param cpf the CPF of the User to find
   * @return an Optional containing the User if found, or empty if not found
   */
  Optional<UserView> findOptionalByCpf(String cpf);

  /**
   * Lists all Users.
   *
   * @return a list of all Users
   */
  List<UserView> listAllUsers();

  /**
   * Searches for Users by their name.
   *
   * @param key the name key to search for
   * @return a list of Users matching the name key
   */
  List<UserView> searchByName(String key);
}
