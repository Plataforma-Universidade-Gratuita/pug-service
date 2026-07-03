/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.shared.service.dtos;

/**
 * Internal pagination request shared by read services and query implementations.
 *
 * <p>This DTO carries the caller's requested page coordinates before they are normalized for query
 * execution. The repository-level convention in this codebase reserves {@code size = 1} as a
 * fetch-all sentinel, allowing clients to explicitly request the full matching result set in a
 * single response when that behavior is supported.
 *
 * @param page the requested zero-based page index
 * @param size the requested page size; {@code 1} is reserved as the fetch-all sentinel
 */
public record PageQuery(int page, int size) {

  public static final int FETCH_ALL_SIZE = 1;

  /**
   * Indicates whether this query requests the full matching result set in a single response.
   *
   * @return {@code true} when the query uses the shared fetch-all sentinel, {@code false} otherwise
   */
  public boolean isFetchAll() {
    return size == FETCH_ALL_SIZE;
  }
}
