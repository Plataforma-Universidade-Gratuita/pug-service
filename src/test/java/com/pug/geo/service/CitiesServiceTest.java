package com.pug.geo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pug.geo.domain.CitiesRepository;
import com.pug.geo.domain.City;
import com.pug.geo.domain.errors.GeoErrorCodes;
import com.pug.geo.domain.vos.IbgeCode;
import com.pug.helpers.domainGenerators.CityGenerator;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.text.StringUtils;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CitiesServiceTest {

  @Inject CitiesService service;

  @InjectMock CitiesRepository repo;

  private final CityGenerator gen = new CityGenerator();

  @Test
  void save_ok_persists_and_returns_city() {
    City input = gen.randomCity();
    City saved = input.toBuilder().id(UUID.randomUUID()).build();

    when(repo.existsByIbgeCode(input.getIbgeCode().toString())).thenReturn(false);
    when(repo.persist(input)).thenReturn(saved);

    City out = service.save(input);

    assertNotNull(out);
    assertEquals(saved.getId(), out.getId());
    assertEquals(saved.getName(), out.getName());
    assertEquals(saved.getIbgeCode().toString(), out.getIbgeCode().toString());
    verify(repo).existsByIbgeCode(input.getIbgeCode().toString());
    verify(repo).persist(input);
    verify(repo, never()).findOptionalByIbgeCode(org.mockito.ArgumentMatchers.anyString());
  }

  @Test
  void save_duplicate_ibge_throws() {
    City input = gen.randomCity();
    when(repo.existsByIbgeCode(input.getIbgeCode().toString())).thenReturn(true);

    DuplicateResourceException ex =
        assertThrows(DuplicateResourceException.class, () -> service.save(input));

    assertEquals(GeoErrorCodes.CITY_ALREADY_EXISTS, ex.code());
    verify(repo, never()).persist(any());
  }

  @Test
  void saveAll_ok_persists_all_when_no_duplicates() {
    List<City> batch = Stream.generate(gen::randomCity).limit(3).toList();
    List<String> codes = batch.stream().map(c -> c.getIbgeCode().toString()).toList();
    List<City> saved =
        batch.stream().map(c -> c.toBuilder().id(UUID.randomUUID()).build()).toList();

    when(repo.existsAnyByIbgeCodeIn(codes)).thenReturn(false);
    when(repo.persistAll(batch)).thenReturn(saved);

    List<City> out = service.saveAll(batch);

    assertEquals(3, out.size());
    assertTrue(out.stream().allMatch(c -> c.getId() != null));
    verify(repo).persistAll(batch);
  }

  @Test
  void saveAll_duplicate_any_throws() {
    List<City> batch = Stream.generate(gen::randomCity).limit(2).toList();
    List<String> codes = batch.stream().map(c -> c.getIbgeCode().toString()).toList();
    when(repo.existsAnyByIbgeCodeIn(codes)).thenReturn(true);

    DuplicateResourceException ex =
        assertThrows(DuplicateResourceException.class, () -> service.saveAll(batch));

    assertEquals(GeoErrorCodes.CITY_ALREADY_EXISTS, ex.code());
    verify(repo, never()).persistAll(any());
  }

  @Test
  void update_ok_updates_and_returns_city() {
    UUID id = UUID.randomUUID();
    City existing =
        City.builder().id(id).name("Old Name").ibgeCode(new IbgeCode("1234567")).build();
    City updated = existing.toBuilder().name("New Name").ibgeCode(new IbgeCode("7654321")).build();

    when(repo.findOptionalById(id)).thenReturn(Optional.of(existing), Optional.of(updated));

    City out = service.update(id, updated);

    assertEquals("New Name", out.getName());
    assertEquals("7654321", out.getIbgeCode().toString());
    verify(repo).update(any(City.class));
  }

  @Test
  void update_not_found_throws() {
    UUID id = UUID.randomUUID();
    when(repo.findOptionalById(id)).thenReturn(Optional.empty());

    ResourceNotFoundException ex =
        assertThrows(ResourceNotFoundException.class, () -> service.update(id, gen.randomCity()));

    assertEquals(GeoErrorCodes.CITY_NOT_FOUND, ex.code());
  }

  @Test
  void deleteByIds_returns_count_from_repo() {
    List<UUID> ids = List.of(UUID.randomUUID(), UUID.randomUUID());
    when(repo.deleteByIds(ids)).thenReturn(2L);

    long removed = service.deleteByIds(ids);

    assertEquals(2L, removed);
  }

  @Test
  void listAll_returns_domain() {
    City c1 = gen.randomCity().toBuilder().id(UUID.randomUUID()).build();
    City c2 = gen.randomCity().toBuilder().id(UUID.randomUUID()).build();
    when(repo.listAllCities()).thenReturn(List.of(c1, c2));

    List<City> out = service.listAll();

    assertEquals(2, out.size());
    assertEquals(c1.getIbgeCode().toString(), out.get(0).getIbgeCode().toString());
    assertEquals(c2.getIbgeCode().toString(), out.get(1).getIbgeCode().toString());
  }

  @Test
  void getById_ok_returns_city() {
    City c = gen.randomCity().toBuilder().id(UUID.randomUUID()).build();
    when(repo.findOptionalById(c.getId())).thenReturn(Optional.of(c));

    City out = service.getById(c.getId());

    assertEquals(c.getIbgeCode().toString(), out.getIbgeCode().toString());
  }

  @Test
  void getById_not_found_throws() {
    UUID id = UUID.randomUUID();
    when(repo.findOptionalById(id)).thenReturn(Optional.empty());

    ResourceNotFoundException ex =
        assertThrows(ResourceNotFoundException.class, () -> service.getById(id));

    assertEquals(GeoErrorCodes.CITY_NOT_FOUND, ex.code());
  }

  @Test
  void getByIbgeCode_ok_returns_city() {
    City c = gen.randomCity().toBuilder().id(UUID.randomUUID()).build();
    when(repo.findOptionalByIbgeCode(c.getIbgeCode().toString())).thenReturn(Optional.of(c));

    City out = service.getByIbgeCode(c.getIbgeCode().toString());

    assertEquals(c.getName(), out.getName());
  }

  @Test
  void getByIbgeCode_not_found_throws() {
    when(repo.findOptionalByIbgeCode("0000000")).thenReturn(Optional.empty());

    ResourceNotFoundException ex =
        assertThrows(ResourceNotFoundException.class, () -> service.getByIbgeCode("0000000"));

    assertEquals(GeoErrorCodes.CITY_NOT_FOUND, ex.code());
  }

  @Test
  void search_folds_and_lowercases_query_then_passes_to_repo() {
    City c = gen.randomCity().toBuilder().id(UUID.randomUUID()).build();
    String query = "JaráGuá do SÚL";
    String expectedKey = StringUtils.fold(query).toLowerCase(Locale.ROOT);

    when(repo.searchByName(expectedKey)).thenReturn(List.of(c));

    List<City> out = service.search(query);

    assertEquals(1, out.size());
    assertEquals(c.getIbgeCode().toString(), out.getFirst().getIbgeCode().toString());
    verify(repo).searchByName(expectedKey);
  }
}
