package com.pug.identity.service.impl;

import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.infra.read.IAccountQueries;
import com.pug.identity.infra.read.dtos.AccountView;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Read-only service for account views.
 */
@ApplicationScoped
public class AccountReadService {

  @Inject
  IAccountQueries queries;

  /**
   * Retrieves an AccountView by its unique identifier.
   *
   * @param id the UUID of the account
   * @return the AccountView
   * @throws ResourceNotFoundException if no account with the given ID is found
   */
  public AccountView getViewById(UUID id) {
    return queries
            .findOptionalById(id)
            .orElseThrow(
                    () -> new ResourceNotFoundException(IdentityErrorCodes.ACCOUNT_NOT_FOUND, Map.of("id", id)));
  }

  /**
   * Retrieves an AccountView by its email.
   *
   * @param email the email of the account
   * @return the AccountView
   * @throws ResourceNotFoundException if no account with the given email is found
   */
  public AccountView getViewByEmail(String email) {
    return queries
            .findOptionalByEmail(email)
            .orElseThrow(
                    () -> new ResourceNotFoundException(IdentityErrorCodes.ACCOUNT_NOT_FOUND, Map.of("email", email)));
  }

  /**
   * Lists all AccountViews.
   *
   * @return a list of all AccountViews
   */
  public List<AccountView> listViews() {
    return queries.listAllAccounts();
  }

  /**
   * Lists AccountViews by CPF.
   *
   * @param cpf the CPF to filter accounts
   * @return the list of AccountViews matching the given CPF
   */
  public List<AccountView> listViewsByCpf(String cpf) {
    return queries.listByCpf(cpf);
  }

  /**
   * Searches for AccountViews by name (of the associated user).
   *
   * @param query the search query
   * @return a list of AccountViews matching the search query
   */
  public List<AccountView> search(String query) {
    String key = StringUtils.fold(query);
    return queries.searchByName(key);
  }
}