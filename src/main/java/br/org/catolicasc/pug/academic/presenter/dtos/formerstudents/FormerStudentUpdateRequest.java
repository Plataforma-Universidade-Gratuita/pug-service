package br.org.catolicasc.pug.academic.presenter.dtos.formerstudents;

import br.org.catolicasc.pug.shared.domain.enums.Campi;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) used as the JSON request payload for partially updating an existing
 * FormerStudent.
 *
 * <p>Because updates can be partial, all fields in this record are inherently optional. If a field
 * is provided as {@code null} or omitted from the JSON payload, the application service will ignore
 * it and retain the existing value for that specific attribute in the database.
 *
 * @param name the new full name, or {@code null} to leave unchanged
 * @param cpf the new CPF string, or {@code null} to leave unchanged
 * @param email the new email address string, or {@code null} to leave unchanged
 * @param academicRegistration the new academic registration string, or {@code null} to leave
 *     unchanged
 * @param campus the new university campus assignment, or {@code null} to leave unchanged
 * @param courseId the new enrolled course ID, or {@code null} to leave unchanged
 * @param requiredHours the new total required counterpart hours, or {@code null} to leave unchanged
 * @param startDate the new start date of the enrollment period, or {@code null} to leave unchanged
 * @param dueDate the new due date of the enrollment period, or {@code null} to leave unchanged
 */
public record FormerStudentUpdateRequest(
    String name,
    String cpf,
    String email,
    String academicRegistration,
    Campi campus,
    UUID courseId,
    BigDecimal requiredHours,
    LocalDate startDate,
    LocalDate dueDate) {}
