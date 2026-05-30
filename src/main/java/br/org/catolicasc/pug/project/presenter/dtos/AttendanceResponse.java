package br.org.catolicasc.pug.project.presenter.dtos;

import java.util.UUID;

/**
 * Data Transfer Object used as the canonical single-record attendance response.
 *
 * @param id the unique identifier of the attendance record
 * @param projectId the unique identifier of the associated project
 * @param studentId the unique identifier of the associated former student account
 * @param status the nested lifecycle status payload
 * @param attendanceInfo the nested validation and audit metadata payload
 * @param qrValidationInfo the nested QR validation payload
 */
public record AttendanceResponse(
    UUID id,
    UUID projectId,
    UUID studentId,
    AttendanceStatusResponse status,
    AttendanceInfoResponse attendanceInfo,
    QrValidationInfoResponse qrValidationInfo) {}
