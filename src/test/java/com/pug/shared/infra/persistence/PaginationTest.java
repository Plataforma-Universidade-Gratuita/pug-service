package com.pug.shared.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.TypedQuery;
import java.util.List;
import org.junit.jupiter.api.Test;

class PaginationTest {

  @Test
  @SuppressWarnings("unchecked")
  void pageAppliesOffsetLimitAndBuildsPage() {
    TypedQuery<String> q = mock(TypedQuery.class);
    when(q.getResultList()).thenReturn(List.of("a", "b", "c"));

    var pr = new PageRequest(2, 10); // offset 20
    var pg = Pagination.page(q, () -> 25L, pr);

    verify(q).setFirstResult(20);
    verify(q).setMaxResults(10);
    verify(q).getResultList();

    assertEquals(List.of("a", "b", "c"), pg.items());
    assertEquals(25L, pg.total());
    assertEquals(2, pg.page());
    assertEquals(10, pg.size());
    assertEquals(3, pg.pages()); // ceil(25/10)
  }
}
