package com.pug.academic.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.academic.infra.persistence.SchoolEntity;
import com.pug.shared.application.StringQuery;
import com.pug.shared.application.UuidQuery;
import com.pug.shared.infra.persistence.Page;
import com.pug.shared.infra.persistence.PageRequest;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

@QuarkusTest
class SchoolServiceTest {

  @Inject SchoolService service;
  @Inject EntityManager em;

  private static SchoolEntity school(String name) {
    return SchoolEntity.builder().name(name).build();
  }

  @Test
  @TestTransaction
  void getByIdAndNameAndList() {
    var e = school("UFSC");
    em.persist(e);
    em.flush();
    em.clear();

    assertTrue(service.getById(new UuidQuery(e.getId())).isPresent());
    assertTrue(service.getByName(new StringQuery("ufsc")).isPresent());

    Page<?> page = service.listOrdered(new PageRequest(0, 10));
    assertFalse(page.items().isEmpty());
  }
}
