package br.org.catolicasc.pug.partner.infra.read.dtos;

import java.util.UUID;

/**
 * Read-only projection used by complex-search flows that only require basic partner-entity
 * information.
 *
 * @param id the unique identifier (UUIDv7) of the partner entity
 * @param name the registered name of the partner entity
 */
public record EntityComplexSearchView(UUID id, String name) {}
