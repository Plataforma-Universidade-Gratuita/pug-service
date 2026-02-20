package com.pug.identity.service.impl;

import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.infra.read.UserQueries;
import com.pug.identity.infra.read.dtos.UserView;
import com.pug.identity.service.UserReadService;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of the {@link UserReadService} interface for retrieving user-related information.
 *
 * <p>This service provides methods to get user views by ID or CPF, list all user views, and search
 * for users based on a query string. It handles scenarios where users are not found and ensures
 * that appropriate exceptions are thrown with relevant error codes.
 */
@ApplicationScoped
public class UserReadServiceImpl implements UserReadService {

  private static final Logger LOG = Logger.getLogger(UserReadServiceImpl.class);

  @Inject
  UserQueries queries;

  @Override
  public UserView getViewById(UUID id) {
    return queries
      .findOptionalById(id)
      .orElseThrow(() -> {
        LOG.debugf("User lookup failed: ID %s not found", id);
        return new ResourceNotFoundException(
          IdentityErrorCodes.USER_NOT_FOUND,
          "id",
          id.toString()
        );
      });
  }

  @Override
  public UserView getViewByCpf(String cpf) {
    if (StringUtils.isEmpty(cpf)) {
      throw new ResourceNotFoundException(
        IdentityErrorCodes.USER_NOT_FOUND,
        "cpf",
        "empty"
      );
    }

    return queries
      .findOptionalByCpf(cpf)
      .orElseThrow(() -> {
        LOG.debugf("User lookup failed: CPF %s not found", cpf);
        return new ResourceNotFoundException(
          IdentityErrorCodes.USER_NOT_FOUND,
          "cpf",
          cpf
        );
      });
  }

  @Override
  public List<UserView> listViews() {
    return queries.listAllUsers();
  }

  @Override
  public List<UserView> search(String query) {
    String key = StringUtils.fold(query);
    return queries.searchByName(key);
  }
}