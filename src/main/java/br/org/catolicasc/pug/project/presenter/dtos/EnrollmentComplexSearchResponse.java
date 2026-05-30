package br.org.catolicasc.pug.project.presenter.dtos;

public record EnrollmentComplexSearchResponse(
    ProjectSimpleComplexSearchResponse project,
    StudentSimpleComplexSearchResponse student,
    EnrollmentStatusResponse status,
    EnrollmentInfoResponse enrollmentInfo) {}
