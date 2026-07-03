/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.project.service.dtos.attendance;

import br.org.catolicasc.pug.project.domain.enums.AttendanceStatus;

/**
 * Data Transfer Object (DTO) acting as an application command to validate an existing Attendance.
 *
 * <p>This command encapsulates the account's decision regarding the attendance record.
 *
 * @param qrValidationHash the unique hash associated with the attendance record to be validated
 * @param status the status to assign to the attendance (PRESENT or ABSENT)
 */
public record AttendanceValidateCommand(String qrValidationHash, AttendanceStatus status) {}
