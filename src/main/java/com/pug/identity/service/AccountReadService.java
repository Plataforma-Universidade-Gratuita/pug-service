package com.pug.identity.service;

import com.pug.identity.infra.read.dtos.AccountView;

import java.util.List;
import java.util.UUID;

/**
 * Application service interface dedicated exclusively to querying Account data.
 * <p>
 * Following CQRS principles, this service handles the "Query" operations. It bypasses
 * complex domain logic and retrieves lightweight, fully resolved {@link AccountView} Data
 * Transfer Objects directly from the underlying data store or search indices.
 */
public interface AccountReadService {

  /**
   * Retrieves a read-only projection of an account based on its unique identifier.
   *
   * @param id the unique identifier (UUID) of the requested account
   * @return the populated {@link AccountView} DTO
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if no account matches the provided ID
   */
  AccountView getViewById(UUID id);

  /**
   * Retrieves a read-only projection of an account based on its registered email address.
   *
   * @param email the exact email address string of the requested account
   * @return the populated {@link AccountView} DTO
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if no account matches the provided email
   */
  AccountView getViewByEmail(String email);

  /**
   * Retrieves a comprehensive list of all accounts registered in the system.
   * <p>
   * <i>Note:</i> This method returns the entire dataset. It should be used judiciously
   * in contexts where the dataset size is known to be safely bounded.
   *
   * @return a {@link List} containing all available {@link AccountView} entries
   */
  List<AccountView> listViews();

  /**
   * Retrieves a list of accounts filtered by the exact CPF of their associated user.
   *
   * @param cpf the raw 11-digit numeric CPF string
   * @return a {@link List} of matching {@link AccountView} entries
   */
  List<AccountView> listViewsByCpf(String cpf);

  /**
   * Executes a robust full-text search against the names of the associated users,
   * returning their corresponding accounts.
   *
   * @param query the raw search string or partial name provided by the client
   * @return a sorted {@link List} of matching {@link AccountView} entries
   */
  List<AccountView> search(String query);
}