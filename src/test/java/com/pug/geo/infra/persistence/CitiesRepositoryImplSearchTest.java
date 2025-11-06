package com.pug.geo.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.geo.domain.CitiesRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import org.hibernate.search.mapper.orm.Search;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class CitiesRepositoryImplSearchTest {

  @Inject CitiesRepository citiesRepository;
  @Inject EntityManager em;

  @BeforeEach
  void setup() throws InterruptedException {
    seedTx();
    Search.session(em).massIndexer(CitiesEntity.class).purgeAllOnStart(true).startAndWait();
  }

  @Transactional
  void seedTx() {
    em.createQuery("delete from CitiesEntity").executeUpdate();
    em.persist(CitiesEntity.builder().name("Jaraguá do Sul").ibgeCode("1000001").build());
    em.persist(CitiesEntity.builder().name("Joinville").ibgeCode("1000002").build());
    em.persist(CitiesEntity.builder().name("Jaragoa do Sul").ibgeCode("1000003").build());
    em.persist(CitiesEntity.builder().name("Araquari").ibgeCode("1000004").build());
  }

  @Test
  void listAllCitiesReturnsSeeded() {
    var all = citiesRepository.listAllCities();
    assertEquals(4, all.size());
    assertTrue(all.stream().anyMatch(c -> c.getName().equals("Jaraguá do Sul")));
    assertTrue(all.stream().anyMatch(c -> c.getName().equals("Joinville")));
    assertTrue(all.stream().anyMatch(c -> c.getName().equals("Jaragoa do Sul")));
    assertTrue(all.stream().anyMatch(c -> c.getName().equals("Araquari")));
  }

  @Test
  void searchAraOrder() {
    List<String> names =
        citiesRepository.searchByName("ara").stream().map(CitiesEntity::getName).toList();
    assertEquals(3, names.size());
    assertEquals("Araquari", names.get(0));
    assertEquals("Jaragoa do Sul", names.get(1));
    assertEquals("Jaraguá do Sul", names.get(2));
  }

  @Test
  void searchJaraguaOrder() {
    List<String> names =
        citiesRepository.searchByName("jaragua").stream().map(CitiesEntity::getName).toList();
    assertEquals(2, names.size());
    assertEquals("Jaraguá do Sul", names.get(0));
    assertEquals("Jaragoa do Sul", names.get(1));
  }

  @Test
  void searchTypoJarguaDoSulOrder() {
    List<String> names =
        citiesRepository.searchByName("jargua do sul").stream().map(CitiesEntity::getName).toList();
    assertEquals(2, names.size());
    assertEquals("Jaraguá do Sul", names.get(0));
    assertEquals("Jaragoa do Sul", names.get(1));
  }
}
