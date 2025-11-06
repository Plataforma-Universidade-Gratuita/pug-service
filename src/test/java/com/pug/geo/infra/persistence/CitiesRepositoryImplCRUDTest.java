package com.pug.geo.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.geo.domain.CitiesRepository;
import com.pug.helpers.entityGenerators.CitiesEntityGenerator;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class CitiesRepositoryImplCRUDTest {

  @Inject CitiesRepository citiesRepository;

  private final CitiesEntityGenerator citiesEntityGenerator = new CitiesEntityGenerator();

  @Test
  @Transactional
  public void testPersistCity() {
    CitiesEntity city = citiesEntityGenerator.createRandomCitiesEntity();

    citiesRepository.persist(city);

    Optional<CitiesEntity> result = citiesRepository.findOptionalById(city.getId());

    assertTrue(result.isPresent());
    assertEquals(city.getName(), result.get().getName());
    assertEquals(city.getIbgeCode(), result.get().getIbgeCode());
  }

  @Test
  @Transactional
  public void testFindCityById() {
    CitiesEntity city = citiesEntityGenerator.createRandomCitiesEntity();
    citiesRepository.persist(city);

    Optional<CitiesEntity> result = citiesRepository.findOptionalById(city.getId());

    assertTrue(result.isPresent());
    assertEquals(city.getId(), result.get().getId());
  }

  @Test
  @Transactional
  public void testFindCityByIbgeCode() {
    CitiesEntity city = citiesEntityGenerator.createRandomCitiesEntity();
    citiesRepository.persist(city);

    Optional<CitiesEntity> result = citiesRepository.findOptionalByIbgeCode(city.getIbgeCode());

    assertTrue(result.isPresent());
    assertEquals(city.getIbgeCode(), result.get().getIbgeCode());
  }

  @Test
  @Transactional
  public void testCityNotFound() {
    String nonExistingIbgeCode = "0000000";

    Optional<CitiesEntity> result = citiesRepository.findOptionalByIbgeCode(nonExistingIbgeCode);

    assertFalse(result.isPresent());
  }

  @Test
  @Transactional
  public void testPersistAll() {
    List<CitiesEntity> cities =
        Stream.generate(citiesEntityGenerator::createRandomCitiesEntity)
            .limit(10)
            .collect(Collectors.toList());

    citiesRepository.persistAll(cities);

    for (CitiesEntity city : cities) {
      Optional<CitiesEntity> result = citiesRepository.findOptionalById(city.getId());
      assertTrue(result.isPresent());
      assertEquals(city.getName(), result.get().getName());
      assertEquals(city.getIbgeCode(), result.get().getIbgeCode());
    }
  }

  @Test
  public void testDeleteByIds_single() {
    CitiesEntity c1 = citiesEntityGenerator.createRandomCitiesEntity();
    CitiesEntity c2 = citiesEntityGenerator.createRandomCitiesEntity();
    citiesRepository.persistAll(List.of(c1, c2));

    long deleted = citiesRepository.deleteByIds(List.of(c1.getId()));
    assertEquals(1L, deleted);

    assertFalse(citiesRepository.findOptionalById(c1.getId()).isPresent());
    assertTrue(citiesRepository.findOptionalById(c2.getId()).isPresent());
  }

  @Test
  public void testDeleteByIds_multiple() {
    CitiesEntity c1 = citiesEntityGenerator.createRandomCitiesEntity();
    CitiesEntity c2 = citiesEntityGenerator.createRandomCitiesEntity();
    CitiesEntity c3 = citiesEntityGenerator.createRandomCitiesEntity();
    citiesRepository.persistAll(List.of(c1, c2, c3));

    long deleted = citiesRepository.deleteByIds(List.of(c1.getId(), c3.getId()));
    assertEquals(2L, deleted);

    assertFalse(citiesRepository.findOptionalById(c1.getId()).isPresent());
    assertFalse(citiesRepository.findOptionalById(c3.getId()).isPresent());
    assertTrue(citiesRepository.findOptionalById(c2.getId()).isPresent());
  }

  @Test
  public void testDeleteByIds_mixedWithNonExisting() {
    CitiesEntity c1 = citiesEntityGenerator.createRandomCitiesEntity();
    citiesRepository.persist(c1);

    java.util.UUID ghost = java.util.UUID.randomUUID();
    long deleted = citiesRepository.deleteByIds(List.of(c1.getId(), ghost));
    assertEquals(1L, deleted);

    assertFalse(citiesRepository.findOptionalById(c1.getId()).isPresent());
  }
}
