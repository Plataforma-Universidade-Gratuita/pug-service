package br.org.catolicasc.pug.academic.presenter.dtos;

import br.org.catolicasc.pug.shared.presenter.dtos.AuditInfoResponse;
import java.util.UUID;

/**
 * Paginated course-search response used by the course complex-search endpoint.
 *
 * @param id course identifier
 * @param name course name
 * @param areaOfExpertise lightweight area-of-expertise projection associated with the course
 * @param auditInfo audit metadata for the course row
 */
public record CourseWithAuditInfoComplexSearchResponse(
    UUID id,
    String name,
    AreaOfExpertiseComplexSearchResponse areaOfExpertise,
    AuditInfoResponse auditInfo) {}
