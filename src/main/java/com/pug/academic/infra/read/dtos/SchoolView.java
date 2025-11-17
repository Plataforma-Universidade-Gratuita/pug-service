package com.pug.academic.infra.read.dtos;

import java.util.UUID;

/**
 * School view DTO.
 *
 * @param id   the unique identifier of the school
 * @param name the name of the school
 */
public record SchoolView(UUID id, String name) {
}
