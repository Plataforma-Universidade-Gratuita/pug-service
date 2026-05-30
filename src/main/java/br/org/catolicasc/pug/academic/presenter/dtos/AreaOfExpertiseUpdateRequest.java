package br.org.catolicasc.pug.academic.presenter.dtos;

/**
 * Request DTO used as the JSON payload for updating an academic area of expertise.
 *
 * @param name the new area-of-expertise name, or {@code null} to leave unchanged
 */
public record AreaOfExpertiseUpdateRequest(String name) {}
