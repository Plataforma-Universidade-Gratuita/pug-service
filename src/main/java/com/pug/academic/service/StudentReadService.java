package com.pug.academic.service;

import com.pug.academic.infra.read.dtos.StudentViewWithCompletedHours;
import java.util.List;
import java.util.UUID;

/** Interface for reading Student data. */
public interface StudentReadService {

  /**
   * Retrieves a StudentViewWithCompletedHours by its unique identifier (Account ID).
   *
   * @param accountId the UUID of the Student's account
   * @return the StudentViewWithCompletedHours
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if no Student with the given ID is
   *     found
   */
  StudentViewWithCompletedHours getView(UUID accountId);

  /**
   * Retrieves a StudentViewWithCompletedHours by its academic registration.
   *
   * @param academicRegistration the academic registration of the Student
   * @return the StudentViewWithCompletedHours
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if no Student with the given
   *     academic registration is found
   */
  StudentViewWithCompletedHours getViewByAcademicRegistration(String academicRegistration);

  /**
   * Lists all StudentViews.
   *
   * @return a list of all StudentViews
   */
  List<StudentViewWithCompletedHours> listViews();

  /**
   * Lists all StudentViews by course ID.
   *
   * @param courseId the UUID of the course
   * @return a list of StudentViews enrolled in the specified course
   */
  List<StudentViewWithCompletedHours> listViewsByCourseId(UUID courseId);

  /**
   * Lists all StudentViews by a collection of account IDs.
   *
   * @param accountIds an iterable of UUIDs representing account IDs
   * @return a list of StudentViews corresponding to the provided account IDs
   */
  List<StudentViewWithCompletedHours> listViewsByIds(Iterable<UUID> accountIds);

  /**
   * Searches for StudentViewWithCompletedHours objects by name (of the associated account).
   *
   * @param query the search query.
   * @return a list of StudentViewWithCompletedHours objects matching the search key.
   */
  List<StudentViewWithCompletedHours> searchByName(String query);
}
