/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.identity.service.impl;

import br.org.catolicasc.pug.identity.infra.read.AccountsQueries;
import br.org.catolicasc.pug.identity.infra.read.dtos.AccountComplexSearchView;
import br.org.catolicasc.pug.identity.infra.read.dtos.AccountView;
import br.org.catolicasc.pug.identity.service.AccountsReadService;
import br.org.catolicasc.pug.identity.service.dtos.accounts.AccountComplexSearchCriteria;
import br.org.catolicasc.pug.identity.service.utils.ExceptionHelper;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import br.org.catolicasc.pug.shared.utils.CollectionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

/**
 * Implementation of the {@link AccountsReadService}.
 *
 * <p>This application-scoped bean delegates read-only operations to the underlying {@link
 * AccountsQueries} infrastructure component. It handles basic input validation and translates "not
 * found" states into standardized domain exceptions.
 */
@ApplicationScoped
public class AccountsReadServiceImpl implements AccountsReadService {

  private static final Logger LOG = Logger.getLogger(AccountsReadServiceImpl.class);

  @Inject AccountsQueries queries;

  /** {@inheritDoc} */
  @Override
  public AccountView getViewById(UUID id) {
    return queries
        .findOptionalById(id)
        .orElseThrow(
            () -> {
              LOG.debugf("Account lookup failed: ID %s not found", id);
              return ExceptionHelper.accountNotFound();
            });
  }

  /** {@inheritDoc} */
  @Override
  public List<AccountView> listViews() {
    return queries.listAllAccounts();
  }

  /** {@inheritDoc} */
  @Override
  public List<AccountView> listViewsByIds(List<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return List.of();
    }
    return queries.listAllByIds(ids);
  }

  /** {@inheritDoc} */
  @Override
  public PageResult<AccountComplexSearchView> search(
      PageQuery pageQuery, AccountComplexSearchCriteria criteria) {
    return queries.search(pageQuery, criteria);
  }
}
