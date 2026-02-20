package com.pug.academic.service;

import com.pug.academic.domain.Student;
import com.pug.academic.service.dtos.StudentCreateCommand;
import com.pug.academic.service.dtos.StudentUpdateCommand;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Interface for managing Student entities. */
public interface StudentService {

  /**
   * Saves a new Student entity.
   *
   * <p>This method also creates and saves the associated Account.
   *
   * @param cmd the command containing the data to create the new Student.
   * @return the saved Student entity.
   * @throws com.pug.shared.exceptions.DuplicateResourceException if a student with the same
   *     academic registration already exists, or if an account with the given email already exists.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the associated course does not
   *     exist.
   * @throws com.pug.shared.exceptions.AppValidationException if input validation fails.
   */
  Student save(StudentCreateCommand cmd);

  /**
   * Saves multiple new Student entities.
   *
   * <p>This method also creates and saves the associated Accounts for each student.
   *
   * @param cmds an iterable of commands for student creation.
   * @return a list of saved Student entities.
   * @throws com.pug.shared.exceptions.DuplicateResourceException if any student with the same
   *     academic registration already exists, or if there are duplicate registrations or emails in
   *     the input commands.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if any associated course does not
   *     exist.
   * @throws com.pug.shared.exceptions.AppValidationException if input validation fails for any
   *     student in the bulk.
   */
  List<Student> saveAll(Iterable<StudentCreateCommand> cmds);

  /**
   * Updates an existing Student entity.
   *
   * <p>This method also updates the associated Account.
   *
   * @param accountId the UUID of the student's account to update.
   * @param cmd the command containing the new data for the student.
   * @return the updated Student entity.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the student with the given
   *     account ID does not exist, or if the new course does not exist.
   * @throws com.pug.shared.exceptions.DuplicateResourceException if a student with the new academic
   *     registration already exists, or if an account with the new email already exists.
   * @throws com.pug.shared.exceptions.AppValidationException if input validation fails.
   */
  Student update(UUID accountId, StudentUpdateCommand cmd);

  /**
   * Deletes Student entities by their account IDs.
   *
   * @param accountIds an iterable of UUIDs representing the student's account IDs to delete.
   * @return a map containing the count of deleted entities for each DeleteKeys.
   * @throws DataIntegrityException if any student is still referenced
   *     by other modules (e.g. Enrollments).
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if a student's account is not found
   *     during cascade deletion.
   */
  Map<DeleteKeys, Long> deleteAll(Iterable<UUID> accountIds);

  /**
   * Lists all Student entities.
   *
   * @return a list of all Student entities.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if no student is found (or data is
   *     corrupted in DB).
   * @throws com.pug.shared.exceptions.AppValidationException if any Student entity found is
   *     corrupted in the database.
   */
  List<Student> listAll();

  /**
   * Lists all Student entities associated with a specific course ID.
   *
   * @param courseId the UUID of the course.
   * @return a list of Student entities associated with the given course ID.
   * @throws com.pug.shared.exceptions.AppValidationException if any Student entity found is
   *     corrupted in the database.
   */
  List<Student> listAllByCourseId(UUID courseId);

  /**
   * Retrieves a Student entity by its account ID.
   *
   * @param accountId the UUID of the student's account.
   * @return the Student entity.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the student with the given
   *     account ID does not exist (or data is corrupted in DB).
   * @throws com.pug.shared.exceptions.AppValidationException if the student is found but its data
   *     is corrupted in the database.
   */
  Student getById(UUID accountId);

  /**
   * Checks if any Student entities exist with account IDs in the provided iterable.
   *
   * @param accountIds an iterable of account IDs to check.
   * @return true if any Student entities exist with the given account IDs, false otherwise.
   */
  boolean existsAnyByAccountIdIn(Iterable<UUID> accountIds);

  /**
   * Checks if any Student entities exist with academic registrations in the provided iterable.
   *
   * @param registrations an iterable of academic registrations to check.
   * @return true if any Student entities exist with the given registrations, false otherwise.
   */
  boolean existsAnyByRegistrationIn(Iterable<String> registrations);

  /**
   * Checks if any Student entity exists with the given academic registration.
   *
   * @param registration the academic registration to check.
   * @return true if any Student entity exists with the given registration, false otherwise.
   */
  boolean existsByRegistration(String registration);

  /**
   * Checks if any Student entities exist for the given course IDs. This is used by
   * CourseService.deleteAll to prevent deleting referenced courses.
   *
   * @param courseIds an iterable of course IDs to check.
   * @return true if any student is associated with any of the given course IDs, false otherwise.
   */
  boolean existsAnyByCourseIdIn(Iterable<UUID> courseIds);
}
