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

  /**
   * Constructs a new PageResult, ensuring that the content list is immutable and non-null. If the
   * provided content is null, it defaults to an empty list.
   *
   * @param content the list of items for the current page; if null, it will be set to an empty list
   * @param page the zero-based index of the current page
   * @param size the effective number of items returned in this page; may differ from requested size
   *     when fetching all records
   * @param totalElements the total number of matching records across all pages
   * @param totalPages the total number of pages available for the current query
   */
  public PageResult {
    content = content == null ? List.of() : List.copyOf(content);
  }
}
