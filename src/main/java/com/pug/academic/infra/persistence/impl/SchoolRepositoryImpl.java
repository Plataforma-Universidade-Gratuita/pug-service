package com.pug.academic.infra.persistence.impl;

import com.pug.academic.domain.School;
import com.pug.academic.domain.SchoolRepository;
import com.pug.academic.infra.SchoolMapper;
import com.pug.academic.infra.persistence.SchoolEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

/** Implementation of the SchoolRepository interface using PanacheRepositoryBase. */
@ApplicationScoped
public class SchoolRepositoryImpl
    implements SchoolRepository, PanacheRepositoryBase<SchoolEntity, UUID> {

  @Transactional
  @Override
  public School persist(School school) {
    if (school == null) {
      return null;
    }
    var e = SchoolMapper.toEntity(school);
    persistAndFlush(e);
    return SchoolMapper.toDomain(e);
  }

  @Transactional
  @Override
  public void update(School school) {
    if (school == null || school.getId() == null) {
      return;
    }
    SchoolEntity managed = findById(school.getId());
    if (managed != null) {
      SchoolMapper.copy(school, managed);
    }
  }

  @Transactional
  @Override
  public boolean deleteById(UUID id) {
    if (id == null) {
      return false;
    }
    var deleted = PanacheRepositoryBase.super.deleteById(id);
    flush();
    return deleted;
  }

  @Override
  public Optional<School> findOptionalById(UUID id) {
    Optional<SchoolEntity> entityOpt = findByIdOptional(id);
    return entityOpt.map(SchoolMapper::toDomain);
  }

  @Override
  public boolean existsByName(String name) {
    return count("name = ?1", name) > 0;
  }
}
