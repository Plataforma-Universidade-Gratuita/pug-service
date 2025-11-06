package com.pug.shared.presenter.dtos;

import com.pug.shared.validation.UuidV7;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * UUIDs request DTO.
 *
 * @param ids the list of UUIDs.
 */
public record UuidsRequest(@NotEmpty List<@NotNull @UuidV7 UUID> ids) {
  /**
   * Constructor.
   *
   * @param ids the list of UUIDs.
   */
  public UuidsRequest {
    ids = List.copyOf(Objects.requireNonNull(ids));
  }

  /**
   * Gets the list of UUIDs.
   *
   * @return the list of UUIDs.
   */
  @Override
  public List<UUID> ids() {
    return List.copyOf(ids);
  }
}
