package br.org.catolicasc.pug.project.presenter.dtos;

import br.org.catolicasc.pug.shared.validation.UuidV7;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) used as the JSON request payload for creating a new Attendance record.
 *
 * @param projectId the unique identifier (UUIDv7) of the project (must not be null)
 * @param studentId the unique identifier (UUIDv7) of the formerStudent account (must not be null)
 * @param duration the duration of time the formerStudent spent on the project (must be min 0.01)
 */
public record AttendanceCreateRequest(
    @NotNull @UuidV7 UUID projectId,
    @NotNull @UuidV7 UUID studentId,
    @NotNull @DecimalMin(value = "0.01") BigDecimal duration) {}
