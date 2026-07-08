package br.org.catolicasc.pug.identity.presenter.dtos.accounts;

import java.util.UUID;

/**
 * Lightweight account projection used inside nested complex-search response contracts.
 *
 * <p>This response intentionally exposes only the minimal account identity required by parent
 * payloads that need to reference a person without embedding the full account representation.
 */
public record AccountSimpleComplexSearchResponse(UUID id, String name, String email) {}
