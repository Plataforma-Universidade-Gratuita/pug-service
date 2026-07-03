/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.academic.infra.read;

import br.org.catolicasc.pug.academic.infra.read.dtos.FormerStudentComplexSearchView;
import br.org.catolicasc.pug.academic.infra.read.dtos.FormerStudentView;
import br.org.catolicasc.pug.academic.service.dtos.formerstudents.FormerStudentComplexSearchCriteria;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Read-only query contract for former-student projections.
 *
 * <p>This boundary exposes the infrastructure queries that power direct lookups, collection reads,
 * and paginated complex-search flows for former students. It returns read-model projections shaped
 * for presenter and service consumption without leaking persistence entities upward.
 */
public interface FormerStudentsQueries {

  /**
   * Resolves a single former-student projection by its linked account identifier.
   *
   * @param accountId the linked account identifier of the requested former student
   * @return an {@link Optional} containing the matching {@link FormerStudentView}, or an empty
   *     optional when no row matches the provided account identifier
   */
  Optional<FormerStudentView> findOptionalById(UUID accountId);

  /**
   * Retrieves all former-student projections whose linked account identifiers are present in the
   * provided collection.
   *
   * @param accountIds the linked account identifiers used to restrict the result set
   * @return a list containing the matching {@link FormerStudentView} projections
   */
  List<FormerStudentView> listAllByIds(List<UUID> accountIds);

  /**
   * Retrieves every former-student projection available to the academic read model.
   *
   * @return a list containing all {@link FormerStudentView} projections
   */
  List<FormerStudentView> listAllFormerStudents();

  /**
   * Executes paginated former-student complex search using the provided filtering criteria.
   *
   * @param pageQuery the pagination request containing page and size information
   * @param criteria the search filters to apply to the read model
   * @return a paginated result containing matching {@link FormerStudentComplexSearchView}
   *     projections
   */
  PageResult<FormerStudentComplexSearchView> search(
      PageQuery pageQuery, FormerStudentComplexSearchCriteria criteria);
}
