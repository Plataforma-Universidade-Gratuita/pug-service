package br.org.catolicasc.pug.academic.presenter.dtos;

import java.util.UUID;

/**
 * Lightweight school response used by former-student complex-search payloads.
 *
 * @param id school identifier
 * @param name school name
 */
public record SchoolComplexSearchResponse(UUID id, String name) {}
