package br.org.catolicasc.pug.academic.service.dtos;

import br.org.catolicasc.pug.academic.domain.Course;

import java.util.UUID;

/**
 * Data Transfer Object (DTO) acting as an application command to provision a new Course.
 *
 * <p>This record encapsulates the raw input data required by the application service to instantiate
 * a new {@link Course} aggregate.
 *
 * @param name the raw name of the academic course
 * @param schoolId the unique identifier of the school offering this course
 */
public record CourseCreateCommand(String name, UUID schoolId) {}
