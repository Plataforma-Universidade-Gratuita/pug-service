/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.identity.service.dtos.admins;

import java.time.OffsetDateTime;

/**
 * Service-layer criteria DTO used to execute administrator complex-search operations.
 *
 * @param name optional user-name fragment used in a {@code like} filter
 * @param cpf optional user-CPF fragment used in a {@code like} filter
 * @param email optional account-email fragment used in a {@code like} filter
 * @param dateFrom optional lower-bound timestamp applied inclusively to supported timestamps
 * @param dateTo optional upper-bound timestamp applied inclusively to supported timestamps
 * @param activeOnly flag indicating whether only active accounts should be returned
 */
public record AdminComplexSearchCriteria(
    String name,
    String cpf,
    String email,
    OffsetDateTime dateFrom,
    OffsetDateTime dateTo,
    boolean activeOnly) {}
