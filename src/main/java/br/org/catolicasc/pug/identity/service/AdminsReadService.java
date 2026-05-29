package br.org.catolicasc.pug.identity.service;

import br.org.catolicasc.pug.identity.infra.read.dtos.AdminComplexSearchView;
import br.org.catolicasc.pug.identity.infra.read.dtos.AdminView;
import br.org.catolicasc.pug.identity.service.dtos.AdminComplexSearchCriteria;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import java.util.List;
import java.util.UUID;

/**
 * Application service interface dedicated exclusively to querying Administrator data.
 *
 * <p>Following CQRS principles, this service handles the "Query" operations. It bypasses complex
 * domain logic and retrieves lightweight, fully resolved read-model DTOs directly from the
 * underlying data store.
 */
public interface AdminsReadService {

  /**
   * Retrieves a read-only projection of an administrator based on their linked account ID.
   *
   * @param accountId the unique identifier (UUID) of the admin's account
   * @return the populated {@link AdminView} DTO
   * @throws ResourceNotFoundException if no admin matches the provided account ID
   */
  AdminView getViewByAccountId(UUID accountId);

  /**
   * Retrieves a read-only projection of an administrator based on their linked account ID.
   *
   * <p>This method exists so collection-oriented contracts can keep the plural naming pattern while
   * still exposing an explicit by-id read operation to callers that need it.
   *
   * @param accountId the unique identifier (UUID) of the admin's account
   * @return the populated {@link AdminView} DTO
   * @throws ResourceNotFoundException if no admin matches the provided account ID
   */
  AdminView getViewById(UUID accountId);

  /**
   * Retrieves a comprehensive list of all administrators registered in the system.
   *
   * <p><i>Note:</i> This method returns the entire dataset. It should be used judiciously in
   * contexts where the dataset size is known to be safely bounded.
   *
   * @return a {@link List} containing all available {@link AdminView} entries
   */
  List<AdminView> listViews();

  /**
   * Retrieves the administrators whose linked account identifiers are present in the provided
   * collection.
   *
   * @param ids the account identifiers used to restrict the returned collection
   * @return a {@link List} containing the matching administrator projections
   */
  List<AdminView> listViewsByIds(List<UUID> ids);

  /**
   * Executes paginated administrator search using the shared complex-search contract.
   *
   * @param pageQuery the requested pagination information
   * @param criteria the optional administrator search criteria
   * @return a paginated result containing the administrator search projections
   */
  PageResult<AdminComplexSearchView> search(
      PageQuery pageQuery, AdminComplexSearchCriteria criteria);
}
