package br.org.catolicasc.pug.academic.infra.persistence.impl;

import br.org.catolicasc.pug.academic.domain.AreaOfExpertise;
import br.org.catolicasc.pug.academic.domain.AreaOfExpertiseRepository;
import br.org.catolicasc.pug.academic.infra.AreaOfExpertiseMapper;
import br.org.catolicasc.pug.academic.infra.persistence.AreaOfExpertiseEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the {@link AreaOfExpertiseRepository} utilizing Hibernate ORM with Panache.
 *
 * <p>This application-scoped bean bridges the pure domain repository interface with the underlying
 * database infrastructure. It handles standard CRUD operations and ensures proper mapping between
 * {@link AreaOfExpertise} domain aggregates and their JPA counterparts.
 */
@ApplicationScoped
public class AreaOfExpertiseRepositoryImpl
    implements AreaOfExpertiseRepository, PanacheRepositoryBase<AreaOfExpertiseEntity, UUID> {

  /** {@inheritDoc} */
  @Transactional
  @Override
  public boolean deleteById(UUID id) {
    if (id == null) {
      return false;
    }
    var deleted = delete("id", id) > 0;
    flush();
    return deleted;
  }

  /** {@inheritDoc} */
  @Override
  public boolean existsByName(String name) {
    return count("name = ?1", name) > 0;
  }

  /** {@inheritDoc} */
  @Override
  public Optional<AreaOfExpertise> findOptionalById(UUID id) {
    Optional<AreaOfExpertiseEntity> entityOpt = findByIdOptional(id);
    return entityOpt.map(AreaOfExpertiseMapper::toDomain);
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public AreaOfExpertise persist(AreaOfExpertise school) {
    if (school == null) {
      return null;
    }
    var e = AreaOfExpertiseMapper.toEntity(school);
    persistAndFlush(e);
    return AreaOfExpertiseMapper.toDomain(e);
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public void update(AreaOfExpertise school) {
    if (school == null || school.getId() == null) {
      return;
    }
    AreaOfExpertiseEntity managed = findById(school.getId());
    if (managed != null) {
      AreaOfExpertiseMapper.copy(school, managed);
    }
  }
}
