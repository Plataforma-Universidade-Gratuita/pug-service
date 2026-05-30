package br.org.catolicasc.pug.identity.presenter.dtos.accounts;

import java.util.UUID;

/** Lightweight account projection used by complex-search responses in nested contracts. */
public record AccountSimpleComplexSearchResponse(UUID id, String name, String email) {}
