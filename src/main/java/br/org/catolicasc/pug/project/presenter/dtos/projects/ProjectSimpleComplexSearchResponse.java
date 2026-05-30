package br.org.catolicasc.pug.project.presenter.dtos.projects;

import java.util.UUID;

/** Lightweight project projection used inside nested complex-search responses. */
public record ProjectSimpleComplexSearchResponse(UUID id, String name) {}
