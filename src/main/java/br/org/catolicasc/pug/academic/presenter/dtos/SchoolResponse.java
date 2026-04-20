package br.org.catolicasc.pug.academic.presenter.dtos;

import br.org.catolicasc.pug.shared.presenter.dtos.AuditInfoResponse;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) used as the standardized API JSON response for School data.
 *
 * <p>This record provides the client with the essential details of an academic school, optimized
 * for JSON serialization and direct rendering in the presentation layer.
 *
 * @param id the unique identifier (UUIDv7) of the academic school
 * @param name the name of the academic school
 * @param auditInfo the nested audit information containing creation and update timestamps
 */
public record SchoolResponse(UUID id, String name, AuditInfoResponse auditInfo) {}
