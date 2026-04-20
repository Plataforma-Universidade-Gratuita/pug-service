package br.org.catolicasc.pug.academic.service.dtos;

import br.org.catolicasc.pug.identity.service.dtos.AccountUpdateCommand;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) acting as an application command to update an existing Student's
 * enrollment.
 *
 * <p>This record encapsulates the requested state changes for a student. The fields, including the
 * nested account update command, are treated as optional for partial updates.
 *
 * @param accountUpdateCommand the nested command containing data to update the authentication
 *     account, or {@code null}
 * @param academicRegistration the new academic registration string, or {@code null} to leave
 *     unchanged
 * @param campus the new campus assignment, or {@code null} to leave unchanged
 * @param courseId the new enrolled course identifier, or {@code null} to leave unchanged
 * @param requiredHours the new total required counterpart hours, or {@code null} to leave unchanged
 * @param startDate the new start date of the enrollment period, or {@code null} to leave unchanged
 * @param dueDate the new due date of the enrollment period, or {@code null} to leave unchanged
 */
public record StudentUpdateCommand(
    AccountUpdateCommand accountUpdateCommand,
    String academicRegistration,
    Campi campus,
    UUID courseId,
    BigDecimal requiredHours,
    LocalDate startDate,
    LocalDate dueDate) {}
