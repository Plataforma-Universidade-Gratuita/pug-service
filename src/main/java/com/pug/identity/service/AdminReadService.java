package com.pug.identity.service;

import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.infra.read.AdminQueries;
import com.pug.identity.infra.read.dtos.AdminView;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Service for reading admin data.
 */
@ApplicationScoped
public class AdminReadService {

  @Inject
  AdminQueries queries;

  /**
   * Gets the admin view by account ID.
   *
   * @param accountId the admin's account ID.
   * @return the admin view.
   * @throws ResourceNotFoundException if the admin is not found.
   */
  public AdminView getView(UUID accountId) {
    return queries
            .findOptionalById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException(IdentityErrorCodes.ADMIN_NOT_FOUND));
  }

  /**
   * Gets the admin view by email.
   *
   * @param email the admin email.
   * @return the admin view.
   * @throws ResourceNotFoundException if the admin is not found.
   */
  public AdminView getViewByEmail(String email) {
    return queries
            .findOptionalByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException(IdentityErrorCodes.ADMIN_NOT_FOUND));
  }

  /**
   * Lists all admin views.
   *
   * @return the list of admin views.
   */
  public List<AdminView> listViews() {
    return queries.listAllAdmins();
  }

  /**
   * Lists admin views by CPF.
   *
   * @param cpf the CPF to filter by.
   * @return the list of admin views.
   */
  public List<AdminView> listViewsByCpf(String cpf) {
    return queries.listByCpf(cpf);
  }

  /**
   * Searches admin views by username (folded/lowercased key).
   *
   * @param query the search query.
   * @return the list of matching admin views.
   */
  public List<AdminView> search(String query) {
    String key = StringUtils.fold(query).toLowerCase(Locale.ROOT);
    return queries.searchByName(key);
  }
}
