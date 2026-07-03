/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.identity.presenter.dtos.users;

import br.org.catolicasc.pug.shared.presenter.dtos.AuditInfoResponse;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) used as the standardized API JSON response for User data.
 *
 * <p>This record encapsulates the essential personal information about a user, including their
 * unique identifier, CPF, and name. It includes formatted versions of the CPF and timestamps
 * optimized for direct rendering in UI components.
 *
 * @param id the unique identifier (UUIDv7) of the user
 * @param cpf the raw, 11-digit numeric CPF string
 * @param cpfFormatted the CPF string formatted with standard punctuation (e.g., "000.000.000-00")
 * @param name the full name of the user
 * @param auditInfo the nested audit information containing creation and update timestamps
 */
public record UserResponse(
    UUID id, String cpf, String cpfFormatted, String name, AuditInfoResponse auditInfo) {}
