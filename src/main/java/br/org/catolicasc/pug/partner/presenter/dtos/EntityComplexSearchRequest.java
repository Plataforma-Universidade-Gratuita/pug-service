package br.org.catolicasc.pug.partner.presenter.dtos;

import jakarta.validation.constraints.Pattern;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Request DTO used by the partner-entity complex-search endpoint.
 *
 * <p>Each field is optional. When more than one filter is provided, the search applies them using
 * logical {@code AND}. Timestamp filters are evaluated against the entity audit timestamps.
 *
 * @param name optional entity-name fragment used in a {@code like} filter
 * @param cnpj optional CNPJ fragment used in a {@code like} filter
 * @param address optional address fragment used in a {@code like} filter
 * @param cityIds optional city identifiers used in an {@code in} filter
 * @param dateFrom optional lower-bound timestamp applied inclusively to supported timestamps
 * @param dateTo optional upper-bound timestamp applied inclusively to supported timestamps
 */
public record EntityComplexSearchRequest(
    @Pattern(regexp = ".*\\S.*") String name,
    @Pattern(regexp = ".*\\S.*") String cnpj,
    @Pattern(regexp = ".*\\S.*") String address,
    List<UUID> cityIds,
    OffsetDateTime dateFrom,
    OffsetDateTime dateTo) {

  /**
   * Creates an immutable complex-search request payload for partner-entity queries.
   *
   * <p>The {@code cityIds} collection is defensively copied to prevent accidental mutation after
   * request instantiation.
   */
  public EntityComplexSearchRequest {
    cityIds = cityIds == null ? List.of() : List.copyOf(cityIds);
  }
}
