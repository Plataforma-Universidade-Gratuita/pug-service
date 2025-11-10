package com.pug.academic.domain.vos;

import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.shared.exceptions.AppValidationException;

import java.math.BigDecimal;

/** Value Object representing Counterpart Hours with validation. */
public record CounterpartHours(BigDecimal requiredHours, BigDecimal completedHours) {
    /**
     * Constructs CounterpartHours after validating the input hours.
     * @param requiredHours the required hours
     * @param completedHours the completed hours
     * @throws AppValidationException if the hours are invalid
     */
    public CounterpartHours {
        if (requiredHours == null || completedHours == null) {
            throw new AppValidationException(AcademicErrorCodes.INVALID_HOURS);
        }
        if (requiredHours.signum() < 0 || completedHours.signum() < 0) {
            throw new AppValidationException(AcademicErrorCodes.INVALID_HOURS);
        }
        if (completedHours.compareTo(requiredHours) > 0) {
            throw new AppValidationException(AcademicErrorCodes.INVALID_HOURS_COMPLETED_GT_REQUIRED);
        }
    }

    /**
     * Calculates the missing hours.
     * @return the difference between required and completed hours
     */
    public BigDecimal getMissingHour(){
        return requiredHours.subtract(completedHours);
    }
}
