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
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/** Service class for managing Student entities. */
@ApplicationScoped
public class StudentService {

  @Inject StudentRepository repo;
  @Inject UserService users;
  @Inject PasswordService passwords;

  /**
   * Save a new Student.
   *
   * @param cpf the CPF
   * @param name the name
   * @param email the email
   * @param rawPassword the raw password
   * @param reg the academic registration
   * @param campus the campus
   * @param courseId the course ID
   * @param hours the counterpart hours
   * @param period the period
   * @return the saved Student
   * @throws DuplicateResourceException if a Student with the same registration already exists
   */
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

    Objects.requireNonNull(cpf, "cpf");
    Objects.requireNonNull(name, "name");
    Objects.requireNonNull(email, "email");
    Objects.requireNonNull(rawPassword, "rawPassword");
    Objects.requireNonNull(reg, "reg");
    Objects.requireNonNull(campus, "campus");
    Objects.requireNonNull(courseId, "courseId");
    Objects.requireNonNull(hours, "hours");
    Objects.requireNonNull(period, "period");

    if (repo.existsByRegistration(reg.toString())) {
      throw new DuplicateResourceException(AcademicErrorCodes.STUDENT_ALREADY_EXISTS);
    }

    String hash = passwords.hash(rawPassword);
    var user = users.save(cpf, name, email, AccountType.STUDENT, hash);
    var student = Student.createNew(user.getId(), reg, campus, courseId, hours, period);
    return repo.persist(student);
  }

  /**
   * Update an existing Student.
   *
   * @param userId the user ID
   * @param campus the new campus
   * @param courseId the new course ID
   * @param period the new period
   * @return the updated Student
   * @throws ResourceNotFoundException if the Student is not found
   */
  @Transactional
  public Student update(UUID userId, Campi campus, UUID courseId, Period period) {
    Objects.requireNonNull(userId, "userId");
    Objects.requireNonNull(campus, "campus");
    Objects.requireNonNull(courseId, "courseId");
    Objects.requireNonNull(period, "period");

    Student current =
        repo.findOptionalById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(AcademicErrorCodes.STUDENT_NOT_FOUND));

    Student updated = current;
    if (current.getCampus() != campus) {
      updated = updated.changeCampus(campus);
    }
    if (!current.getCourseId().equals(courseId)) {
      updated = updated.moveToCourse(courseId);
    }
    if (!current.getPeriod().equals(period)) {
      updated = updated.changePeriod(period);
    }

    repo.update(updated);

    return repo.findOptionalById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(AcademicErrorCodes.STUDENT_NOT_FOUND));
  }

  /**
   * Get a Student by user ID.
   *
   * @param userId the user ID
   * @return the Student
   * @throws ResourceNotFoundException if the Student is not found
   */
  public Student get(UUID userId) {
    Objects.requireNonNull(userId, "userId");
    return repo.findOptionalById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(AcademicErrorCodes.STUDENT_NOT_FOUND));
  }

  /**
   * Get all Students by a list of user IDs.
   *
   * @param userIds the list of user IDs
   * @return the list of Students
   */
  public List<Student> getAllByIds(Iterable<UUID> userIds) {
    Objects.requireNonNull(userIds, "userIds");
    return repo.listAllByIds(userIds);
  }

  /**
   * List all Students.
   *
   * @return the list of all Students
   */
  public List<Student> listAll() {
    return repo.listAllStudents();
  }

  /**
   * Delete Students by a list of user IDs.
   *
   * @param userIds the list of user IDs
   * @return a map with the count of deleted students and users
   * @throws ResourceNotFoundException if any Student is not found
   */
  @Transactional
  public Map<String, Long> delete(Iterable<UUID> userIds) {
    Objects.requireNonNull(userIds, "userIds");

    var ids = toStream(userIds).filter(Objects::nonNull).toList();
    if (ids.isEmpty()) {
      return Map.of("students", 0L, "users", 0L);
    }

    int found = repo.listAllByIds(ids).size();
    if (found != ids.size()) {
      throw new ResourceNotFoundException(AcademicErrorCodes.STUDENT_NOT_FOUND);
    }

    long students = repo.deleteByIds(ids);
    long usersDeleted = users.deleteByIds(ids);
    return Map.of("students", students, "users", usersDeleted);
  }

  /**
   * Convert Iterable to Stream.
   *
   * @param it the iterable
   * @param <T> the type
   * @return the stream
   */
  private static <T> Stream<T> toStream(Iterable<T> it) {
    return it == null ? Stream.empty() : StreamSupport.stream(it.spliterator(), false);
  }
}
