package com.pug.identity.service.impl;

import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.infra.read.IAdminQueries;
import com.pug.identity.infra.read.dtos.AdminView;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for reading admin data.
 */
@ApplicationScoped
public class AdminReadService {

  @Inject
  IAdminQueries queries;

  /**
   * Retrieves an AdminView by its account ID.
   *
   * @param accountId the account ID of the admin.
   * @return the AdminView.
   * @throws ResourceNotFoundException if no admin with the given account ID is found.
   */
  public AdminView getViewById(UUID accountId) {
    return queries
            .findOptionalById(accountId)
            .orElseThrow(
                    () -> new ResourceNotFoundException(IdentityErrorCodes.ADMIN_NOT_FOUND, Map.of("accountId", accountId)));
  }

  /**
   * Retrieves an AdminView by its email.
   *
   * @param email the email of the admin.
   * @return the AdminView.
   * @throws ResourceNotFoundException if no admin with the given email is found.
   */
  public AdminView getViewByEmail(String email) {
    return queries
            .findOptionalByEmail(email)
            .orElseThrow(
                    () -> new ResourceNotFoundException(IdentityErrorCodes.ADMIN_NOT_FOUND, Map.of("email", email)));
  }

  /**
   * Lists all AdminViews.
   *
   * @return a list of all AdminViews.
   */
  public List<AdminView> listViews() {
    return queries.listAllAdmins();
  }

  /**
   * Lists AdminViews by CPF.
   *
   * @param cpf the CPF to filter by.
   * @return a list of AdminViews matching the given CPF.
   */
  public List<AdminView> listViewsByCpf(String cpf) {
    return queries.listByCpf(cpf);
  }

  /**
   * Searches for AdminViews by name (of the associated user).
   *
   * @param query the search query.
   * @return a list of matching AdminViews.
   */
  public List<AdminView> search(String query) {
    String key = StringUtils.fold(query);
    return queries.searchByName(key);
  }
}