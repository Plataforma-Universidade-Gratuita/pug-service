package br.org.catolicasc.pug.academic.presenter.dtos.courses;

import java.util.UUID;

import br.org.catolicasc.pug.academic.presenter.dtos.areasofexpertise.AreaOfExpertiseComplexSearchResponse;

/**
 * Lightweight course response used by former-student complex-search payloads.
 *
 * @param id course identifier
 * @param name course name
 * @param areaOfExpertise lightweight area-of-expertise projection associated with the course
 */
public record CourseComplexSearchResponse(
    UUID id, String name, AreaOfExpertiseComplexSearchResponse areaOfExpertise) {}
