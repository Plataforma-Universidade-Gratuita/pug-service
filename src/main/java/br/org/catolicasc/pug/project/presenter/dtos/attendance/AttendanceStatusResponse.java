package br.org.catolicasc.pug.project.presenter.dtos.attendance;

import br.org.catolicasc.pug.project.domain.enums.AttendanceStatus;

/**
 * Data Transfer Object carrying both the raw attendance status code and its localized label.
 *
 * @param status the raw attendance status enum
 * @param statusFormatted the localized, human-readable representation of the status
 */
public record AttendanceStatusResponse(AttendanceStatus status, String statusFormatted) {}
