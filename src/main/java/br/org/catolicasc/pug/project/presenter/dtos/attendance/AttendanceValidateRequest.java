package br.org.catolicasc.pug.project.presenter.dtos.attendance;

import br.org.catolicasc.pug.project.domain.enums.AttendanceStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) used as the JSON request payload for validating an existing
 * Attendance.
 *
 * @param status the status to assign to the attendance (PRESENT or ABSENT; must not be null)
 * @param qrValidationHash the unique cryptographic hash of the QR code being scanned (must not be
 *     blank, max 512 chars)
 */
public record AttendanceValidateRequest(
    @NotNull AttendanceStatus status, @NotBlank @Size(max = 512) String qrValidationHash) {}
