package br.org.catolicasc.pug.identity.presenter.dtos.accounts;

import java.util.UUID;

public record AccountSimpleComplexSearchResponse(UUID id, String name, String email) {}
