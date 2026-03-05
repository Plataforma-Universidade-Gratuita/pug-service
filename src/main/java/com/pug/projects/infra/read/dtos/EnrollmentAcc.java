package com.pug.projects.infra.read.dtos;

import com.pug.academic.infra.persistence.StudentEntity;
import com.pug.projects.infra.persistence.EnrollmentEntity;
import com.pug.projects.infra.persistence.ProjectEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Internal Data Transfer Object (DTO) used exclusively for JPA tuple projections.
 * <p>
 * Extracts the base persistence entities required to build an {@link EnrollmentView}.
 * Since Student and Project views require deep relational trees, this tuple fetches the
 * absolute core components to serve as a baseline for the Query Implementation to assemble.
 */
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public record EnrollmentAcc(
        EnrollmentEntity enrollment,
        ProjectEntity project,
        StudentEntity student) {
}