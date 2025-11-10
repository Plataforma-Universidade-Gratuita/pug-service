package com.pug.academic.domain.vos;

import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.shared.exceptions.AppValidationException;

import java.time.LocalDate;

/** Value Object representing a Period with start and due dates. */
public record Period(LocalDate startDate, LocalDate dueDate) {
    /**
     * Constructs a Period after validating the start and due dates.
     * @param startDate the start date
     * @param dueDate the due date
     * @throws AppValidationException if the dates are invalid
     */
    public Period {
        if (startDate == null || dueDate == null) {
            throw new AppValidationException(AcademicErrorCodes.INVALID_PERIOD);
        }
        if (dueDate.isBefore(startDate)) {
            throw new AppValidationException(AcademicErrorCodes.INVALID_PERIOD_RANGE);
        }
    }

    /**
     * Calculates the remaining days until the due date.
     * @return the number of remaining days
     */
    public LocalDate remainingDays() {
        return dueDate.minusDays(LocalDate.now().toEpochDay());
    }
}
