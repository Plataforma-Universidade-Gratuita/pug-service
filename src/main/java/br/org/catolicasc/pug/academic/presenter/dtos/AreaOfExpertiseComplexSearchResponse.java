package br.org.catolicasc.pug.academic.presenter.dtos;

import java.util.UUID;

/**
 * Lightweight area-of-expertise response shared by academic complex-search payloads.
 *
 * @param id area-of-expertise identifier
 * @param name area-of-expertise name
 */
public record AreaOfExpertiseComplexSearchResponse(UUID id, String name) {}
