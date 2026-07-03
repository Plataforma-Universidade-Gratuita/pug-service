/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.project.service;

import br.org.catolicasc.pug.project.infra.read.dtos.EnrollmentView;
import br.org.catolicasc.pug.project.service.dtos.enrollments.EnrollmentComplexSearchCriteria;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import java.util.List;
import java.util.UUID;

/**
 * Application-layer read contract for enrollment projections and search results.
 *
 * <p>This service exposes the read use cases consumed by enrollment presenters, including
 * composite-key lookups, collection reads scoped by project or former student, and paginated
 * complex search.
 */
public interface EnrollmentsReadService {

  /**
   * Retrieves a read-only enrollment projection by its composite identifiers.
   *
   * @param projectId the unique identifier of the project linked to the enrollment
   * @param formerStudentId the unique identifier of the former student linked to the enrollment
   * @return the matching {@link EnrollmentView} projection
   * @throws ResourceNotFoundException if no enrollment matches the provided identifiers
   */
  EnrollmentView getViewByIds(UUID projectId, UUID formerStudentId);

  /**
   * Retrieves every enrollment projection currently available in the read model.
   *
   * @return the complete collection of enrollment projections
   */
  List<EnrollmentView> listViews();

  /**
   * Retrieves every enrollment projection associated with the provided project.
   *
   * @param projectId the unique identifier of the project
   * @return the matching enrollment projections, or an empty list when no enrollment matches
   */
  List<EnrollmentView> listViewsByProjectId(UUID projectId);

  /**
   * Retrieves every enrollment projection associated with the provided former student.
   *
   * @param formerStudentId the unique identifier of the former student account
   * @return the matching enrollment projections, or an empty list when no enrollment matches
   */
  List<EnrollmentView> listViewsByFormerStudentId(UUID formerStudentId);

  /**
   * Executes the paginated enrollment complex-search flow.
   *
   * @param criteria the optional filtering criteria used to constrain the query
   * @param pageQuery the normalized paging request
   * @return a paginated collection of matching enrollment projections
   */
  PageResult<EnrollmentView> search(EnrollmentComplexSearchCriteria criteria, PageQuery pageQuery);
}
