package com.pug.projects.domain.vos;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ProjectInfo(
    UUID createdBy, OffsetDateTime createAt, OffsetDateTime closedAt, Integer maxParticipants) {}
