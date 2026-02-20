package com.pug.identity.service.impl;

import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.infra.read.AccountQueries;
import com.pug.identity.infra.read.dtos.AccountView;
import com.pug.identity.service.AccountReadService;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

/** Read-only service for account views. */
@ApplicationScoped
public class AccountReadServiceImpl implements AccountReadService {

  private static final Logger LOG = Logger.getLogger(AccountReadServiceImpl.class);

  @Inject AccountQueries queries;

  @Override
  public AccountView getViewById(UUID id) {
    return queries
        .findOptionalById(id)
        .orElseThrow(
            () -> {
              LOG.debugf("Account lookup failed: ID %s not found", id);
              return new ResourceNotFoundException(
                  IdentityErrorCodes.ACCOUNT_NOT_FOUND, "id", id.toString());
            });
  }

  @Override
  public AccountView getViewByEmail(String email) {
    if (StringUtils.isEmpty(email)) {
      throw new ResourceNotFoundException(IdentityErrorCodes.ACCOUNT_NOT_FOUND, "email", "empty");
    }

    return queries
        .findOptionalByEmail(email)
        .orElseThrow(
            () -> {
              LOG.debugf("Account lookup failed: Email %s not found", email);
              return new ResourceNotFoundException(
                  IdentityErrorCodes.ACCOUNT_NOT_FOUND, "email", email);
            });
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
