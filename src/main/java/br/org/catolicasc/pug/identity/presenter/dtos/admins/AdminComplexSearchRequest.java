package br.org.catolicasc.pug.identity.presenter.dtos.admins;

import jakarta.validation.constraints.Pattern;
import java.time.OffsetDateTime;

/**
 * Request DTO used by the administrator complex-search endpoint.
 *
 * <p>Each field is optional. When more than one filter is provided, the search applies all of them
 * using logical {@code AND}. Timestamp filters are evaluated against the joined admin, account, and
 * linked-user audit timestamps that participate in the query.
 *
 * @param name optional user-name fragment used in a {@code like} filter
 * @param cpf optional user-CPF fragment used in a {@code like} filter
 * @param email optional account-email fragment used in a {@code like} filter
 * @param dateFrom optional lower-bound timestamp applied inclusively to supported timestamps
 * @param dateTo optional upper-bound timestamp applied inclusively to supported timestamps
 * @param activeOnly optional flag indicating whether only active accounts should be returned;
 *     defaults to {@code true} when omitted
 */
public record AdminComplexSearchRequest(
    @Pattern(regexp = ".*\\S.*") String name,
    @Pattern(regexp = ".*\\S.*") String cpf,
    @Pattern(regexp = ".*\\S.*") String email,
    OffsetDateTime dateFrom,
    OffsetDateTime dateTo,
    Boolean activeOnly) {}
