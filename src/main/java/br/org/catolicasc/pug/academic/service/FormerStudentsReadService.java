/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.academic.service;

import br.org.catolicasc.pug.academic.infra.read.dtos.FormerStudentComplexSearchView;
import br.org.catolicasc.pug.academic.infra.read.dtos.FormerStudentView;
import br.org.catolicasc.pug.academic.service.dtos.formerstudents.FormerStudentComplexSearchCriteria;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import java.util.List;
import java.util.UUID;

/**
 * Application-layer read contract dedicated to former-student views and search results.
 *
 * <p>This service exposes the academic read use cases consumed by the presenter layer while hiding
 * the underlying query implementation details. It returns immutable read projections instead of
 * domain aggregates because these flows are display-oriented.
 */
public interface FormerStudentsReadService {

  /**
   * Retrieves the read-model projection for a former student using the linked account identifier.
   *
   * @param accountId the linked account identifier of the former student
   * @return the matching {@link FormerStudentView} projection
   * @throws ResourceNotFoundException if no former-student projection exists for the account
   */
  FormerStudentView getViewByAccountId(UUID accountId);

  /**
   * Retrieves every former-student read projection currently available to the academic module.
   *
   * @return a list containing all {@link FormerStudentView} projections
   */
  List<FormerStudentView> listViews();

  /**
   * Retrieves former-student projections restricted to the provided linked account identifiers.
   *
   * @param accountIds the linked account identifiers used to restrict the result set
   * @return a list containing the matching {@link FormerStudentView} projections
   */
  List<FormerStudentView> listViewsByIds(List<UUID> accountIds);

  /**
   * Executes paginated former-student complex search using the provided filtering criteria.
   *
   * @param pageQuery the requested pagination information
   * @param criteria the former-student filters to apply
   * @return a paginated result containing matching {@link FormerStudentComplexSearchView}
   *     projections
   */
  PageResult<FormerStudentComplexSearchView> search(
      PageQuery pageQuery, FormerStudentComplexSearchCriteria criteria);
}
