package com.pug.projects.domain.vos;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AttendanceInfo(
    UUID validateBy, OffsetDateTime validatedAt, OffsetDateTime createdAt) {}
