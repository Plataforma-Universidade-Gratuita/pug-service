package com.pug.academic.infra.read.dtos;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.math.BigDecimal;

/** DTO representing a student view along with their completed hours. */
public record StudentViewWithCompletedHours(
    @JsonUnwrapped StudentView details, BigDecimal completedHours) {}
