package com.pug.projects.domain.vos;

import java.time.OffsetDateTime;

public record EnrollmentInfo(
    OffsetDateTime requestAt, OffsetDateTime acceptedAt, OffsetDateTime closingStatusAt) {}
