package br.org.catolicasc.pug.academic.infra.read.dtos;

import java.util.UUID;

/**
 * Lightweight school projection used by former-student complex-search queries.
 *
 * @param id school identifier
 * @param name school name
 */
public record SchoolComplexSearchView(UUID id, String name) {}
