package com.pug.shared.presenter.dtos;

import com.pug.shared.domain.enums.DeleteKeys;
import java.util.Map;
import java.util.Objects;

/**
 * Delete result DTO.
 *
 * @param deleted Map of entity names to number of deleted records.
 */
public record DeleteResult(Map<DeleteKeys, Long> deleted) {
  /**
   * Constructor.
   *
   * @param deleted Map of entity names to number of deleted records.
   */
  public DeleteResult {
    deleted = Map.copyOf(Objects.requireNonNull(deleted));
  }

  /**
   * Gets the map of deleted entities.
   *
   * @return the map of deleted entities.
   */
  @Override
  public Map<DeleteKeys, Long> deleted() {
    return Map.copyOf(deleted);
  }
}
