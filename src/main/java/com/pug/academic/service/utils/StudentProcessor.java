package com.pug.academic.service.utils;

import com.pug.academic.domain.Student;
import com.pug.academic.domain.vos.AcademicRegistration;
import com.pug.academic.domain.vos.CounterpartHours;
import com.pug.academic.domain.vos.Period;
import com.pug.academic.service.dtos.StudentCreateCommand;
import com.pug.shared.domain.enums.Campi;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.utils.StringUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
   * Processes a bulk list of student creation commands, combining them with their newly generated
   * account identifiers to instantiate pure Domain Aggregates.
   *
   * <p>This method safely maps concurrent lists by index. It triggers the aggregate's internal
   * validations, and if any student violates domain rules, an exception is thrown to abort the
   * entire batch transaction.
   *
   * @param cmds the {@link List} of bulk student creation commands
   * @param accountIds the {@link List} of newly provisioned {@link UUID} account identifiers
   * @return a {@link List} of instantiated and validated {@link Student} aggregates
   * @throws IllegalArgumentException if the size of the commands list does not match the account
   *     IDs
   * @throws AppValidationException if any aggregate contains domain validation errors
   */
  public static List<Student> processBulkCreateInput(
      List<StudentCreateCommand> cmds, List<UUID> accountIds) {

    if (cmds.size() != accountIds.size()) {
      throw new IllegalArgumentException(
          "Commands and Account IDs lists must be of the same size.");
    }

    List<Student> students = new ArrayList<>(cmds.size());

    for (int i = 0; i < cmds.size(); i++) {
      StudentCreateCommand cmd = cmds.get(i);
      UUID linkedAccountId = accountIds.get(i);

      Student student =
          processCreateInput(
              linkedAccountId,
              cmd.academicRegistration(),
              cmd.campus(),
              cmd.courseId(),
              cmd.requiredHours(),
              cmd.startDate(),
              cmd.dueDate());

      if (student.hasFieldErrors()) {
        throw new AppValidationException(student.getFieldErrors());
      }
      students.add(student);
    }

    return students;
  }

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
