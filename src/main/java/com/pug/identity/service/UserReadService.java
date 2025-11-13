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
import java.util.Map;
import java.util.UUID;

/** Read-only service for user views. */
@ApplicationScoped
public class UserReadService {

  @Inject UserQueries queries;

  /**
   * Retrieves a UserView by its unique identifier.
   *
   * @param id the UUID of the user
   * @return the UserView
   * @throws ResourceNotFoundException if no user with the given ID is found
   */
  public UserView getViewById(UUID id) {
    return queries
        .findOptionalById(id)
        .orElseThrow(
            () ->
                new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND, Map.of("id", id)));
  }

  /**
   * Retrieves a UserView by its CPF.
   *
   * @param cpf the CPF of the user
   * @return the UserView
   * @throws ResourceNotFoundException if no user with the given CPF is found
   */
  public UserView getViewByCpf(String cpf) {
    return queries
        .findOptionalByCpf(cpf)
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    IdentityErrorCodes.USER_NOT_FOUND, Map.of("cpf", cpf)));
  }

  /**
   * Lists all UserViews.
   *
   * @return a list of all UserViews
   */
  public List<UserView> listViews() {
    return queries.listAllUsers();
  }

  /**
   * Searches for UserViews by name.
   *
   * @param query the search query
   * @return a list of UserViews matching the search query
   */
  public List<UserView> search(String query) {
    String key = StringUtils.fold(query).toLowerCase(Locale.ROOT);
    return queries.searchByName(key);
  }
}
