package com.pug.academic.presenter.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record StudentView(
        UUID userId,
        String cpf,
        String name,
        String email,
        String academicRegistration,
        String campus,
        CourseResponse course,
        BigDecimal requiredHours,
        BigDecimal completedHours,
        LocalDate startDate,
        LocalDate dueDate) {}
