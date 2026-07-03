/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.identity.service.dtos.users;

/**
 * Data Transfer Object (DTO) acting as an application command to update an existing User identity.
 *
 * <p>This record encapsulates the requested state changes for a user. The fields are treated as
 * optional for partial updates; omitting a value will retain the current state in the database.
 *
 * @param name the new name of the person, or {@code null} to leave unchanged
 */
public record UserUpdateCommand(String name) {}
