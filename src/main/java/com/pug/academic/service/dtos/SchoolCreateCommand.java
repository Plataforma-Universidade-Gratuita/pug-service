package com.pug.academic.service.dtos;

/**
 * Data Transfer Object (DTO) acting as an application command to provision a new School.
 * <p>
 * This record encapsulates the raw input data required by the application service to
 * instantiate a new {@link com.pug.academic.domain.School} aggregate.
 *
 * @param name the raw name of the academic school or department
 */
public record SchoolCreateCommand(String name) {
}