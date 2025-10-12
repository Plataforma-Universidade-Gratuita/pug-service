package com.pug.shared.infra.persistence;

public record PageRequest(int page, int size) {
  public PageRequest {
    if (page < 0) throw new IllegalArgumentException("page >= 0");
    if (size < 1 || size > 1000) throw new IllegalArgumentException("1 <= size <= 1000");
  }

  public int offset() {
    return page * size;
  }
}
