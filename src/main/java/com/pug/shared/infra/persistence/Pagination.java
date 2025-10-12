package com.pug.shared.infra.persistence;

import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.function.Supplier;

public final class Pagination {
  private Pagination() {}

  public static <T> Page<T> page(
      TypedQuery<T> dataQuery, Supplier<Long> totalSupplier, PageRequest pr) {
    dataQuery.setFirstResult(pr.offset());
    dataQuery.setMaxResults(pr.size());
    List<T> items = dataQuery.getResultList();
    long total = totalSupplier.get();
    return new Page<>(items, total, pr.page(), pr.size());
  }
}
