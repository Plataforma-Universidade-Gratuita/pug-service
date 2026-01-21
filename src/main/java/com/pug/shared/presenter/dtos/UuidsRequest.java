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
   * Compact constructor for UuidsRequest. Ensures immutability of the IDs list.
   *
   * @param ids the list of UUIDs.
   */
  public UuidsRequest {
    ids = List.copyOf(Objects.requireNonNull(ids));
  }

  /**
   * Gets a defensive copy of the list of UUIDs.
   *
   * @return a new List containing the UUIDs.
   */
  @Override
  public List<UUID> ids() {
    return List.copyOf(ids);
  }
}
