/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.identity.service;

import br.org.catolicasc.pug.identity.infra.read.dtos.UserView;
import br.org.catolicasc.pug.identity.service.dtos.users.UserComplexSearchCriteria;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import java.util.List;
import java.util.UUID;

/**
 * Application service interface dedicated exclusively to querying User data.
 *
 * <p>Following CQRS principles, this service handles the "Query" operations. It bypasses complex
 * domain logic and retrieves lightweight {@link UserView} Data Transfer Objects directly from the
 * underlying data store.
 */
public interface UsersReadService {

  /**
   * Retrieves a read-only projection of a user based on their unique identifier.
   *
   * @param id the unique identifier (UUID) of the requested user
   * @return the populated {@link UserView} DTO
   * @throws ResourceNotFoundException if no user matches the provided ID
   */
  UserView getViewById(UUID id);

  /**
   * Retrieves a comprehensive list of all users registered in the system.
   *
   * <p><i>Note:</i> This method returns the entire dataset. It should be used judiciously in
   * contexts where the dataset size is known to be safely bounded.
   *
   * @return a {@link List} containing all available {@link UserView} entries
   */
  List<UserView> listViews();

  /**
   * Retrieves user projections for a provided collection of unique identifiers.
   *
   * <p>This method is optimized for batch retrieval scenarios, allowing clients to fetch multiple
   * user views in a single call.
   *
   * @param ids a {@link List} of unique identifiers (UUIDs) corresponding to the desired users
   * @return a {@link List} of {@link UserView} entries matching the provided IDs
   */
  List<UserView> listViewsByIds(List<UUID> ids);

  /**
   * Executes paginated user search using the provided page request and complex-search criteria.
   *
   * @param pageQuery the requested page and page size
   * @param criteria the optional search criteria
   * @return a paginated result of matching {@link UserView} entries
   */
  PageResult<UserView> search(PageQuery pageQuery, UserComplexSearchCriteria criteria);
}
