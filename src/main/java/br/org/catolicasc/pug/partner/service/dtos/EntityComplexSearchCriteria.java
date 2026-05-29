package br.org.catolicasc.pug.partner.service.dtos;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service-layer criteria DTO used to execute partner-entity complex-search operations.
 *
 * @param name optional entity-name fragment used in a {@code like} filter
 * @param cnpj optional CNPJ fragment used in a {@code like} filter
 * @param address optional address fragment used in a {@code like} filter
 * @param cityIds optional city identifiers used in an {@code in} filter
 * @param dateFrom optional lower-bound timestamp applied inclusively to supported timestamps
 * @param dateTo optional upper-bound timestamp applied inclusively to supported timestamps
 */
public record EntityComplexSearchCriteria(
    String name,
    String cnpj,
    String address,
    List<UUID> cityIds,
    OffsetDateTime dateFrom,
    OffsetDateTime dateTo) {

  /**
   * Creates immutable partner-entity complex-search criteria for the application service layer.
   *
   * <p>The {@code cityIds} collection is defensively copied so downstream query code can safely
   * treat it as immutable.
   */
  public EntityComplexSearchCriteria {
    cityIds = cityIds == null ? List.of() : List.copyOf(cityIds);
  }
}
