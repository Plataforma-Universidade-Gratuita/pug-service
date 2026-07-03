/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.project.presenter.dtos.projects;

import br.org.catolicasc.pug.project.domain.enums.ProjectStatus;

/**
 * Nested response DTO that exposes the current project lifecycle state in both raw and formatted
 * forms.
 *
 * @param status the raw lifecycle status enum
 * @param statusFormatted the localized display label for the current status
 */
public record ProjectStatusResponse(ProjectStatus status, String statusFormatted) {}
