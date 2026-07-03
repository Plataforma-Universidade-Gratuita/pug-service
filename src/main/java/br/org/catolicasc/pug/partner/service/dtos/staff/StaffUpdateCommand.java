/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.partner.service.dtos.staff;

import br.org.catolicasc.pug.identity.service.dtos.accounts.AccountUpdateCommand;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) acting as an application command to update an existing Staff member.
 *
 * @param accountCommand the nested command containing the identity updates
 * @param entityId the new partner entity identifier, or {@code null} to preserve the current one
 */
public record StaffUpdateCommand(AccountUpdateCommand accountCommand, UUID entityId) {}
