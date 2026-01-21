package com.pug.academic.service.dtos;

/**
 * Command DTO for creating a new School.
 *
 * @param name the name of the school.
 */
public record SchoolCreateCommand(String name) {}
