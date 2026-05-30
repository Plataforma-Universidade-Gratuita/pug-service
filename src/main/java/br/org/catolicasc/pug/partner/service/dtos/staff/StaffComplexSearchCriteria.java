package br.org.catolicasc.pug.partner.service.dtos.staff;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service-layer criteria DTO used to execute staff complex-search operations.
 *
 * @param name optional user-name fragment used in a {@code like} filter
 * @param cpf optional user-CPF fragment used in a {@code like} filter
 * @param email optional account-email fragment used in a {@code like} filter
 * @param dateFrom optional lower-bound timestamp applied inclusively to supported timestamps
 * @param dateTo optional upper-bound timestamp applied inclusively to supported timestamps
 * @param activeOnly flag indicating whether only active accounts should be returned
 * @param entityIds optional partner-entity identifiers used in an {@code in} filter
 */
public record StaffComplexSearchCriteria(
    String name,
    String cpf,
    String email,
    OffsetDateTime dateFrom,
    OffsetDateTime dateTo,
    boolean activeOnly,
    List<UUID> entityIds) {

  /**
   * Creates immutable staff complex-search criteria for the application service layer.
   *
   * <p>The {@code entityIds} collection is defensively copied so downstream query code can safely
   * treat it as immutable.
   */
  public StaffComplexSearchCriteria {
    entityIds = entityIds == null ? List.of() : List.copyOf(entityIds);
  }
}
