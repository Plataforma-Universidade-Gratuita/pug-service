package com.pug.academic.infra.persistence.impl;

import com.pug.academic.domain.Student;
import com.pug.academic.domain.StudentRepository;
import com.pug.academic.infra.StudentMapper;
import com.pug.academic.infra.persistence.StudentEntity;
import com.pug.shared.utils.StringUtils;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the {@link StudentRepository} utilizing Hibernate ORM with Panache.
 *
 * <p>This application-scoped bean bridges the pure domain repository interface with the underlying
 * database infrastructure. It manages transaction boundaries, entity state transitions, and the
 * mapping of complex nested Value Objects into the flattened {@link StudentEntity}.
 */
@ApplicationScoped
public class StudentRepositoryImpl
    implements StudentRepository, PanacheRepositoryBase<StudentEntity, UUID> {

  /** {@inheritDoc} */
  @Transactional
  @Override
  public Student persist(Student student) {
    if (student == null) {
      return null;
    }
    StudentEntity e = StudentMapper.toEntity(student);
    persistAndFlush(e);
    return StudentMapper.toDomain(e);
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public void update(Student student) {
    if (student == null || student.getAccountId() == null) {
      return;
    }
    StudentEntity entity = findById(student.getAccountId());
    if (entity != null) {
      StudentMapper.copy(student, entity);
    }
  }

  /** {@inheritDoc} */
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

  /** {@inheritDoc} */
  @Override
  public Optional<Student> findOptionalById(UUID id) {
    Optional<StudentEntity> entityOpt = findByIdOptional(id);
    return entityOpt.map(StudentMapper::toDomain);
  }

  /** {@inheritDoc} */
  @Override
  public boolean existsByRegistration(String registration) {
    if (StringUtils.isEmpty(registration)) {
      return false;
    }
    return count("academicRegistration", registration) > 0;
  }
}
