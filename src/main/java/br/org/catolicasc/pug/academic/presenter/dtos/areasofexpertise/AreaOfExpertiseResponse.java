package br.org.catolicasc.pug.academic.presenter.dtos.areasofexpertise;

import br.org.catolicasc.pug.shared.presenter.dtos.AuditInfoResponse;
import java.util.UUID;

/**
 * Standardized API response DTO for academic area-of-expertise data.
 *
 * @param id the unique identifier of the academic area of expertise
 * @param name the name of the academic area of expertise
 * @param auditInfo the nested audit information containing creation and update timestamps
 */
public record AreaOfExpertiseResponse(UUID id, String name, AuditInfoResponse auditInfo) {}
