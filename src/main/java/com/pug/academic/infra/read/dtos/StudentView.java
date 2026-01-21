package com.pug.academic.infra.read.dtos;

import com.pug.identity.infra.read.dtos.AccountView;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * StudentView DTO.
 *
 * @param account the account information
 * @param course the course information
 * @param requiredHours the required hours for the course
 * @param completedHours the completed hours by the student
 * @param startDate the start date of the program
 * @param dueDate the due date for program completion
 */
public record StudentView(
    AccountView account,
    String academicRegistration,
    String campus,
    CourseView course,
    BigDecimal requiredHours,
    BigDecimal completedHours,
    LocalDate startDate,
    LocalDate dueDate) {}
