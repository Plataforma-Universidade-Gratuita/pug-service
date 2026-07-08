package br.org.catolicasc.pug.shared.service.dtos;

import jakarta.persistence.TypedQuery;

/**
 * Resolved pagination state derived from an incoming {@link PageQuery} and the total result size.
 *
 * <p>This DTO represents the execution-time pagination values after request normalization has been
 * applied. It captures the effective page metadata returned to the client, including the fetch-all
 * convention defined by {@link PageQuery#FETCH_ALL_SIZE}, and can also apply the resolved paging
 * window directly to a {@link TypedQuery}.
 *
 * @param page the zero-based page index actually returned
 * @param size the effective page size returned
 * @param offset the zero-based row offset used for paged queries
 * @param totalElements the total number of matching records
 * @param totalPages the total number of pages for the current query
 * @param fetchAll whether the request resolved to a fetch-all execution
 */
public record PageExecution(
    int page, int size, int offset, long totalElements, int totalPages, boolean fetchAll) {

  /**
   * Creates the resolved pagination state for a query execution.
   *
   * <p>This factory normalizes negative or undersized incoming values, applies the shared fetch-all
   * sentinel when necessary, and computes the metadata required by paginated API responses.
   *
   * @param pageQuery the incoming pagination request
   * @param totalElements the total number of matching records
   * @return the resolved pagination state for the current query execution
   */
  public static PageExecution from(PageQuery pageQuery, long totalElements) {
    int requestedPage = Math.max(pageQuery.page(), 0);
    int requestedSize = Math.max(pageQuery.size(), 1);
    boolean fetchAll = pageQuery.isFetchAll();

    int page = fetchAll ? 0 : requestedPage;
    int size = fetchAll ? Math.toIntExact(Math.max(totalElements, 1L)) : requestedSize;
    int offset = page * size;
    int totalPages =
        totalElements == 0 ? 0 : (fetchAll ? 1 : (int) Math.ceil((double) totalElements / size));

    return new PageExecution(page, size, offset, totalElements, totalPages, fetchAll);
  }

  /**
   * Applies the resolved offset/limit window to the provided query when this execution is paged.
   *
   * <p>When the execution represents a fetch-all request, the query is returned unchanged so the
   * full result set can be loaded.
   *
   * @param query the query that should receive pagination constraints
   * @param <T> the query result type
   * @return the same query instance, with paging applied when appropriate
   */
  public <T> TypedQuery<T> apply(TypedQuery<T> query) {
    if (fetchAll) {
      return query;
    }
    return query.setFirstResult(offset).setMaxResults(size);
  }
}
