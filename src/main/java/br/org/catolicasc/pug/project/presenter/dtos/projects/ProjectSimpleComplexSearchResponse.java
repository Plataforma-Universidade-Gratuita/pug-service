package br.org.catolicasc.pug.project.presenter.dtos.projects;

import java.util.UUID;

/**
 * Lightweight project projection used inside nested complex-search responses.
 *
 * <p>This response is intentionally minimal and is meant for parent payloads that only need to
 * identify and label a project.
 */
public record ProjectSimpleComplexSearchResponse(UUID id, String name) {}
