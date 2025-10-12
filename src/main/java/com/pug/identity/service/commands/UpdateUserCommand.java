package com.pug.identity.service.commands;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record UpdateUserCommand(@NotNull UUID id, String cpf, @Size(max = 150) String name) {}
