package com.pug.shared.presenter.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.Objects;

/**
 * Bulk create request DTO.
 *
 * @param entities the list of entities to create
 * @param <T>      the type of the entities
 */
public record BulkCreateRequest<T>(@NotEmpty List<@Valid T> entities) {
  /**
   * Compact constructor to ensure immutability of the entities list.
   *
   * @param entities the list of entities to create.
   */
  public BulkCreateRequest {
    entities = List.copyOf(Objects.requireNonNull(entities));
  }

  /**
   * Gets a defensive copy of the list of entities to create.
   *
   * @return a new List containing the entities.
   */
  @Override
  public List<T> entities() {
    return List.copyOf(entities);
  }
}