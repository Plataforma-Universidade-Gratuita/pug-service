package br.org.catolicasc.pug.project.presenter.dtos;

import java.util.UUID;

/**
 * Data Transfer Object used as the content payload for paginated attendance complex-search
 * responses.
 *
 * @param id the unique identifier of the attendance record
 * @param project the lightweight associated project payload
 * @param student the lightweight associated former student payload
 * @param status the nested lifecycle status payload
 * @param attendanceInfo the nested validation and audit metadata payload
 * @param validator the lightweight validator account payload
 * @param qrValidationInfo the nested QR validation payload
 */
public record AttendanceComplexSearchResponse(
    UUID id,
    ProjectSimpleComplexSearchResponse project,
    StudentSimpleComplexSearchResponse student,
    AttendanceStatusResponse status,
    AttendanceInfoResponse attendanceInfo,
    AccountSimpleComplexSearchResponse validator,
    QrValidationInfoResponse qrValidationInfo) {}
