package com.pug.shared.application;

import jakarta.validation.constraints.NotBlank;

public record StringQuery(@NotBlank String value) {}
