/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.partner.infra.read.dtos;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing a read-only, flattened view of a Partner Entity.
 *
 * <p>Following CQRS principles, this record is used exclusively for returning queried data to the
 * client. It consolidates the partner organization's details and its geographic reference into a
 * single structure, exposing only the city's identifier so that additional location details can be
 * fetched on demand via dedicated geo endpoints.
 *
 * @param id the unique identifier (UUIDv7) of the partner entity
 * @param cnpj the exact 14-digit numeric CNPJ string
 * @param name the registered name or corporate reason of the entity
 * @param address the physical street address of the entity
 * @param cityId the unique identifier (UUIDv7) of the city where the entity is located
 * @param createdAt the exact timestamp when the entity record was created
 * @param updatedAt the exact timestamp when the entity record was last modified
 */
public record EntityView(
    UUID id,
    String cnpj,
    String name,
    String address,
    UUID cityId,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt) {}
