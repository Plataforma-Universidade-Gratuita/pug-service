package br.org.catolicasc.pug.academic.service.dtos.formerstudents;

import br.org.catolicasc.pug.academic.domain.FormerStudent;
import br.org.catolicasc.pug.identity.service.dtos.accounts.AccountCreateCommand;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) acting as an application command to enroll a new FormerStudent.
 *
 * <p>This record acts as an aggregated payload that combines identity credentials with academic
 * enrollment details. It encapsulates the data required to instantiate a new {@link FormerStudent}
 * aggregate and cascade the creation of the underlying authentication account.
 *
 * @param accountCreateCommand the nested command containing data to create the authentication
 *     account
 * @param academicRegistration the raw academic registration string assigned to the formerStudent
 * @param campus the designated university campus where the formerStudent is enrolled
 * @param courseId the unique identifier of the enrolled course
 * @param requiredHours the required counterpart hours the formerStudent must complete
 * @param startDate the start date defining the validity of the enrollment period
 * @param dueDate the due date (end date) of the enrollment period
 */
public record FormerStudentCreateCommand(
    AccountCreateCommand accountCreateCommand,
    String academicRegistration,
    Campi campus,
    UUID courseId,
    BigDecimal requiredHours,
    LocalDate startDate,
    LocalDate dueDate) {}
