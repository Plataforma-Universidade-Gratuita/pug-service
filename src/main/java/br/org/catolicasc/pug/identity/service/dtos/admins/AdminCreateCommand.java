/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.identity.service.dtos.admins;

import br.org.catolicasc.pug.identity.domain.Admin;
import br.org.catolicasc.pug.identity.service.dtos.accounts.AccountCreateCommand;
import br.org.catolicasc.pug.shared.domain.enums.Campi;

/**
 * Data Transfer Object (DTO) acting as an application command to grant new Administrator
 * privileges.
 *
 * <p>This record encapsulates the raw input data required by the application service to instantiate
 * a new {@link Admin} aggregate, cascading down to create the necessary authentication account and
 * user identity in a single transaction.
 *
 * @param accountCommand the nested command containing the data to create the underlying
 *     authentication account
 * @param campus the designated university campus for the administrator's jurisdiction
 */
public record AdminCreateCommand(AccountCreateCommand accountCommand, Campi campus) {}
