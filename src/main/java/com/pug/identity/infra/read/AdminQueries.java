package com.pug.identity.infra.read;

import com.pug.identity.infra.read.dtos.AdminView;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Queries related to Admins. */
public interface AdminQueries {
  /**
   * Find AdminView by accountId.
   *
   * @param accountId the account ID of the admin.
   * @return an Optional containing the AdminView if found, otherwise empty.
   */
  Optional<AdminView> findOptionalById(UUID accountId);

  /**
   * Finds a AdminView by its email.
   *
   * @param email the email of the AdminView to find.
   * @return an Optional containing the found AdminView, or empty if not found.
   */
  Optional<AdminView> findOptionalByEmail(String email);

  /**
   * List all AdminViews.
   *
   * @return a list of all AdminViews.
   */
  List<AdminView> listAllAdmins();

  /**
   * Lists AdminView objects by CPF.
   *
   * @param cpf the CPF to filter AdminView objects.
   * @return a list of AdminView objects matching the given CPF.
   */
  List<AdminView> listByCpf(String cpf);

  /**
   * Searches for AdminView objects by name.
   *
   * @param key the name key to search for.
   * @return a list of AdminView objects matching the search key.
   */
  List<AdminView> searchByName(String key);
}
