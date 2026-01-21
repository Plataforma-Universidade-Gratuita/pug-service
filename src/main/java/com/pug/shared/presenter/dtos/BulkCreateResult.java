package com.pug.shared.presenter.dtos;

import java.util.List;
import java.util.Objects;

/**
 * Bulk create result DTO.
 *
 * @param requested number of requested entities.
 * @param entities list of created entities.
 * @param <T> type of entity.
 */
public record BulkCreateResult<T>(int requested, List<T> entities) {
  /**
   * Compact constructor for BulkCreateResult. Ensures immutability of the entities list.
   *
   * @param requested the number of requested entities.
   * @param entities the list of created entities.
   */
  public BulkCreateResult {
    entities = List.copyOf(Objects.requireNonNull(entities));
  }

  /**
   * Gets a defensive copy of the list of created entities.
   *
   * @return a new List containing the created entities.
   */
  @Override
  public List<T> entities() {
    return List.copyOf(entities);
  }

  /**
   * Creates a BulkCreateResult from a list of entities. The 'requested' count is derived from the
   * size of the provided list.
   *
   * @param entities the list of created entities.
   * @param <T> type of entity.
   * @return the BulkCreateResult.
   */
  public static <T> BulkCreateResult<T> of(List<T> entities) {
    return new BulkCreateResult<>(entities.size(), List.copyOf(entities));
  }

  /**
   * Creates a BulkCreateResult with only the size of requested entities. The list of entities will
   * be empty.
   *
   * @param size the number of requested entities.
   * @param <T> type of entity.
   * @return the BulkCreateResult.
   */
  public static <T> BulkCreateResult<T> sizeOnly(int size) {
    return new BulkCreateResult<>(size, List.of());
  }
}
