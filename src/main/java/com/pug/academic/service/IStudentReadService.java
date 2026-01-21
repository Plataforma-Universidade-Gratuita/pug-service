package com.pug.academic.service;

import com.pug.academic.infra.read.dtos.StudentView;
import java.util.List;
import java.util.UUID;

/** Interface for reading Student data. */
public interface IStudentReadService {

  /**
   * Retrieves a StudentView by its unique identifier (Account ID).
   *
   * @param accountId the UUID of the Student's account
   * @return the StudentView
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if no Student with the given ID is
   *     found
   */
  StudentView getView(UUID accountId);

  /**
   * Retrieves a StudentView by its academic registration.
   *
   * @param academicRegistration the academic registration of the Student
   * @return the StudentView
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if no Student with the given
   *     academic registration is found
   */
  StudentView getViewByAcademicRegistration(String academicRegistration);

  /**
   * Lists all StudentViews.
   *
   * @return a list of all StudentViews
   */
  List<StudentView> listViews();

  /**
   * Lists all StudentViews by course ID.
   *
   * @param courseId the UUID of the course
   * @return a list of StudentViews enrolled in the specified course
   */
  List<StudentView> listViewsByCourseId(UUID courseId);

  /**
   * Lists all StudentViews by a collection of account IDs.
   *
   * @param accountIds an iterable of UUIDs representing account IDs
   * @return a list of StudentViews corresponding to the provided account IDs
   */
  List<StudentView> listViewsByIds(Iterable<UUID> accountIds);

  /**
   * Searches for StudentView objects by name (of the associated user).
   *
   * @param query the search query.
   * @return a list of StudentView objects matching the search key.
   */
  List<StudentView> searchByName(String query);
}
