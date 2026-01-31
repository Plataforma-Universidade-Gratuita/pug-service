package com.pug.identity.service;

import com.pug.identity.infra.read.dtos.AccountView;
import java.util.List;
import java.util.UUID;

/** Interface for reading account views. */
public interface AccountReadService {

  /**
   * Retrieves an AccountView by its unique identifier.
   *
   * @param id the UUID of the account
   * @return the AccountView
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if no account with the given ID is
   *     found
   */
  AccountView getViewById(UUID id);

  /**
   * Retrieves an AccountView by its email.
   *
   * @param email the email of the account
   * @return the AccountView
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if no account with the given email
   *     is found
   */
  AccountView getViewByEmail(String email);

  /**
   * Lists all AccountViews.
   *
   * @return a list of all AccountViews
   */
  List<AccountView> listViews();

  /**
   * Lists AccountViews by CPF.
   *
   * @param cpf the CPF to filter accounts
   * @return the list of AccountViews matching the given CPF
   */
  List<AccountView> listViewsByCpf(String cpf);

  /**
   * Searches for AccountViews by name (of the associated user).
   *
   * @param query the search query
   * @return a list of AccountViews matching the search query
   */
  List<AccountView> search(String query);
}
