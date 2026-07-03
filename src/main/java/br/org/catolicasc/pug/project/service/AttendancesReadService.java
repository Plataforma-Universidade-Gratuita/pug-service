/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.project.service;

import br.org.catolicasc.pug.project.infra.read.dtos.AttendanceView;
import br.org.catolicasc.pug.project.service.dtos.attendance.AttendanceComplexSearchCriteria;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import java.util.List;
import java.util.UUID;

/** Application service interface dedicated exclusively to querying attendance projections. */
public interface AttendancesReadService {

  /**
   * Retrieves a read-only projection of an attendance record based on its unique identifier.
   *
   * @param id the unique identifier of the attendance
   * @return the populated {@link AttendanceView} DTO
   * @throws ResourceNotFoundException if no attendance matches the provided identifier
   */
  AttendanceView getViewById(UUID id);

  /**
   * Retrieves every attendance projection currently available in the system.
   *
   * @return the complete collection of attendance projections
   */
  List<AttendanceView> listViews();

  /**
   * Retrieves the attendance projections associated with the provided identifiers.
   *
   * @param ids the identifiers used to restrict the query
   * @return the subset of attendance projections matching the provided identifiers
   */
  List<AttendanceView> listViewsByIds(List<UUID> ids);

  /**
   * Executes a paginated attendance complex-search query.
   *
   * @param criteria the optional filtering criteria used to constrain the query
   * @param pageQuery the normalized paging request
   * @return a paginated collection of matching attendance projections
   */
  PageResult<AttendanceView> search(AttendanceComplexSearchCriteria criteria, PageQuery pageQuery);
}
