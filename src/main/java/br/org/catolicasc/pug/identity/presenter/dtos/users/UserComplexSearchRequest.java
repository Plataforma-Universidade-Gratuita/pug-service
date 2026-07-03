/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.identity.presenter.dtos.users;

import jakarta.validation.constraints.Pattern;
import java.time.OffsetDateTime;

/**
 * Request DTO used by the user complex-search endpoint.
 *
 * <p>Each field is optional. When more than one filter is provided, the search applies all of them
 * using logical {@code AND}. Timestamp filters are evaluated against every audit timestamp field
 * supported by the underlying user query.
 *
 * @param cpf optional CPF fragment used in a {@code like} filter
 * @param dateFrom optional lower-bound timestamp applied inclusively to user audit timestamps
 * @param dateTo optional upper-bound timestamp applied inclusively to user audit timestamps
 * @param name optional name fragment used in a {@code like} filter
 */
public record UserComplexSearchRequest(
    @Pattern(regexp = ".*\\S.*") String cpf,
    OffsetDateTime dateFrom,
    OffsetDateTime dateTo,
    @Pattern(regexp = ".*\\S.*") String name) {}
