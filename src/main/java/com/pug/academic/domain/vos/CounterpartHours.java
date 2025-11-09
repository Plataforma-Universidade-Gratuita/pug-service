package com.pug.academic.domain.vos;

import java.math.BigDecimal;

public record CounterpartHours(BigDecimal requiredHours, BigDecimal completedHours) {}
