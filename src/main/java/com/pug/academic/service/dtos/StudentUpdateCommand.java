package com.pug.academic.service.dtos;

import com.pug.academic.domain.enums.Campi;
import com.pug.academic.domain.vos.AcademicRegistration;
import com.pug.identity.service.dtos.AccountUpdateCommand;

/**
 * Command to update an existing student.
 *
 * @param accountCommand       the command containing the data to update the underlying account.
 * @param academicRegistration the new registration for the student.
 * @param campus               the campus associated with the student.
 */
public record StudentUpdateCommand(
        AccountUpdateCommand accountCommand,
        AcademicRegistration academicRegistration,
        Campi campus
) {
}
