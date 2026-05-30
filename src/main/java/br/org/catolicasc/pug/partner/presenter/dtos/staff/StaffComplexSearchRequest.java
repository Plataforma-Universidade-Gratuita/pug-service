package br.org.catolicasc.pug.partner.presenter.dtos.staff;

import jakarta.validation.constraints.Pattern;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Request DTO used by the staff complex-search endpoint.
 *
 * <p>Each field is optional. When more than one filter is provided, the search applies all of them
 * using logical {@code AND}. Timestamp filters are evaluated against the account, linked-user, and
 * partner-entity audit timestamps resolved by the query.
 *
 * @param name optional user-name fragment used in a {@code like} filter
 * @param cpf optional user-CPF fragment used in a {@code like} filter
 * @param email optional account-email fragment used in a {@code like} filter
 * @param dateFrom optional lower-bound timestamp applied inclusively to supported timestamps
 * @param dateTo optional upper-bound timestamp applied inclusively to supported timestamps
 * @param activeOnly optional flag indicating whether only active accounts should be returned;
 *     defaults to {@code true} when omitted
 * @param entityIds optional partner-entity identifiers used in an {@code in} filter
 */
public record StaffComplexSearchRequest(
    @Pattern(regexp = ".*\\S.*") String name,
    @Pattern(regexp = ".*\\S.*") String cpf,
    @Pattern(regexp = ".*\\S.*") String email,
    OffsetDateTime dateFrom,
    OffsetDateTime dateTo,
    Boolean activeOnly,
    List<UUID> entityIds) {

  /**
   * Creates an immutable complex-search request payload for staff queries.
   *
   * <p>The {@code entityIds} collection is defensively copied to prevent accidental mutation after
   * request instantiation.
   */
  public StaffComplexSearchRequest {
    entityIds = entityIds == null ? List.of() : List.copyOf(entityIds);
  }
}
