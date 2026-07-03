/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.identity.service.impl;

import br.org.catolicasc.pug.identity.infra.read.UsersQueries;
import br.org.catolicasc.pug.identity.infra.read.dtos.UserView;
import br.org.catolicasc.pug.identity.service.UsersReadService;
import br.org.catolicasc.pug.identity.service.dtos.users.UserComplexSearchCriteria;
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
 * Implementation of the {@link UsersReadService}.
 *
 * <p>This application-scoped bean delegates read-only operations to the underlying {@link
 * UsersQueries} infrastructure component. It handles basic input validation and translates "not
 * found" states into standardized domain exceptions.
 */
@ApplicationScoped
public class UsersReadServiceImpl implements UsersReadService {

  private static final Logger LOG = Logger.getLogger(UsersReadServiceImpl.class);

  @Inject UsersQueries queries;

  /** {@inheritDoc} */
  @Override
  public UserView getViewById(UUID id) {
    return queries
        .findOptionalById(id)
        .orElseThrow(
            () -> {
              LOG.debugf("User lookup failed: ID %s not found", id);
              return ExceptionHelper.userNotFound();
            });
  }

  /** {@inheritDoc} */
  @Override
  public List<UserView> listViews() {
    return queries.listAllUsers();
  }

  /** {@inheritDoc} */
  @Override
  public List<UserView> listViewsByIds(List<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return List.of();
    }
    return queries.listAllByIds(ids);
  }

  /** {@inheritDoc} */
  @Override
  public PageResult<UserView> search(PageQuery pageQuery, UserComplexSearchCriteria criteria) {
    return queries.search(pageQuery, criteria);
  }
}
