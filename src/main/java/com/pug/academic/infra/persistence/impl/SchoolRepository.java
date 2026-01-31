package com.pug.academic.infra.persistence.impl;

import com.pug.academic.domain.ISchoolRepository;
import com.pug.academic.domain.School;
import com.pug.academic.infra.SchoolMapper;
import com.pug.academic.infra.persistence.SchoolEntity;
import com.pug.shared.utils.CollectionUtils;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Implementation of the SchoolRepository interface using PanacheRepositoryBase. */
@ApplicationScoped
public class SchoolRepository
    implements ISchoolRepository, PanacheRepositoryBase<SchoolEntity, UUID> {

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

    var domainObjects = new ArrayList<School>();
    for (SchoolEntity e : entities) {
      domainObjects.add(SchoolMapper.toDomain(e));
    }
    return domainObjects;
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
    Optional<SchoolEntity> entityOpt = findByIdOptional(id);
    return entityOpt.map(SchoolMapper::toDomain);
  }

  @Override
  public Optional<School> findOptionalByName(String name) {
    Optional<SchoolEntity> entityOpt = find("name", name).firstResultOptional();
    return entityOpt.map(SchoolMapper::toDomain);
  }

  @Override
  public List<School> listAllSchools() {
    var domainList = new ArrayList<School>();
    for (SchoolEntity entity : listAll()) {
      domainList.add(SchoolMapper.toDomain(entity));
    }
    return domainList;
  }

  @Override
  public boolean existsByName(String name) {
    return count("name = ?1", name) > 0;
  }

  @Override
  public boolean existsAnyByNameIn(Iterable<String> names) {
    if (CollectionUtils.isEmpty(names)) {
      return false;
    }
    return count("name in ?1", names) > 0;
  }
}
