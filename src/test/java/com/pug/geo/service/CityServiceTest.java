package com.pug.geo.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.geo.infra.persistence.CityEntity;
import com.pug.geo.service.queries.SearchCitiesQuery;
import com.pug.shared.infra.persistence.Page;
import com.pug.shared.infra.persistence.PageRequest;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CityServiceTest {

  @Inject CityService service;
  @Inject EntityManager em;

  private static CityEntity city(String name, String ibge) {
    return CityEntity.builder().name(name).ibgeCode(ibge).build();
  }

  private static String ibge() {
    String s = String.valueOf(Math.abs(UUID.randomUUID().getLeastSignificantBits()));
    return s.substring(0, 7);
  }

  @Test
  @TestTransaction
  void searchDelegatesAndReturnsPage() {
    var name = "São José-" + UUID.randomUUID().toString().substring(0, 6);
    em.persist(city(name, ibge()));
    em.flush();
    em.clear();

    Page<?> out = service.search(new SearchCitiesQuery("sao jose", new PageRequest(0, 10)));
    assertFalse(out.items().isEmpty());
    assertTrue(out.items().stream().anyMatch(o -> o.toString().contains("São José")));
  }
}
