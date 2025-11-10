package com.pug.academic.infra.persistence;

import com.pug.academic.domain.Student;
import com.pug.academic.domain.StudentRepository;
import com.pug.academic.infra.StudentMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Implementation of StudentRepository using Panache for persistence operations. */
@ApplicationScoped
public class StudentRepositoryImpl
        implements StudentRepository, PanacheRepositoryBase<StudentEntity, UUID> {

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
    if (students == null || !students.iterator().hasNext()) {
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
    return batch.stream().map(StudentMapper::toDomain).toList();
  }

  @Transactional
  @Override
  public long deleteByIds(Iterable<UUID> userIds) {
    if (userIds == null || !userIds.iterator().hasNext()) {
      return 0L;
    }
    long n = delete("userId in ?1", userIds);
    flush();
    getEntityManager().clear();
    return n;
  }

  @Override
  public Optional<Student> findOptionalById(UUID userId) {
    return findByIdOptional(userId).map(StudentMapper::toDomain);
  }

  @Override
  public List<Student> listAllStudents() {
    return listAll().stream().map(StudentMapper::toDomain).toList();
  }

  @Override
  public List<Student> listAllByCourseId(UUID courseId) {
    return find("courseId", courseId).list().stream().map(StudentMapper::toDomain).toList();
  }

  @Override
  public boolean existsByRegistration(String registration) {
    if (registration == null || registration.isBlank()) {
      return false;
    }
    return find("academicRegistration", registration.trim()).firstResultOptional().isPresent();
  }
}
