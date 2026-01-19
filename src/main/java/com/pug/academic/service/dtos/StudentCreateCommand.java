package com.pug.academic.service.dtos;

import com.pug.academic.domain.enums.Campi;
import com.pug.academic.domain.vos.AcademicRegistration;
import com.pug.academic.domain.vos.CounterpartHours;
import com.pug.academic.domain.vos.Period;
import com.pug.identity.service.dtos.AccountCreateCommand;

/**
 * Command to create a new student.
 *
 * @param accountCreateCommand the command containing account creation details
 * @param reg the academic registration of the student
 * @param campus the campus where the student is enrolled
 * @param hours the counterpart hours for the student
 * @param period the period of study for the student
 */
public record StudentCreateCommand(
    AccountCreateCommand accountCreateCommand,
    AcademicRegistration reg,
    Campi campus,
    CounterpartHours hours,
    Period period) {}
