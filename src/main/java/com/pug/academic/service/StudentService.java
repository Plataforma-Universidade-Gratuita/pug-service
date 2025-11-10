// src/main/java/com/pug/academic/service/StudentService.java
package com.pug.academic.service;

import com.pug.academic.domain.Student;
import com.pug.academic.domain.StudentRepository;
import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.academic.domain.enums.Campi;
import com.pug.academic.domain.vos.AcademicRegistration;
import com.pug.academic.domain.vos.CounterpartHours;
import com.pug.academic.domain.vos.Period;
import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.domain.vos.Email;
import com.pug.identity.service.PasswordService;
import com.pug.identity.service.UserService;
import com.pug.shared.domain.enums.AccountType;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class StudentService {

  @Inject StudentRepository repo;
  @Inject UserService users;
  @Inject PasswordService passwords;

  @Transactional
  public Student save(
      Cpf cpf,
      String name,
      Email email,
      String rawPassword,
      AcademicRegistration reg,
      Campi campus,
      UUID courseId,
      CounterpartHours hours,
      Period period) {

    if (repo.existsByRegistration(reg.toString())) {
      throw new DuplicateResourceException(AcademicErrorCodes.STUDENT_ALREADY_EXISTS);
    }
    String hash = passwords.hash(rawPassword);
    var user = users.save(cpf, name, email, AccountType.STUDENT, hash);
    var student = Student.createNew(user.getId(), reg, campus, courseId, hours, period);
    return repo.persist(student);
  }

  public Student get(UUID userId) {
    return repo.findOptionalById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(AcademicErrorCodes.STUDENT_NOT_FOUND));
  }

  public List<Student> listAll() {
    return repo.listAllStudents();
  }

  @Transactional
  public void revoke(UUID userId) {
    repo.findOptionalById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(AcademicErrorCodes.STUDENT_NOT_FOUND));
    repo.deleteByIds(List.of(userId));
    users.deleteByIds(List.of(userId));
  }
}
