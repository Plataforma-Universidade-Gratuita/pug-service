package com.pug.geo.infra.persistence;

import com.pug.geo.domain.CitiesRepository;
import com.pug.helpers.entityGenerators.CitiesEntityGenerator;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class CitiesRepositoryImplCRUDTest {

  @Inject
  CitiesRepository citiesRepository;

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

    Optional<CitiesEntity> result = citiesRepository.findByIbgeCode(city.getIbgeCode());

    assertTrue(result.isPresent());
    assertEquals(city.getIbgeCode(), result.get().getIbgeCode());
  }

  @Test
  @Transactional
  public void testCityNotFound() {
    String nonExistingIbgeCode = "0000000";

    Optional<CitiesEntity> result = citiesRepository.findByIbgeCode(nonExistingIbgeCode);

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
}
