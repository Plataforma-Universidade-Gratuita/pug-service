package br.org.catolicasc.pug.shared.service.dtos;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;

/**
 * Internal paginated result container shared across read-side flows.
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
public record PageResult<T>(
    List<T> content, int page, int size, long totalElements, int totalPages) {

  public PageResult {
    content = content == null ? List.of() : List.copyOf(content);
  }
}
