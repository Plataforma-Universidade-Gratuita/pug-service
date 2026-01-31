package com.pug.identity.service.impl;

import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.infra.read.UserQueries;
import com.pug.identity.infra.read.dtos.UserView;
import com.pug.identity.service.UserReadService;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Read-only service for user views. */
@ApplicationScoped
public class UserReadServiceImpl implements UserReadService {

  @Inject UserQueries queries;

  @Override
  public UserView getViewById(UUID id) {
    return queries
        .findOptionalById(id)
        .orElseThrow(
            () ->
                new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND, Map.of("id", id)));
  }

  @Override
  public UserView getViewByCpf(String cpf) {
    if (StringUtils.isEmpty(cpf)) {
      throw new ResourceNotFoundException(IdentityErrorCodes.USER_NOT_FOUND, Map.of("cpf", cpf));
    }
    return queries
        .findOptionalByCpf(cpf)
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    IdentityErrorCodes.USER_NOT_FOUND, Map.of("cpf", cpf)));
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
