package com.pug.academic.presenter.dtos;

import com.pug.shared.presenter.dtos.AuditInfoResponse;

import java.util.UUID;

/**
 * SchoolResponse DTO.
 *
 * @param id the school id
 * @param name the school name
 * @param auditInfo the audit info for the school
 */
public record SchoolResponse(UUID id, String name, AuditInfoResponse auditInfo) {}
