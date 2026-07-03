/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.partner.service.dtos.entities;

import java.util.UUID;

/**
 * Data Transfer Object (DTO) acting as an application command to update an existing Partner Entity.
 *
 * <p>This record encapsulates the requested state changes for a partner organization. The fields
 * are treated as optional for partial updates; omitting a value will retain the current state in
 * the database.
 *
 * @param name the new name of the organization, or {@code null} to leave unchanged
 * @param cityId the new city ID, or {@code null} to leave unchanged
 * @param address the new physical street address, or {@code null} to leave unchanged
 */
public record EntityUpdateCommand(String name, UUID cityId, String address) {}
