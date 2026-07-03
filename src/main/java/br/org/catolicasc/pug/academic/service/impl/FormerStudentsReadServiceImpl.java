/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.academic.service.impl;

import br.org.catolicasc.pug.academic.infra.read.FormerStudentsQueries;
import br.org.catolicasc.pug.academic.infra.read.dtos.FormerStudentComplexSearchView;
import br.org.catolicasc.pug.academic.infra.read.dtos.FormerStudentView;
import br.org.catolicasc.pug.academic.service.FormerStudentsReadService;
import br.org.catolicasc.pug.academic.service.dtos.formerstudents.FormerStudentComplexSearchCriteria;
import br.org.catolicasc.pug.academic.service.utils.ExceptionHelper;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import br.org.catolicasc.pug.shared.utils.CollectionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

/** Default implementation of the former-student read service. */
@ApplicationScoped
public class FormerStudentsReadServiceImpl implements FormerStudentsReadService {

  private static final Logger LOG = Logger.getLogger(FormerStudentsReadServiceImpl.class);

  @Inject FormerStudentsQueries queries;

  /** {@inheritDoc} */
  @Override
  public FormerStudentView getViewByAccountId(UUID accountId) {
    return queries
        .findOptionalById(accountId)
        .orElseThrow(
            () -> {
              LOG.debugf("Former student lookup failed: account ID %s not found", accountId);
              return ExceptionHelper.formerStudentNotFound();
            });
  }

  /** {@inheritDoc} */
  @Override
  public List<FormerStudentView> listViews() {
    return queries.listAllFormerStudents();
  }

  /** {@inheritDoc} */
  @Override
  public List<FormerStudentView> listViewsByIds(List<UUID> accountIds) {
    if (CollectionUtils.isEmpty(accountIds)) {
      return List.of();
    }
    return queries.listAllByIds(accountIds);
  }

  /** {@inheritDoc} */
  @Override
  public PageResult<FormerStudentComplexSearchView> search(
      PageQuery pageQuery, FormerStudentComplexSearchCriteria criteria) {
    return queries.search(pageQuery, criteria);
  }
}
