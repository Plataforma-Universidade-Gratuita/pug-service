/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.partner.infra.read;

import br.org.catolicasc.pug.partner.infra.read.dtos.StaffComplexSearchView;
import br.org.catolicasc.pug.partner.infra.read.dtos.StaffView;
import br.org.catolicasc.pug.partner.service.dtos.staff.StaffComplexSearchCriteria;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Read-only interface for executing staff profile queries.
 *
 * <p>This query port exposes projection-based lookup, listing, and complex-search operations for
 * partner staff members. Implementations are responsible for joining staff, account, user, partner
 * entity, and location data into read-model DTOs optimized for API responses.
 */
public interface StaffQueries {

  /**
   * Finds a staff projection by the linked account identifier.
   *
   * @param id the unique identifier of the account linked to the staff member
   * @return an {@link Optional} containing the matching {@link StaffView}, or {@link
   *     Optional#empty()} when the identifier is null or no staff member exists for it
   */
  Optional<StaffView> findOptionalById(UUID id);

  /**
   * Lists staff projections restricted to the provided linked account identifiers.
   *
   * <p>Results are ordered by the associated user's name in ascending order.
   *
   * @param ids the linked account identifiers used to restrict the returned staff members
   * @return a list containing the matching {@link StaffView} projections, or an empty list when no
   *     identifiers are provided
   */
  List<StaffView> listAllByIds(List<UUID> ids);

  /**
   * Lists all staff projections.
   *
   * <p>Results are ordered by the associated user's name in ascending order.
   *
   * @return a list containing all available {@link StaffView} projections
   */
  List<StaffView> listAllStaff();

  /**
   * Executes paginated staff complex search.
   *
   * <p>The search may filter staff by active account status, CPF, email, creation/update date
   * range, username, and linked partner entity identifiers. Results are ordered by the associated
   * user's name in ascending order.
   *
   * @param pageQuery the pagination request containing page, size, and fetch-all behavior
   * @param criteria the staff search filters to apply
   * @return a paginated {@link PageResult} containing {@link StaffComplexSearchView} projections
   */
  PageResult<StaffComplexSearchView> search(
      PageQuery pageQuery, StaffComplexSearchCriteria criteria);
}
