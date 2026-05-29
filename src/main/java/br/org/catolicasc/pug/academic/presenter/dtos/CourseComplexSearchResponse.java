package br.org.catolicasc.pug.academic.presenter.dtos;

import java.util.UUID;

/**
 * Lightweight course response used by former-student complex-search payloads.
 *
 * @param id course identifier
 * @param name course name
 * @param school lightweight school projection associated with the course
 */
public record CourseComplexSearchResponse(UUID id, String name, SchoolComplexSearchResponse school) {}
