package br.org.catolicasc.pug.helpers;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Base class for testing infrastructure mapper classes that convert between Domain and Entity
 * objects.
 *
 * <p>Provides standard contract tests for: round-trip mapping, null-safety on {@code toDomain} and
 * {@code toEntity}.
 *
 * @param <D> the domain type
 * @param <E> the persistence entity type
 */
public abstract class AbstractMapperTest<D, E> {

  /** Creates a valid domain instance for testing. */
  protected abstract D createDomain();

  /** Maps an entity to a domain object. Should delegate to the actual Mapper method. */
  protected abstract D mapToDomain(E entity);

  /** Maps a domain object to an entity. Should delegate to the actual Mapper method. */
  protected abstract E mapToEntity(D domain);

  /** Verifies that the round-trip mapping preserved the essential fields. */
  protected abstract void assertRoundTrip(D original, D mapped);

  @Test
  @DisplayName("Should perform round-trip mapping (Domain → Entity → Domain)")
  void shouldPerformRoundTrip() {
    D domain = createDomain();

    E entity = mapToEntity(domain);
    D mapped = mapToDomain(entity);

    assertRoundTrip(domain, mapped);
  }

  @Test
  @DisplayName("toDomain should return null when entity is null")
  void toDomainShouldReturnNullForNullEntity() {
    assertThat(mapToDomain(null)).isNull();
  }

  @Test
  @DisplayName("toEntity should return null when domain is null")
  void toEntityShouldReturnNullForNullDomain() {
    assertThat(mapToEntity(null)).isNull();
  }
}
