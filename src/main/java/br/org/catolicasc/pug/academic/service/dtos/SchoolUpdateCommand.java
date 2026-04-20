package br.org.catolicasc.pug.academic.service.dtos;

/**
 * Data Transfer Object (DTO) acting as an application command to update an existing School.
 *
 * <p>This record encapsulates the requested state changes for a school. The fields are treated as
 * optional for partial updates.
 *
 * @param name the new name of the school, or {@code null} to leave unchanged
 */
public record SchoolUpdateCommand(String name) {}
