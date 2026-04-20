package br.org.catolicasc.pug.academic.service.dtos;

import br.org.catolicasc.pug.academic.domain.School;

/**
 * Data Transfer Object (DTO) acting as an application command to provision a new School.
 *
 * <p>This record encapsulates the raw input data required by the application service to instantiate
 * a new {@link School} aggregate.
 *
 * @param name the raw name of the academic school or department
 */
public record SchoolCreateCommand(String name) {}
