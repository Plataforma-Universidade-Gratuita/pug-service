package br.org.catolicasc.pug.project.presenter.dtos.attendance;

import java.math.BigDecimal;

/**
 * Data Transfer Object carrying the QR validation payload associated with an attendance record.
 *
 * @param duration the recorded attendance duration
 * @param qrValidationHash the cryptographic QR validation hash
 */
public record QrValidationInfoResponse(BigDecimal duration, String qrValidationHash) {}
