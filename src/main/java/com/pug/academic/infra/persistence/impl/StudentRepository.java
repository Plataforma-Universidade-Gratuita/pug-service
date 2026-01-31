package com.pug.academic.infra.persistence.impl;

import com.pug.academic.domain.IStudentRepository;
import com.pug.academic.domain.Student;
import com.pug.academic.infra.StudentMapper;
import com.pug.academic.infra.persistence.StudentEntity;
import com.pug.shared.utils.CollectionUtils;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of StudentRepository using Panache for persistence operations.
 */
@ApplicationScoped
public class StudentRepository
        implements IStudentRepository, PanacheRepositoryBase<StudentEntity, UUID> {

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

  @Transactional
  @Override
  public List<Student> persistAll(Iterable<Student> students) {
    if (CollectionUtils.isEmpty(students)) {
      return List.of();
    }
    List<StudentEntity> batch = new ArrayList<>();
    for (Student s : students) {
      if (s != null) {
        batch.add(StudentMapper.toEntity(s));
      }
    }

    if (batch.isEmpty()) {
      return List.of();
    }

    persist(batch);
    flush();

    var domainObjects = new ArrayList<Student>();
    for (StudentEntity e : batch) {
      domainObjects.add(StudentMapper.toDomain(e));
    }
    return domainObjects;
  }

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

  @Transactional
  @Override
  public long deleteByIds(Iterable<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return 0L;
    }
    long n = delete("accountId in ?1", ids);
    flush();
    getEntityManager().clear();
    return n;
  }

  @Override
  public Optional<Student> findOptionalById(UUID id) {
    Optional<StudentEntity> entityOpt = findByIdOptional(id);
    return entityOpt.map(StudentMapper::toDomain);
  }

  @Override
  public List<Student> listAllStudents() {
    var domainList = new ArrayList<Student>();
    for (StudentEntity entity : listAll()) {
      domainList.add(StudentMapper.toDomain(entity));
    }
    return domainList;
  }

  @Override
  public List<Student> listAllByCourseId(UUID courseId) {
    var domainList = new ArrayList<Student>();
    for (StudentEntity entity : list("courseId", courseId)) {
      domainList.add(StudentMapper.toDomain(entity));
    }
    return domainList;
  }

  @Override
  public boolean existsAnyByRegistrationIn(Iterable<String> registrations) {
    if (CollectionUtils.isEmpty(registrations)) {
      return false;
    }
    return count("academicRegistration in ?1", registrations) > 0;
  }

  @Override
  public boolean existsAnyByAccountIdIn(Iterable<UUID> accountIds) {
    if (CollectionUtils.isEmpty(accountIds)) {
      return false;
    }
    return count("accountId in ?1", accountIds) > 0;
  }

  @Override
  public boolean existsAnyByCourseIdIn(Iterable<UUID> courseIds) {
    if (CollectionUtils.isEmpty(courseIds)) {
      return false;
    }
    return count("courseId in ?1", courseIds) > 0;
  }
}