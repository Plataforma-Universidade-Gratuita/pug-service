/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.identity.service;

import br.org.catolicasc.pug.identity.infra.read.dtos.AccountComplexSearchView;
import br.org.catolicasc.pug.identity.infra.read.dtos.AccountView;
import br.org.catolicasc.pug.identity.service.dtos.accounts.AccountComplexSearchCriteria;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import java.util.List;
import java.util.UUID;

/**
 * Application service interface dedicated exclusively to querying Account data.
 *
 * <p>Following CQRS principles, this service handles the "Query" operations. It bypasses complex
 * domain logic and retrieves lightweight read-model Data Transfer Objects directly from the
 * underlying data store.
 */
public interface AccountsReadService {

  /**
   * Retrieves a read-only projection of an account based on its unique identifier.
   *
   * @param id the unique identifier (UUID) of the requested account
   * @return the populated {@link AccountView} DTO
   * @throws ResourceNotFoundException if no account matches the provided ID
   */
  AccountView getViewById(UUID id);

  /**
   * Retrieves a comprehensive list of all accounts registered in the system.
   *
   * <p><i>Note:</i> This method returns the entire dataset. It should be used judiciously in
   * contexts where the dataset size is known to be safely bounded.
   *
   * @return a {@link List} containing all available {@link AccountView} entries
   */
  List<AccountView> listViews();

  /**
   * Retrieves account projections for a provided collection of unique identifiers.
   *
   * <p>This method is optimized for batch retrieval scenarios, allowing clients to fetch multiple
   * account views in a single call.
   *
   * @param ids a {@link List} of unique identifiers (UUIDs) corresponding to the desired accounts
   * @return a {@link List} of {@link AccountView} entries matching the provided IDs
   */
  List<AccountView> listViewsByIds(List<UUID> ids);

  /**
   * Executes paginated account search using the provided page request and complex-search criteria.
   *
   * @param pageQuery the requested page and page size
   * @param criteria the optional search criteria
   * @return a paginated result of matching {@link AccountComplexSearchView} entries
   */
  PageResult<AccountComplexSearchView> search(
      PageQuery pageQuery, AccountComplexSearchCriteria criteria);
}
