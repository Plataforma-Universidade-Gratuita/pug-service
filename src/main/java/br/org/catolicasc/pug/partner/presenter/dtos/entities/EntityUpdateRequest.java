/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.partner.presenter.dtos.entities;

import jakarta.validation.constraints.Size;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) used as the JSON request payload for partially updating an existing
 * Partner Entity.
 *
 * <p>Because updates can be partial, all fields in this record are inherently optional. If a field
 * is provided as {@code null} or omitted from the JSON payload, the application service will ignore
 * it and retain the existing value for that specific attribute.
 *
 * @param name the new name of the organization, or {@code null} to leave unchanged (if provided,
 *     max 150 characters)
 * @param cityId the new city UUID, or {@code null} to leave unchanged
 * @param address the new physical street address, or {@code null} to leave unchanged (if provided,
 *     max 254 characters)
 */
public record EntityUpdateRequest(
    @Size(max = 150) String name, UUID cityId, @Size(max = 254) String address) {}
