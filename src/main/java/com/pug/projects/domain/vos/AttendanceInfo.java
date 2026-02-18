package com.pug.projects.domain.vos;

import com.pug.projects.domain.enums.ProjectsErrorCodes;
import com.pug.shared.domain.DomainError;
import com.pug.shared.exceptions.AppValidationException;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Value object representing attendance information.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
public class AttendanceInfo extends DomainError {

    UUID validatedBy;
    OffsetDateTime validatedAt;
    OffsetDateTime createdAt;

    /**
     * Private constructor for AttendanceInfo.
     *
     * @param validatedBy UUID of the user who validated the attendance
     * @param validatedAt Date and time when the attendance was validated
     * @param createdAt   Date and time when the attendance record was created
     */
    @Builder(toBuilder = true)
    private AttendanceInfo(UUID validatedBy, OffsetDateTime validatedAt, OffsetDateTime createdAt) {
        this.validatedBy = validatedBy;
        this.validatedAt = validatedAt;
        this.createdAt = createdAt;
    }

    /**
     * Factory method to create and validate an AttendanceInfo instance.
     *
     * @param validatedBy UUID of the user who validated the attendance
     * @param validatedAt Date and time when the attendance was validated
     * @param createdAt   Date and time when the attendance record was created
     * @return A validated AttendanceInfo instance
     */
    public static AttendanceInfo factory(UUID validatedBy, OffsetDateTime validatedAt, OffsetDateTime createdAt) {
        AttendanceInfo vo = AttendanceInfo.builder()
                .validatedBy(validatedBy)
                .validatedAt(validatedAt)
                .createdAt(createdAt)
                .build();
        vo.collectValidationProblems();
        return vo;
    }

    /**
     * Validates the AttendanceInfo instance.
     * Adds validation errors to the domain error list if any validation rules are violated.
     */
    private void collectValidationProblems() {
        if (createdAt == null) {
            addError(new AppValidationException.Problem(ProjectsErrorCodes.INVALID_CREATED_AT_BLANK));
        }

        boolean hasValidator = validatedBy != null;
        boolean hasDate = validatedAt != null;

        if (hasValidator != hasDate) {
            addError(new AppValidationException.Problem(ProjectsErrorCodes.INVALID_ATTENDANCE_STATUS_BLANK));
        }
        if (hasDate && createdAt != null && validatedAt.isBefore(createdAt)) {
            addError(new AppValidationException.Problem(ProjectsErrorCodes.INVALID_CREATED_AT_FUTURE));
        }
    }
}