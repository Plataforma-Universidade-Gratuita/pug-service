package com.pug.shared.infra.persistence;

import java.util.List;

public record Page<T>(List<T> items, long total, int page, int size) {

  public Page(List<T> items, long total, int page, int size) {
    this.items = items == null ? List.of() : List.copyOf(items);
    this.total = total;
    this.page = page;
    this.size = size;
  }

  public int pages() {
    return (int) Math.ceil(total / (double) size);
  }
}
