package com.pug.identity.usecase.user.update;

import java.util.UUID;

public record UpdateUserCommand(UUID id, String cpf, String name) {}
