package com.pug.project.infra.read.dtos;

import com.pug.academic.infra.persistence.StudentEntity;
import com.pug.identity.infra.persistence.AccountEntity;
import com.pug.project.infra.persistence.AttendanceEntity;
import com.pug.project.infra.persistence.ProjectEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Internal Data Transfer Object (DTO) used exclusively for JPA tuple projections.
 *
 * <p>Fetches the base persistence entities required to build an {@link AttendanceView} in a single
 * query projection, mapping the attendance record to its project, student, and the staff member
 * (validator) who approved it.
 */
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public record AttendanceAcc(
    AttendanceEntity attendance,
    ProjectEntity project,
    StudentEntity student,
    AccountEntity validatorAccount) {}
