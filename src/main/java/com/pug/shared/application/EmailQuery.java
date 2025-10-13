package com.pug.shared.application;

import com.pug.shared.domain.validation.EmailBasic;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmailQuery(@NotBlank @EmailBasic @Size(max = 254) String email) {}
