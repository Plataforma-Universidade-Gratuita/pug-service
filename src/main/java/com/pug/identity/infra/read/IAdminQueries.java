package com.pug.identity.infra.read;

import com.pug.identity.infra.read.dtos.AdminView;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Queries related to Admins. */
public interface IAdminQueries {
  /**
   * Finds an AdminView by its account ID.
   *
   * @param accountId the account ID of the admin.
   * @return an Optional containing the AdminView if found, otherwise empty.
   */
  Optional<AdminView> findOptionalById(UUID accountId);

  /**
   * Finds an AdminView by its email.
   *
   * @param email the email of the admin.
   * @return an Optional containing the AdminView if found, otherwise empty.
   */
  Optional<AdminView> findOptionalByEmail(String email);

  /**
   * Lists all AdminViews.
   *
   * @return a list of all AdminViews.
   */
  List<AdminView> listAllAdmins();

  /**
   * Lists AdminViews by CPF.
   *
   * @param cpf the CPF to filter admins.
   * @return a list of AdminViews matching the given CPF.
   */
  List<AdminView> listByCpf(String cpf);

  /**
   * Searches for AdminViews by name (of the associated user).
   *
   * @param key the name key to search for.
   * @return a list of AdminViews matching the search key.
   */
  List<AdminView> searchByName(String key);
}
