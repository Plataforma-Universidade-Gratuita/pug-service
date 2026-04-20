package br.org.catolicasc.pug.partner.service;

import br.org.catolicasc.pug.partner.infra.read.dtos.StaffView;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.UUID;

/**
 * Application service interface dedicated exclusively to querying Staff data.
 *
 * <p>Following CQRS principles, this service handles the "Query" operations. It bypasses complex
 * domain logic and retrieves lightweight, fully resolved {@link StaffView} Data Transfer Objects
 * directly from the underlying data store or search indices.
 */
public interface StaffReadService {

  /**
   * Retrieves a read-only projection of a staff member based on their linked account ID.
   *
   * @param accountId the unique identifier (UUID) of the staff member's account
   * @return the populated {@link StaffView} DTO
   * @throws ResourceNotFoundException if no staff matches the provided ID
   */
  StaffView getViewByAccountId(UUID accountId);

  /**
   * Retrieves a read-only projection of a staff member based on their registered email address.
   *
   * @param email the exact email address string of the requested staff member
   * @return the populated {@link StaffView} DTO
   * @throws ResourceNotFoundException if no staff matches the provided
   *     email
   */
  StaffView getViewByEmail(String email);

  /**
   * Retrieves a comprehensive list of all staff members registered in the system.
   *
   * <p><i>Note:</i> This method returns the entire dataset. It should be used judiciously in
   * contexts where the dataset size is known to be safely bounded.
   *
   * @return a {@link List} containing all available {@link StaffView} entries
   */
  List<StaffView> listViews();

  /**
   * Retrieves a list of staff members filtered by the exact CPF of their associated user.
   *
   * @param cpf the raw 11-digit numeric CPF string
   * @return a {@link List} of matching {@link StaffView} entries
   */
  List<StaffView> listViewsByCpf(String cpf);

  /**
   * Retrieves a list of all staff members currently assigned to a specific partner organization.
   *
   * @param entityId the unique identifier (UUID) of the partner entity
   * @return a {@link List} of matching {@link StaffView} entries
   */
  List<StaffView> listViewsByEntityId(UUID entityId);

  /**
   * Executes a robust full-text search against the names of the associated staff users, returning
   * their corresponding staff profiles.
   *
   * @param term the raw search string or partial name provided by the client
   * @return a sorted {@link List} of matching {@link StaffView} entries
   */
  List<StaffView> search(String term);
}
