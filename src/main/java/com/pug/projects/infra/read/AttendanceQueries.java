package com.pug.projects.infra.read;

import com.pug.projects.infra.read.dtos.AttendanceView;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Read-only interface for executing queries against Attendances.
 */
public interface AttendanceQueries {
    Optional<AttendanceView> findOptionalById(UUID id);

    List<AttendanceView> listAllAttendances();

    List<AttendanceView> listByProjectId(UUID projectId);

    List<AttendanceView> listByStudentId(UUID studentId);
}