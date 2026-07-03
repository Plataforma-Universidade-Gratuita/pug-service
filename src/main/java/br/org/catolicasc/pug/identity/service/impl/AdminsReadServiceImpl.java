/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.identity.service.impl;

import br.org.catolicasc.pug.identity.infra.read.AdminsQueries;
import br.org.catolicasc.pug.identity.infra.read.dtos.AdminComplexSearchView;
import br.org.catolicasc.pug.identity.infra.read.dtos.AdminView;
import br.org.catolicasc.pug.identity.service.AdminsReadService;
import br.org.catolicasc.pug.identity.service.dtos.admins.AdminComplexSearchCriteria;
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
 * Implementation of the {@link AdminsReadService}.
 *
 * <p>This application-scoped bean delegates read-only operations to the underlying {@link
 * AdminsQueries} infrastructure component. It handles basic input validation and translates "not
 * found" states into standardized domain exceptions.
 */
@ApplicationScoped
public class AdminsReadServiceImpl implements AdminsReadService {

  private static final Logger LOG = Logger.getLogger(AdminsReadServiceImpl.class);

  @Inject AdminsQueries queries;

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
  public AdminView getViewById(UUID accountId) {
    return getViewByAccountId(accountId);
  }

  /** {@inheritDoc} */
  @Override
  public List<AdminView> listViews() {
    return queries.listAllAdmins();
  }

  /** {@inheritDoc} */
  @Override
  public List<AdminView> listViewsByIds(List<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return List.of();
    }
    return queries.listAllByIds(ids);
  }

  /** {@inheritDoc} */
  @Override
  public PageResult<AdminComplexSearchView> search(
      PageQuery pageQuery, AdminComplexSearchCriteria criteria) {
    return queries.search(pageQuery, criteria);
  }
}
