package com.pug.academic.infra.persistence;

import com.pug.academic.domain.School;
import com.pug.academic.domain.SchoolRepository;
import com.pug.academic.infra.SchoolMapper;
import com.pug.shared.utils.CollectionUtils;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the SchoolRepository interface using PanacheRepositoryBase for CRUD operations
 * on SchoolEntity.
 */
@ApplicationScoped
public class SchoolRepositoryImpl
    implements SchoolRepository, PanacheRepositoryBase<SchoolEntity, UUID> {

  @Inject EntityManager entityManager;

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
  public List<School> persistAll(Iterable<School> schools) {
    if (CollectionUtils.isEmpty(schools)) {
      return List.of();
    }
    var entities = new ArrayList<SchoolEntity>();
    for (School s : schools) {
      if (s != null) {
        entities.add(SchoolMapper.toEntity(s));
      }
    }
    if (entities.isEmpty()) {
      return List.of();
    }
    persist(entities);
    flush();
    return entities.stream().map(SchoolMapper::toDomain).toList();
  }

  @Transactional
  @Override
  public long deleteByIds(Iterable<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return 0L;
    }
    long deleted = delete("id in ?1", ids);
    flush();
    getEntityManager().clear();
    return deleted;
  }

  @Override
  public Optional<School> findOptionalById(UUID id) {
    return findByIdOptional(id).map(SchoolMapper::toDomain);
  }

  @Override
  public List<School> listAllSchools() {
    return listAll().stream().map(SchoolMapper::toDomain).toList();
  }

  @Override
  public Optional<School> findOptionalByName(String name) {
    return find("name", name).firstResultOptional().map(SchoolMapper::toDomain);
  }

  @Override
  public boolean existsByName(String name) {
    return find("name", name).firstResultOptional().isPresent();
  }

  @Override
  public boolean existsAnyByNameIn(Iterable<String> names) {
    if (CollectionUtils.isEmpty(names)) {
      return false;
    }
    return find("name in ?1", names).firstResultOptional().isPresent();
  }

  @Override
  public void update(School school) {
    if (school == null || school.getId() == null) {
      return;
    }
    SchoolEntity managed = findById(school.getId());
    if (managed == null) {
      return;
    }
    SchoolMapper.copy(school, managed);
  }
}
