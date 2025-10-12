package com.pug.shared.app.queries;

import jakarta.validation.constraints.NotBlank;

public record StringQuery(@NotBlank String value) {}
