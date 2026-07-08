package br.org.catolicasc.pug.academic.presenter.dtos.courses;

import br.org.catolicasc.pug.academic.presenter.dtos.areasofexpertise.AreaOfExpertiseComplexSearchResponse;
import java.util.UUID;

/**
 * Lightweight course response used by former-student complex-search payloads.
 *
 * @param id course identifier
 * @param name course name
 * @param areaOfExpertise lightweight area-of-expertise projection associated with the course
 */
public record CourseComplexSearchResponse(
    UUID id, String name, AreaOfExpertiseComplexSearchResponse areaOfExpertise) {}
