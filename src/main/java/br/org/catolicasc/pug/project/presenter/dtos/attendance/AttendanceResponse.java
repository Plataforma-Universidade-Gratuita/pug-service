/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.project.presenter.dtos.attendance;

import java.util.UUID;

/**
 * Data Transfer Object used as the canonical single-record attendance response.
 *
 * @param id the unique identifier of the attendance record
 * @param projectId the unique identifier of the associated project
 * @param formerStudentId the unique identifier of the associated formerStudent account
 * @param status the nested lifecycle status payload
 * @param attendanceInfo the nested validation and audit metadata payload
 * @param qrValidationInfo the nested QR validation payload
 */
public record AttendanceResponse(
    UUID id,
    UUID projectId,
    UUID formerStudentId,
    AttendanceStatusResponse status,
    AttendanceInfoResponse attendanceInfo,
    QrValidationInfoResponse qrValidationInfo) {}
