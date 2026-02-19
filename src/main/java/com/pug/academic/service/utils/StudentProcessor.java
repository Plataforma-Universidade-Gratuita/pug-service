package com.pug.academic.service.utils;

import com.pug.academic.domain.Student;
import com.pug.academic.domain.enums.Campi;
import com.pug.academic.domain.vos.AcademicRegistration;
import com.pug.academic.domain.vos.CounterpartHours;
import com.pug.academic.domain.vos.Period;
import com.pug.shared.utils.StringUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/** Utility class for processing Student DTO inputs. */
public class StudentProcessor {

  /**
   * Helper method to process DTO input and build a new Student domain object.
   *
   * @param accountId The account ID associated with the student.
   * @param regString The academic registration string.
   * @param campus The campus.
   * @param courseId The course ID.
   * @param requiredHours The required hours.
   * @param completedHours The completed hours.
   * @param startDate The start date.
   * @param dueDate The due date.
   * @return The constructed Student domain object (may contain errors).
   */
  public static Student processCreateInput(
      UUID accountId,
      String regString,
      Campi campus,
      UUID courseId,
      BigDecimal requiredHours,
      BigDecimal completedHours,
      LocalDate startDate,
      LocalDate dueDate) {

    AcademicRegistration regVo = AcademicRegistration.factory(regString);
    CounterpartHours hoursVo = CounterpartHours.factory(requiredHours, completedHours);
    Period periodVo = Period.factory(startDate, dueDate);

    return Student.factory(accountId, regVo, campus, courseId, hoursVo, periodVo);
  }

  /**
   * Helper method to process DTO input and update an existing Student domain object.
   *
   * @param existingStudent The existing student to be updated.
   * @param regString The academic registration string (can be null).
   * @param campus The campus (can be null).
   * @param courseId The course ID (can be null).
   * @param requiredHours The required hours (can be null).
   * @param completedHours The completed hours (can be null).
   * @param startDate The start date (can be null).
   * @param dueDate The due date (can be null).
   * @return The updated Student domain object (may contain errors).
   */
  public static Student processUpdateInput(
      Student existingStudent,
      String regString,
      Campi campus,
      UUID courseId,
      BigDecimal requiredHours,
      BigDecimal completedHours,
      LocalDate startDate,
      LocalDate dueDate) {

    Student updated = existingStudent;

    if (StringUtils.isNotEmpty(regString)) {
      AcademicRegistration newReg = AcademicRegistration.factory(regString);
      updated = updated.changeAcademicRegistration(newReg);
    }

    if (campus != null) {
      updated = updated.changeCampus(campus);
    }

    if (courseId != null) {
      updated = updated.changeCourse(courseId);
    }

    // Logic for partial updates on composite VOs (Hours and Period):
    // We reconstruct the VO using existing values if the new value is null to support partial
    // updates
    boolean hoursChanged = requiredHours != null || completedHours != null;
    if (hoursChanged) {
      BigDecimal newReq =
          requiredHours != null
              ? requiredHours
              : existingStudent.getCounterpartHours().getRequiredHours();
      BigDecimal newComp =
          completedHours != null
              ? completedHours
              : existingStudent.getCounterpartHours().getCompletedHours();
      CounterpartHours newHours = CounterpartHours.factory(newReq, newComp);
      updated = updated.changeCounterpartHours(newHours);
    }

    boolean periodChanged = startDate != null || dueDate != null;
    if (periodChanged) {
      LocalDate newStart =
          startDate != null ? startDate : existingStudent.getPeriod().getStartDate();
      LocalDate newDue = dueDate != null ? dueDate : existingStudent.getPeriod().getDueDate();
      Period newPeriod = Period.factory(newStart, newDue);
      updated = updated.changePeriod(newPeriod);
    }

    return updated;
  }
}
