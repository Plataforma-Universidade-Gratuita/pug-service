package br.org.catolicasc.pug.identity.presenter.dtos;

import java.util.UUID;

/**
 * Lightweight response DTO used by identity complex-search flows that only require basic user
 * identity information.
 *
 * @param id the unique identifier (UUIDv7) of the user
 * @param name the full name of the user
 */
public record UserComplexSearchResponse(UUID id, String name) {}
