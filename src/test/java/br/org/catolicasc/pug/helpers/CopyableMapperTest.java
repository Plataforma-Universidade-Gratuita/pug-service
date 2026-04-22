package br.org.catolicasc.pug.helpers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Extension of {@link AbstractMapperTest} for mappers that also support a {@code copy(domain,
 * entity)} operation.
 *
 * <p>Adds standard null-safety contract tests for the copy method, plus a template for verifying
 * that copy correctly updates entity fields.
 *
 * @param <D> the domain type
 * @param <E> the persistence entity type
 */
public abstract class CopyableMapperTest<D, E> extends AbstractMapperTest<D, E> {

  /** Creates an entity instance with some default/old values for copy tests. */
  protected abstract E createEntity();

  /** Delegates to the actual Mapper.copy(domain, entity) method. */
  protected abstract void copy(D domain, E entity);

  @Test
  @DisplayName("copy should do nothing when domain is null")
  void copyShouldHandleNullDomain() {
    E entity = createEntity();
    copy(null, entity);
  }

  @Test
  @DisplayName("copy should do nothing when entity is null")
  void copyShouldHandleNullEntity() {
    D domain = createDomain();
    copy(domain, null);
  }

  @Test
  @DisplayName("copy should do nothing when both are null")
  void copyShouldHandleBothNull() {
    copy(null, null);
  }
}
