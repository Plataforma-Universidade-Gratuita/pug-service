package com.pug.geo.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.geo.domain.CitiesRepository;
import com.pug.geo.domain.City;
import com.pug.helpers.domainGenerators.CityGenerator;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class CitiesRepositoryImplCRUDTest {

  @Inject CitiesRepository citiesRepository;

  private final CityGenerator cityGen = new CityGenerator();

  @Test
  @Transactional
  public void testPersistCity() {
    City toSave = cityGen.randomCity();
    City saved = citiesRepository.persist(toSave);

    Optional<City> result = citiesRepository.findOptionalById(saved.getId());

    assertTrue(result.isPresent());
    assertEquals(saved.getName(), result.get().getName());
    assertEquals(saved.getIbgeCode().toString(), result.get().getIbgeCode().toString());
  }

  @Test
  @Transactional
  public void testFindCityById() {
    City saved = citiesRepository.persist(cityGen.randomCity());

    Optional<City> result = citiesRepository.findOptionalById(saved.getId());

    assertTrue(result.isPresent());
    assertEquals(saved.getId(), result.get().getId());
  }

  @Test
  @Transactional
  public void testFindCityByIbgeCode() {
    City saved = citiesRepository.persist(cityGen.randomCity());

    Optional<City> result = citiesRepository.findOptionalByIbgeCode(saved.getIbgeCode().toString());

    assertTrue(result.isPresent());
    assertEquals(saved.getIbgeCode().toString(), result.get().getIbgeCode().toString());
  }

  @Test
  @Transactional
  public void testCityNotFound() {
    Optional<City> result = citiesRepository.findOptionalByIbgeCode("0000000");
    assertFalse(result.isPresent());
  }

  @Test
  @Transactional
  public void testPersistAll() {
    List<City> toSave = Stream.generate(cityGen::randomCity).limit(10).toList();

    List<City> saved = citiesRepository.persistAll(toSave);

    for (City c : saved) {
      Optional<City> result = citiesRepository.findOptionalById(c.getId());
      assertTrue(result.isPresent());
      assertEquals(c.getName(), result.get().getName());
      assertEquals(c.getIbgeCode().toString(), result.get().getIbgeCode().toString());
    }
  }

  @Test
  @Transactional
  public void testDeleteByIds_single() {
    City c1 = citiesRepository.persist(cityGen.randomCity());
    City c2 = citiesRepository.persist(cityGen.randomCity());

    long deleted = citiesRepository.deleteByIds(List.of(c1.getId()));
    assertEquals(1L, deleted);

    assertFalse(citiesRepository.findOptionalById(c1.getId()).isPresent());
    assertTrue(citiesRepository.findOptionalById(c2.getId()).isPresent());
  }

  @Test
  @Transactional
  public void testDeleteByIds_multiple() {
    City c1 = citiesRepository.persist(cityGen.randomCity());
    City c2 = citiesRepository.persist(cityGen.randomCity());
    City c3 = citiesRepository.persist(cityGen.randomCity());

    long deleted = citiesRepository.deleteByIds(List.of(c1.getId(), c3.getId()));
    assertEquals(2L, deleted);

    assertFalse(citiesRepository.findOptionalById(c1.getId()).isPresent());
    assertFalse(citiesRepository.findOptionalById(c3.getId()).isPresent());
    assertTrue(citiesRepository.findOptionalById(c2.getId()).isPresent());
  }

  @Test
  @Transactional
  public void testDeleteByIds_mixedWithNonExisting() {
    City c1 = citiesRepository.persist(cityGen.randomCity());
    UUID ghost = UUID.randomUUID();

    long deleted = citiesRepository.deleteByIds(List.of(c1.getId(), ghost));
    assertEquals(1L, deleted);

    assertFalse(citiesRepository.findOptionalById(c1.getId()).isPresent());
  }
}
