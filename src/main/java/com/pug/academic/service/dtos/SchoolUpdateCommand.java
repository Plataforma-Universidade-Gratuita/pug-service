package com.pug.academic.service.dtos;

/**
 * Command DTO for updating an existing School.
 *
 * @param name the new name of the school (optional, if null, the name is not updated).
 */
public record SchoolUpdateCommand(String name) {}
