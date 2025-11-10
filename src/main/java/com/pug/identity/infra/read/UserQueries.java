package com.pug.identity.infra.read;

import com.pug.identity.infra.read.dtos.UserView;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Queries related to Users. */
public interface UserQueries {
  /**
   * Finds a UserView by its ID.
   *
   * @param id the UUID of the UserView to find.
   * @return an Optional containing the found UserView, or empty if not found.
   */
  Optional<UserView> findOptionalById(UUID id);

  /**
   * Finds a UserView by its email.
   *
   * @param email the email of the UserView to find.
   * @return an Optional containing the found UserView, or empty if not found.
   */
  Optional<UserView> findOptionalByEmail(String email);

  /**
   * Lists all UserView objects.
   *
   * @return a list of all UserView objects.
   */
  List<UserView> listAllUsers();

  /**
   * Lists UserView objects by CPF.
   *
   * @param cpf the CPF to filter UserView objects.
   * @return a list of UserView objects matching the given CPF.
   */
  List<UserView> listByCpf(String cpf);

  /**
   * Searches for UserView objects by name.
   *
   * @param key the name key to search for.
   * @return a list of UserView objects matching the search key.
   */
  List<UserView> searchByName(String key);
}
