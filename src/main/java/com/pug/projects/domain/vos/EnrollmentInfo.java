package com.pug.projects.domain.vos;

import com.pug.projects.domain.enums.ProjectsErrorCodes;
import com.pug.shared.domain.DomainError;
import com.pug.shared.exceptions.AppValidationException;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

import java.time.OffsetDateTime;

/**
 * Value object representing enrollment information.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
public class EnrollmentInfo extends DomainError {

    OffsetDateTime requestAt;
    OffsetDateTime acceptedAt;
    OffsetDateTime closingStatusAt;

    /**
     * Private constructor to enforce the use of the factory method.
     */
    @Builder(toBuilder = true)
    private EnrollmentInfo(
            OffsetDateTime requestAt,
            OffsetDateTime acceptedAt,
            OffsetDateTime closingStatusAt) {
        this.requestAt = requestAt;
        this.acceptedAt = acceptedAt;
        this.closingStatusAt = closingStatusAt;
    }

    /**
     * Factory method to create and validate an EnrollmentInfo instance.
     *
     * @param requestAt       The date and time when the enrollment request was made.
     * @param acceptedAt      The date and time when the enrollment was accepted.
     * @param closingStatusAt The date and time when the enrollment status was closed.
     * @return A validated EnrollmentInfo instance.
     */
    public static EnrollmentInfo factory(
            OffsetDateTime requestAt,
            OffsetDateTime acceptedAt,
            OffsetDateTime closingStatusAt) {

        EnrollmentInfo vo = EnrollmentInfo.builder()
                .requestAt(requestAt)
                .acceptedAt(acceptedAt)
                .closingStatusAt(closingStatusAt)
                .build();
        vo.collectValidationProblems();
        return vo;
    }

    /**
     * Validates the EnrollmentInfo instance.
     */
    private void collectValidationProblems() {
        if (requestAt == null) {
            addError(new AppValidationException.Problem(ProjectsErrorCodes.INVALID_ENROLLMENT_REQUEST_AT_BLANK));
            return;
        }

        if (acceptedAt != null && acceptedAt.isBefore(requestAt)) {
            addError(new AppValidationException.Problem(ProjectsErrorCodes.INVALID_ENROLLMENT_DATES_INVALID));
        }

        if (closingStatusAt != null && closingStatusAt.isBefore(requestAt)) {
            addError(new AppValidationException.Problem(ProjectsErrorCodes.INVALID_ENROLLMENT_DATES_INVALID));
        }
    }
}