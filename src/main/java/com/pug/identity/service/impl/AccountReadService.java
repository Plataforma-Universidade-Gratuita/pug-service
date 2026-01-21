package com.pug.identity.service.impl; // Pacote alterado

import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.infra.read.IAccountQueries;
import com.pug.identity.infra.read.dtos.AccountView;
import com.pug.identity.service.IAccountReadService;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Read-only service for account views. */
@ApplicationScoped
public class AccountReadService implements IAccountReadService {

  @Inject IAccountQueries queries;

  @Override
  public AccountView getViewById(UUID id) {
    return queries
        .findOptionalById(id)
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    IdentityErrorCodes.ACCOUNT_NOT_FOUND, Map.of("id", id)));
  }

  @Override
  public AccountView getViewByEmail(String email) {
    if (StringUtils.isEmpty(email)) {
      throw new ResourceNotFoundException(
          IdentityErrorCodes.ACCOUNT_NOT_FOUND, Map.of("email", email));
    }
    return queries
        .findOptionalByEmail(email)
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    IdentityErrorCodes.ACCOUNT_NOT_FOUND, Map.of("email", email)));
  }

  @Override
  public List<AccountView> listViews() {
    return queries.listAllAccounts();
  }

  @Override
  public List<AccountView> listViewsByCpf(String cpf) {
    if (StringUtils.isEmpty(cpf)) {
      return List.of();
    }
    return queries.listByCpf(cpf);
  }

  @Override
  public List<AccountView> search(String query) {
    String key = StringUtils.fold(query);
    return queries.searchByName(key);
  }
}
