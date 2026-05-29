package br.org.catolicasc.pug.partner.presenter.dtos;

import java.util.UUID;

/**
 * Lightweight response DTO used by partner complex-search flows that only require basic entity
 * identification information.
 *
 * @param id the unique identifier (UUIDv7) of the partner entity
 * @param name the registered name of the partner entity
 */
public record EntityComplexSearchResponse(UUID id, String name) {}
