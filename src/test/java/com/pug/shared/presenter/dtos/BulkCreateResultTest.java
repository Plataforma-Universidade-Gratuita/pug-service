package com.pug.shared.presenter.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class BulkCreateResultTest {

  @Test
  void of_sets_requested_to_size_and_entities_unmodifiable() {
    List<Integer> src = new ArrayList<>(List.of(1, 2, 3));
    BulkCreateResult<Integer> r = BulkCreateResult.of(src);

    assertEquals(3, r.requested());
    assertEquals(List.of(1, 2, 3), r.entities());

    src.add(4);
    assertEquals(3, r.entities().size());

    assertThrows(UnsupportedOperationException.class, () -> r.entities().add(99));
  }

  @Test
  void sizeOnly_sets_requested_and_empty_entities() {
    BulkCreateResult<String> r = BulkCreateResult.sizeOnly(5);
    assertEquals(5, r.requested());
    assertTrue(r.entities().isEmpty());
    assertThrows(UnsupportedOperationException.class, () -> r.entities().add("x"));
  }

  @Test
  void constructor_copies_and_entities_method_returns_copy() {
    BulkCreateResult<String> r = new BulkCreateResult<>(2, List.of("a", "b"));
    List<String> a = r.entities();
    List<String> b = r.entities();
    assertEquals(a, b);
    assertThrows(UnsupportedOperationException.class, () -> a.add("x"));
    assertThrows(UnsupportedOperationException.class, () -> b.add("y"));
  }
}
