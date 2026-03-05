package com.pug.identity.service.impl;

import com.pug.identity.infra.read.AdminQueries;
import com.pug.identity.infra.read.dtos.AdminView;
import com.pug.identity.service.AdminReadService;
import com.pug.identity.service.utils.ExceptionHelper;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

/**
 * Implementation of the {@link AdminReadService}.
 *
 * <p>This application-scoped bean delegates read-only operations to the underlying {@link
 * AdminQueries} infrastructure component. It handles basic input validation and translates "not
 * found" states into standardized domain exceptions.
 */
@ApplicationScoped
public class AdminReadServiceImpl implements AdminReadService {

  private static final Logger LOG = Logger.getLogger(AdminReadServiceImpl.class);

  @Inject AdminQueries queries;

  /** {@inheritDoc} */
  @Override
  public AdminView getViewByAccountId(UUID accountId) {
    return queries
        .findOptionalById(accountId)
        .orElseThrow(
            () -> {
              LOG.debugf("Admin lookup failed: Account ID %s not found", accountId);
              return ExceptionHelper.adminNotFound();
            });
  }

  /** {@inheritDoc} */
  @Override
  public AdminView getViewByEmail(String email) {
    if (StringUtils.isEmpty(email)) {
      throw ExceptionHelper.adminNotFound();
    }

    return queries
        .findOptionalByEmail(email)
        .orElseThrow(
            () -> {
              LOG.debugf("Admin lookup failed: Email %s not found", email);
              return ExceptionHelper.adminNotFound();
            });
  }

  /** {@inheritDoc} */
  @Override
  public List<AdminView> listViews() {
    return queries.listAllAdmins();
  }

  /** {@inheritDoc} */
  @Override
  public List<AdminView> listViewsByCpf(String cpf) {
    if (StringUtils.isEmpty(cpf)) {
      return List.of();
    }
    return queries.listByCpf(cpf);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Prior to execution, the input query is "folded" (lowercased and accents removed via {@link
   * StringUtils#fold(String)}) to ensure maximum compatibility with the underlying search indexing
   * rules.
   */
  @Override
  public List<AdminView> search(String query) {
    String key = StringUtils.fold(query);
    return queries.searchByName(key);
  }
}
