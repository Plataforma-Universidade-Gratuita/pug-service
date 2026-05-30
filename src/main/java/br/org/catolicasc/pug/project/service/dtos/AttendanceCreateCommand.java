package br.org.catolicasc.pug.project.service.dtos;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) acting as an application command to record a new Attendance entry.
 *
 * <p>This command captures the essential data needed to register an attendance instance.
 *
 * @param projectId the unique identifier of the project
 * @param studentId the unique identifier of the formerStudent account
 * @param duration the total hours the formerStudent claims to have worked
 */
public record AttendanceCreateCommand(UUID projectId, UUID studentId, BigDecimal duration) {}
