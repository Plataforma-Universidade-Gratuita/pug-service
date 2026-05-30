package br.org.catolicasc.pug.project.presenter.dtos;

import java.util.UUID;

public record EnrollmentResponse(
    UUID projectId,
    UUID studentId,
    EnrollmentStatusResponse status,
    EnrollmentInfoResponse enrollmentInfo) {}
