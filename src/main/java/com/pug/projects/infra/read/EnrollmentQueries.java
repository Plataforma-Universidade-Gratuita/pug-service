package com.pug.projects.infra.read;

import com.pug.projects.infra.read.dtos.EnrollmentView;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Read-only interface for executing queries against Enrollments.
 */
public interface EnrollmentQueries {
    Optional<EnrollmentView> findOptionalByIds(UUID projectId, UUID studentId);

    List<EnrollmentView> listAllEnrollments();

    List<EnrollmentView> listByProjectId(UUID projectId);

    List<EnrollmentView> listByStudentId(UUID studentId);
}