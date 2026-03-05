package com.pug.identity.infra.read;

import com.pug.identity.infra.read.dtos.AccountView;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Read-only interface for executing account queries.
 *
 * <p>This interface represents the "Query" side of a CQRS architecture. It defines operations for
 * retrieving authentication account data directly into lightweight {@link AccountView} projections,
 * bypassing the overhead of instantiating full JPA entities or domain aggregates.
 */
public interface AccountQueries {

  /**
   * Retrieves a read-only view of an account based on its registered email address.
   *
   * @param email the exact email address string to search for
   * @return an {@link Optional} containing the {@link AccountView} if found, or {@link
   *     Optional#empty()} otherwise
   */
  Optional<AccountView> findOptionalByEmail(String email);

  /**
   * Retrieves a read-only view of an account based on its unique identifier.
   *
   * @param id the unique identifier (UUID) of the account to retrieve
   * @return an {@link Optional} containing the {@link AccountView} if found, or {@link
   *     Optional#empty()} otherwise
   */
  Optional<AccountView> findOptionalById(UUID id);

  /**
   * Retrieves a comprehensive list of all accounts registered in the system.
   *
   * <p><i>Note:</i> Use with caution if the dataset grows significantly, as this method does not
   * implement pagination.
   *
   * @return a {@link List} of all {@link AccountView} entries available in the database
   */
  List<AccountView> listAllAccounts();

  /**
   * Retrieves a list of accounts linked to a specific user's CPF.
   *
   * <p>This query implicitly joins account data with the underlying user data to filter by the
   * natural key of the individual.
   *
   * @param cpf the exact 11-digit numeric CPF string of the linked user
   * @return a {@link List} of {@link AccountView} entries matching the given CPF
   */
  List<AccountView> listByCpf(String cpf);

  /**
   * Executes a robust full-text search against the names of the associated users.
   *
   * <p>This method typically leverages underlying indexing engines (e.g., Elasticsearch via
   * Hibernate Search) to resolve the individual's name and project the resulting account data.
   *
   * @param key the raw search string or partial name of the linked user
   * @return a sorted {@link List} of matching {@link AccountView} entries
   */
  List<AccountView> searchByName(String key);
}
