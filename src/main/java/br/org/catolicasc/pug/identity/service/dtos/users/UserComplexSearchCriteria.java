/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.identity.service.dtos.users;

import java.time.OffsetDateTime;

/**
 * Internal search criteria used by the user read-side complex-search flow.
 *
 * @param cpf optional CPF fragment used in a {@code like} filter
 * @param dateFrom optional lower-bound timestamp applied inclusively to audit timestamps
 * @param dateTo optional upper-bound timestamp applied inclusively to audit timestamps
 * @param name optional name fragment used in a {@code like} filter
 */
public record UserComplexSearchCriteria(
    String cpf, OffsetDateTime dateFrom, OffsetDateTime dateTo, String name) {}
