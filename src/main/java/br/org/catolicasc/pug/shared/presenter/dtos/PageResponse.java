/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.shared.presenter.dtos;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;

/**
 * Public paginated response DTO matching the shared API pagination contract.
 *
 * @param content page items
 * @param page zero-based page index actually returned
 * @param size effective page size returned; may differ from the requested size when fetch-all is
 *     requested
 * @param totalElements total number of matching records
 * @param totalPages total number of pages for the current query
 */
@SuppressFBWarnings(
    value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"},
    justification =
        "The content list is defensively copied with List.copyOf and exposed as immutable.")
public record PageResponse<T>(
    List<T> content, int page, int size, long totalElements, int totalPages) {

  /**
   * Constructs a new {@code PageResponse} with the provided content and pagination metadata. The
   * content list is defensively copied to ensure immutability and prevent external modifications.
   *
   * @param content the list of items for the current page; if null, it will be treated as an empty
   *     list
   * @param page the zero-based index of the current page
   * @param size the effective number of items returned in this page; may differ from the requested
   *     size when fetch-all is requested
   * @param totalElements the total number of matching records across all pages
   * @param totalPages the total number of pages available for the current query
   */
  public PageResponse {
    content = content == null ? List.of() : List.copyOf(content);
  }
}
