package br.org.catolicasc.pug.academic.service.dtos.areasofexpertise;

/**
 * Service-layer command that carries the mutable data of an existing academic area of expertise.
 *
 * @param name the replacement display name to be validated and persisted
 */
public record AreaOfExpertiseUpdateCommand(String name) {}
