package br.org.catolicasc.pug.academic.infra.read.dtos;

import java.util.UUID;

/**
 * Lightweight course projection used by former-student complex-search queries.
 *
 * @param id course identifier
 * @param name course name
 * @param school lightweight school projection associated with the course
 */
public record CourseComplexSearchView(UUID id, String name, SchoolComplexSearchView school) {}
