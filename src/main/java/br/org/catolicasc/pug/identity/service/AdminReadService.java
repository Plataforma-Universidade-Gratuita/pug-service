package br.org.catolicasc.pug.identity.service;

import br.org.catolicasc.pug.identity.infra.read.dtos.AdminView;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import java.util.List;
import java.util.UUID;

/**
 * Application service interface dedicated exclusively to querying Administrator data.
 *
 * <p>Following CQRS principles, this service handles the "Query" operations. It bypasses complex
 * domain logic and retrieves lightweight, fully resolved {@link AdminView} Data Transfer Objects
 * directly from the underlying data store or search indices.
 */
public interface AdminReadService {

  /**
   * Retrieves a read-only projection of an administrator based on their linked account ID.
   *
   * @param accountId the unique identifier (UUID) of the admin's account
   * @return the populated {@link AdminView} DTO
   * @throws ResourceNotFoundException if no admin matches the provided account ID
   */
  AdminView getViewByAccountId(UUID accountId);

  /**
   * Retrieves a read-only projection of an administrator based on their registered email address.
   *
   * @param email the exact email address string of the requested admin
   * @return the populated {@link AdminView} DTO
   * @throws ResourceNotFoundException if no admin matches the provided email
   */
  AdminView getViewByEmail(String email);

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
   * Retrieves a list of administrators filtered by the exact CPF of their associated user.
   *
   * @param cpf the raw 11-digit numeric CPF string
   * @return a {@link List} of matching {@link AdminView} entries
   */
  List<AdminView> listViewsByCpf(String cpf);

  /**
   * Executes a robust full-text search against the names of the associated users, returning their
   * corresponding administrator profiles.
   *
   * @param query the raw search string or partial name provided by the client
   * @return a sorted {@link List} of matching {@link AdminView} entries
   */
  List<AdminView> search(String query);
}
