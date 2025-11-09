package com.pug.projects.domain.vos;

import java.math.BigDecimal;

public record QrValidationInfo(
    BigDecimal duration, BigDecimal latitude, BigDecimal longitude, String qrValidationHash) {}
