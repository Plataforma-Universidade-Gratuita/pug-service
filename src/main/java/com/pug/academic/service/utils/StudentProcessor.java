package com.pug.academic.service.utils;

import com.pug.academic.domain.Student;
import com.pug.academic.domain.vos.AcademicRegistration;
import com.pug.academic.domain.vos.CounterpartHours;
import com.pug.academic.domain.vos.Period;
import com.pug.shared.domain.enums.Campi;
import com.pug.shared.utils.StringUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Stateless utility class responsible for mapping raw DTO command data into pure {@link Student}
 * Domain Aggregates and their complex nested Value Objects.
 *
 * <p>This processor centralizes the orchestration of domain factory methods and state-mutation
 * behaviors, ensuring that complex initialization or update logic does not pollute the application
 * service layer.
 */
public class StudentProcessor {

  /**
   * Processes raw creation inputs and constructs a new {@link Student} domain aggregate.
   *
   * <p>This method translates the raw primitive representations into appropriate Value Objects
   * (e.g., {@link AcademicRegistration}, {@link CounterpartHours}, {@link Period}) before passing
   * them to the aggregate's factory method.
   *
   * <p><b>Note:</b> The returned {@link Student} object may contain accumulated domain validation
   * failures. The caller is responsible for checking {@link Student#hasFieldErrors()} and handling
   * them appropriately.
   *
   * @param accountId the unique identifier of the linked authentication account
   * @param regString the raw academic registration string
   * @param campus the designated university campus enum
   * @param courseId the unique identifier of the enrolled course
   * @param requiredHours the quantified hours the student must complete
   * @param startDate the start date of the enrollment period
   * @param dueDate the due date of the enrollment period
   * @return a fully instantiated {@link Student} domain aggregate, potentially containing
   *     validation errors
   */
  public static Student processCreateInput(
      UUID accountId,
      String regString,
      Campi campus,
      UUID courseId,
      BigDecimal requiredHours,
      LocalDate startDate,
      LocalDate dueDate) {

    AcademicRegistration regVo = AcademicRegistration.factory(regString);
    CounterpartHours hoursVo = CounterpartHours.factory(requiredHours, null);
    Period periodVo = Period.factory(startDate, dueDate);

    return Student.factory(accountId, regVo, campus, courseId, hoursVo, periodVo);
  }

  /**
   * Processes raw update inputs and conditionally mutates the state of an existing {@link Student}.
   *
   * <p>This method applies partial updates. Only fields that are explicitly provided will trigger a
   * state mutation via the aggregate's domain behaviors. Because period dates rely on each other
   * for validation, they are safely merged with existing state before evaluation.
   *
   * @param existingStudent the current, reconstituted {@link Student} aggregate from the repository
   * @param regString the proposed new academic registration, or {@code null}/empty to skip updating
   * @param campus the proposed new campus, or {@code null} to skip updating
   * @param courseId the proposed new course ID, or {@code null} to skip updating
   * @param requiredHours the proposed new required hours, or {@code null} to skip updating
   * @param startDate the proposed new start date, or {@code null} to skip updating
   * @param dueDate the proposed new due date, or {@code null} to skip updating
   * @return a new {@link Student} domain aggregate reflecting the requested updates, potentially
   *     containing validation errors
   */
  public static Student processUpdateInput(
      Student existingStudent,
      String regString,
      Campi campus,
      UUID courseId,
      BigDecimal requiredHours,
      LocalDate startDate,
      LocalDate dueDate) {

    Student updated = existingStudent;

    if (StringUtils.isNotEmpty(regString)) {
      AcademicRegistration newReg = AcademicRegistration.factory(regString);
      updated = updated.changeAcademicRegistration(newReg);
    }

    if (campus != null) {
      updated = updated.moveToCampus(campus);
    }

    if (courseId != null) {
      updated = updated.changeCourse(courseId);
    }

    if (requiredHours != null) {
      CounterpartHours newHours =
          CounterpartHours.factory(requiredHours, updated.getCounterpartHours().getConcluded());
      updated = updated.updateRequiredHours(newHours);
    }

    boolean periodChanged = startDate != null || dueDate != null;
    if (periodChanged) {
      LocalDate newStart =
          startDate != null ? startDate : existingStudent.getPeriod().getStartDate();
      LocalDate newDue = dueDate != null ? dueDate : existingStudent.getPeriod().getDueDate();
      Period newPeriod = Period.factory(newStart, newDue);
      updated = updated.updateDateWindow(newPeriod);
    }

    return updated;
  }
}
