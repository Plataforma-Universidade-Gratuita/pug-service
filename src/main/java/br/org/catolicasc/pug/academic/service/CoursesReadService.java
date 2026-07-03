/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.academic.service;

import br.org.catolicasc.pug.academic.infra.read.dtos.CourseView;
import br.org.catolicasc.pug.academic.service.dtos.courses.CourseComplexSearchCriteria;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import java.util.List;
import java.util.UUID;

/**
 * Application service interface dedicated exclusively to querying Course data.
 *
 * <p>Following CQRS principles, this service handles the query-side operations for academic
 * courses. It retrieves lightweight, fully resolved {@link CourseView} projections directly from
 * the underlying read model.
 */
public interface CoursesReadService {

  /**
   * Retrieves a read-only projection of a course based on its unique identifier.
   *
   * @param id the unique identifier (UUID) of the course
   * @return the populated {@link CourseView} DTO
   * @throws ResourceNotFoundException if no course matches the provided ID
   */
  CourseView getViewById(UUID id);

  /**
   * Retrieves a comprehensive list of all courses registered in the system.
   *
   * <p><i>Note:</i> This method returns the entire dataset. It should be used judiciously in
   * contexts where the dataset size is known to be safely bounded.
   *
   * @return a {@link List} containing all available {@link CourseView} entries
   */
  List<CourseView> listViews();

  /**
   * Retrieves a collection of courses restricted to the provided identifiers.
   *
   * @param ids the course identifiers to resolve
   * @return a sorted {@link List} containing the matching {@link CourseView} entries
   */
  List<CourseView> listViewsByIds(List<UUID> ids);

  /**
   * Executes paginated course search using the academic complex-search contract.
   *
   * @param pageQuery the requested pagination information
   * @param criteria the optional search criteria
   * @return a paginated result containing matching {@link CourseView} projections
   */
  PageResult<CourseView> search(PageQuery pageQuery, CourseComplexSearchCriteria criteria);
}
