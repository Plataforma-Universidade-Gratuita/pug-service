/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.partner.service;

import br.org.catolicasc.pug.partner.infra.read.dtos.StaffComplexSearchView;
import br.org.catolicasc.pug.partner.infra.read.dtos.StaffView;
import br.org.catolicasc.pug.partner.service.dtos.staff.StaffComplexSearchCriteria;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import java.util.List;
import java.util.UUID;

/**
 * Application service interface dedicated to querying partner staff data.
 *
 * <p>This read-side service exposes staff lookup, listing, and complex-search operations. It
 * returns immutable read-model projections and delegates persistence-specific query behavior to the
 * infrastructure layer, keeping staff query flows separated from command operations.
 */
public interface StaffReadService {

  /**
   * Retrieves a staff read-model projection by its linked account identifier.
   *
   * @param accountId the unique identifier of the account linked to the staff member
   * @return the matching {@link StaffView}
   * @throws ResourceNotFoundException if no staff projection exists for the provided account
   */
  StaffView getViewByAccountId(UUID accountId);

  /**
   * Retrieves all staff read-model projections.
   *
   * @return a list containing all available {@link StaffView} projections
   */
  List<StaffView> listViews();

  /**
   * Retrieves staff read-model projections restricted to the provided linked account identifiers.
   *
   * @param ids the linked account identifiers used to restrict the returned staff members
   * @return a list containing the matching {@link StaffView} projections, or an empty list when no
   *     identifiers are provided
   */
  List<StaffView> listViewsByIds(List<UUID> ids);

  /**
   * Executes paginated staff search using the complex-search criteria.
   *
   * @param pageQuery the pagination request containing page, size, and fetch-all behavior
   * @param criteria the staff search filters to apply
   * @return a paginated {@link PageResult} containing {@link StaffComplexSearchView} projections
   */
  PageResult<StaffComplexSearchView> search(
      PageQuery pageQuery, StaffComplexSearchCriteria criteria);
}
