package com.pug.identity.service.impl;

import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.identity.infra.read.AdminQueries;
import com.pug.identity.infra.read.dtos.AdminView;
import com.pug.identity.service.AdminReadService;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.UUID;

/**
 * Service for reading admin data.
 */
@ApplicationScoped
public class AdminReadServiceImpl implements AdminReadService {

  private static final Logger LOG = Logger.getLogger(AdminReadServiceImpl.class);

  @Inject
  AdminQueries queries;

  @Override
  public AdminView getViewByAccountId(UUID accountId) {
    return queries
            .findOptionalById(accountId)
            .orElseThrow(() -> {
              LOG.debugf("Admin lookup failed: Account ID %s not found", accountId);
              return new ResourceNotFoundException(
                      IdentityErrorCodes.ADMIN_NOT_FOUND,
                      "accountId",
                      accountId.toString()
              );
            });
  }

  @Override
  public AdminView getViewByEmail(String email) {
    if (StringUtils.isEmpty(email)) {
      throw new ResourceNotFoundException(
              IdentityErrorCodes.ADMIN_NOT_FOUND,
              "email",
              "empty"
      );
    }

    return queries
            .findOptionalByEmail(email)
            .orElseThrow(() -> {
              LOG.debugf("Admin lookup failed: Email %s not found", email);
              return new ResourceNotFoundException(
                      IdentityErrorCodes.ADMIN_NOT_FOUND,
                      "email",
                      email
              );
            });
  }

  @Override
  public List<AdminView> listViews() {
    return queries.listAllAdmins();
  }

  @Override
  public List<AdminView> listViewsByCpf(String cpf) {
    if (StringUtils.isEmpty(cpf)) {
      return List.of();
    }
    return queries.listByCpf(cpf);
  }

  @Override
  public List<AdminView> search(String query) {
    String key = StringUtils.fold(query);
    return queries.searchByName(key);
  }
}