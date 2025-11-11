package com.pug.identity.service;

import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.infra.read.UserQueries;
import com.pug.identity.infra.read.dtos.UserView;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/** Service for reading user data. */
@ApplicationScoped
public class UserReadService {

  @Inject UserQueries queries;

  /**
   * Gets the user view by ID.
   *
   * @param id the user ID.
   * @return the user view.
   * @throws ResourceNotFoundException if the user is not found.
   */
  public UserView getView(UUID id) {
    return queries
        .findOptionalById(id)
        .orElseThrow(() -> new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND));
  }

  /**
   * Gets the user view by email.
   *
   * @param email the user email.
   * @return the user view.
   * @throws ResourceNotFoundException if the user is not found.
   */
  public UserView getByEmail(String email) {
    return queries
        .findOptionalByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND));
  }

  /**
   * Lists all user views.
   *
   * @return the list of user views.
   */
  public List<UserView> listViews() {
    return queries.listAllUsers();
  }

  /**
   * Lists user views by CPF.
   *
   * @param cpf the user CPF.
   * @return the list of user views with the given CPF.
   */
  public List<UserView> listByCpf(String cpf) {
    return queries.listByCpf(cpf);
  }

  /**
   * Searches for users by name.
   *
   * @param query the search query.
   * @return the list of user views matching the query.
   */
  public List<UserView> search(String query) {
    String key = StringUtils.fold(query).toLowerCase(Locale.ROOT);
    return queries.searchByName(key);
  }
}
