package com.pug.identity.infra.read;

import com.pug.identity.infra.read.dtos.AccountView;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Queries related to Accounts. */
public interface IAccountQueries {
  /**
   * Finds an AccountView by its ID.
   *
   * @param id the UUID of the AccountView to find.
   * @return an Optional containing the found AccountView, or empty if not found.
   */
  Optional<AccountView> findOptionalById(UUID id);

  /**
   * Finds an AccountView by its email.
   *
   * @param email the email of the AccountView to find.
   * @return an Optional containing the found AccountView, or empty if not found.
   */
  Optional<AccountView> findOptionalByEmail(String email);

  /**
   * Lists all AccountView objects.
   *
   * @return a list of all AccountView objects.
   */
  List<AccountView> listAllAccounts();

  /**
   * Lists AccountView objects by CPF.
   *
   * @param cpf the CPF to filter AccountView objects.
   * @return a list of AccountView objects matching the given CPF.
   */
  List<AccountView> listByCpf(String cpf);

  /**
   * Searches for AccountView objects by name (of the associated user).
   *
   * @param key the name key to search for.
   * @return a list of AccountView objects matching the search key.
   */
  List<AccountView> searchByName(String key);
}
