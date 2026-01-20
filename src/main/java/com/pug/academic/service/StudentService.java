package com.pug.academic.service;

import com.pug.academic.domain.Student;
import com.pug.academic.domain.StudentRepository;
import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.academic.domain.enums.Campi;
import com.pug.academic.domain.vos.AcademicRegistration;
import com.pug.academic.service.dtos.StudentCreateBulkCommand;
import com.pug.academic.service.dtos.StudentCreateCommand;
import com.pug.academic.service.dtos.StudentUpdateCommand;
import com.pug.identity.service.AccountService;
import com.pug.shared.domain.enums.DeleteKeys;
import com.pug.shared.utils.CollectionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Service class for managing Student entities. */
@ApplicationScoped
public class StudentService {

  @Inject StudentRepository repo;
  @Inject AccountService accountService;
  @Inject CourseService courseService;

  /**
   * Creates and saves a new Student entity.
   *
   * @param cmd the command containing student creation details
   * @param courseName the name of the course to associate with the student
   * @return the saved Student entity
   * @throws DuplicateResourceException if a student with the same registration already exists
   */
  @Transactional
  public Student save(StudentCreateCommand cmd, String courseName) {
    if (existsByRegistration(cmd.reg())) {
      throw new DuplicateResourceException(
          AcademicErrorCodes.STUDENT_ALREADY_EXISTS, Map.of("registration", cmd.reg()));
    }
    var account = accountService.save(cmd.accountCreateCommand());
    var course = courseService.getByName(courseName);
    var student =
        Student.createNew(
            account.getId(), cmd.reg(), cmd.campus(), course.getId(), cmd.hours(), cmd.period());
    return repo.persist(student);
  }

  @Transactional
  public List<Student> saveAll(Iterable<StudentCreateBulkCommand> cmds) {}

  /**
   * Updates an existing Student entity.
   *
   * @param id the UUID of the student to update
   * @param cmd the command containing student update details
   * @return the updated Student entity
   * @throws ResourceNotFoundException if the student with the given ID does not exist
   */
  @Transactional
  public Student update(UUID id, StudentUpdateCommand cmd) {
    Student current = getById(id);
    accountService.update(id, cmd.accountCommand());

    Campi campus = cmd.campus() != null ? cmd.campus() : current.getCampus();
    AcademicRegistration registration =
        cmd.academicRegistration() != null
            ? cmd.academicRegistration()
            : current.getAcademicRegistration();

    Student updated = current.changeCampus(campus).changeAcademicRegistration(registration);
    repo.update(updated);

    return getById(updated.getAccountId());
  }

  /**
   * Retrieves a Student entity by its UUID.
   *
   * @param userId the UUID of the student to retrieve
   * @return the Student entity
   * @throws ResourceNotFoundException if the student with the given ID does not exist
   */
  public Student getById(UUID userId) {
    return repo.findOptionalById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(AcademicErrorCodes.STUDENT_NOT_FOUND));
  }

  /**
   * Lists all Student entities by Course ID.
   *
   * @param courseId the UUID of the course
   * @return a list of Student entities enrolled in the specified course
   */
  public List<Student> listAllByCourseId(UUID courseId) {
    return repo.listAllByCourseId(courseId);
  }

  /**
   * Lists all Student entities.
   *
   * @return a list of all Student entities
   */
  public List<Student> listAll() {
    return repo.listAllStudents();
  }

  /**
   * Deletes Student entities by their UUIDs.
   *
   * @param ids the iterable of student UUIDs to delete
   * @return a map containing the count of deleted entities for each DeleteKey
   */
  @Transactional
  public Map<DeleteKeys, Long> deleteAll(Iterable<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return Map.of(
          DeleteKeys.STUDENTS, 0L,
          DeleteKeys.ACCOUNTS, 0L,
          DeleteKeys.USERS, 0L);
    }

    long deletedStudents = repo.deleteByIds(ids);
    var deletedAccounts = accountService.deleteAll(ids);
    return Map.of(
        DeleteKeys.STUDENTS, deletedStudents,
        DeleteKeys.ACCOUNTS, deletedAccounts.getOrDefault(DeleteKeys.ACCOUNTS, 0L),
        DeleteKeys.USERS, deletedAccounts.getOrDefault(DeleteKeys.USERS, 0L));
  }

  /**
   * Checks if any Student entities exist for the given account IDs.
   *
   * @param accountIds the iterable of account UUIDs to check
   * @return true if any Student entities exist for the given account IDs, false otherwise
   */
  public boolean existsAnyByAccountIdIn(Iterable<UUID> accountIds) {
    if (CollectionUtils.isEmpty(accountIds)) {
      return false;
    }
    return repo.existsAnyByAccountIdIn(accountIds);
  }

  /**
   * Checks if a Student entity exists with the given academic registration.
   *
   * @param registration the academic registration to check
   * @return true if a Student entity exists with the given registration, false otherwise
   */
  public boolean existsByRegistration(AcademicRegistration registration) {
    return repo.existsByRegistration(registration.toString());
  }
}
