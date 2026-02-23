package com.pug.academic.infra.read.dtos;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.math.BigDecimal;

public record StudentViewWithCompletedHours(
        @JsonUnwrapped
        StudentView details,
        BigDecimal completedHours
) {
}