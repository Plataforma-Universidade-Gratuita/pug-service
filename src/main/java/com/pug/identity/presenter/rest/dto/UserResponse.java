package com.pug.identity.presenter.rest.dto;

import com.pug.identity.domain.User;
import java.time.Instant;
import java.util.UUID;

public record UserResponse(UUID id, String cpf, String name, Instant createdAt, Instant updatedAt) {
  public static UserResponse from(User u) {
    return new UserResponse(u.getId(), u.getCpf(), u.getName(), u.getCreatedAt(), u.getUpdatedAt());
  }
}
