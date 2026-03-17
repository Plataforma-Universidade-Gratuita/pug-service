package com.pug.project.service.dtos;

import java.math.BigDecimal;
import java.util.UUID;

/** Data Transfer Object (DTO) acting as an application command to record a new Attendance entry. */
public record AttendanceCreateCommand(UUID projectId, UUID studentId, BigDecimal duration) {}
