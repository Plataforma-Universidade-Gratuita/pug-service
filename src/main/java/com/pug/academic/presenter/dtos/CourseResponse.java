package com.pug.academic.presenter.dtos;

import com.pug.shared.presenter.dtos.AuditInfoResponse;

import java.util.UUID;

/**
 * CourseResponse DTO.
 *
 * @param id the course id
 * @param name the course name
 * @param school the school response
 * @param auditInfo the audit info for the course
 */
public record CourseResponse(UUID id, String name, SchoolResponse school, AuditInfoResponse auditInfo) {}
