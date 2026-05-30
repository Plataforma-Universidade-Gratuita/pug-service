package br.org.catolicasc.pug.academic.service.utils;

import br.org.catolicasc.pug.academic.domain.FormerStudent;
import br.org.catolicasc.pug.academic.domain.vos.AcademicRegistration;
import br.org.catolicasc.pug.academic.domain.vos.CounterpartHours;
import br.org.catolicasc.pug.academic.domain.vos.Period;
import br.org.catolicasc.pug.academic.service.dtos.formerstudents.FormerStudentCreateCommand;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Stateless utility class responsible for mapping raw DTO command data into pure {@link FormerStudent}
 * Domain Aggregates and their complex nested Value Objects.
 *
 * <p>This processor centralizes the orchestration of domain factory methods and state-mutation
 * behaviors, ensuring that complex initialization or update logic does not pollute the application
 * service layer.
 */
public class FormerStudentProcessor {

  /**
   * Processes a bulk list of formerStudent creation commands, combining them with their newly generated
   * account identifiers to instantiate pure Domain Aggregates.
   *
   * @param cmds the {@link List} of bulk formerStudent creation commands
   * @param accountIds the {@link List} of newly provisioned {@link UUID} account identifiers
   * @return a {@link List} of instantiated and validated {@link FormerStudent} aggregates
   * @throws IllegalArgumentException if the size of the commands list does not match the account
   *     IDs
   * @throws AppValidationException if any aggregate contains domain validation errors
   */
  public static List<FormerStudent> processBulkCreateInput(
      List<FormerStudentCreateCommand> cmds, List<UUID> accountIds) {

    if (cmds.size() != accountIds.size()) {
      throw new IllegalArgumentException(
          "Commands and Account IDs lists must be of the same size.");
    }

    List<FormerStudent> students = new ArrayList<>(cmds.size());

    for (int i = 0; i < cmds.size(); i++) {
      FormerStudentCreateCommand cmd = cmds.get(i);
      UUID linkedAccountId = accountIds.get(i);

      FormerStudent formerStudent =
          processCreateInput(
              linkedAccountId,
              cmd.academicRegistration(),
              cmd.campus(),
              cmd.courseId(),
              cmd.requiredHours(),
              cmd.startDate(),
              cmd.dueDate());

      if (formerStudent.hasFieldErrors()) {
        throw new AppValidationException(formerStudent.getFieldErrors());
      }
      students.add(formerStudent);
    }

    return students;
  }

  /**
   * Processes raw creation inputs and constructs a new {@link FormerStudent} domain aggregate.
   *
   * <p>New students are initialized with {@code completedHours} as zero.
   *
   * @param accountId the unique identifier of the linked authentication account
   * @param regString the raw academic registration string
   * @param campus the designated university campus enum
   * @param courseId the unique identifier of the enrolled course
   * @param requiredHours the quantified hours the formerStudent must complete
   * @param startDate the start date of the enrollment period
   * @param dueDate the due date of the enrollment period
   * @return a fully instantiated {@link FormerStudent} domain aggregate
   */
  public static FormerStudent processCreateInput(
      UUID accountId,
      String regString,
      Campi campus,
      UUID courseId,
      BigDecimal requiredHours,
      LocalDate startDate,
      LocalDate dueDate) {

    AcademicRegistration regVo = AcademicRegistration.factory(regString);
    // Inicializa com 0 horas completadas
    CounterpartHours hoursVo = CounterpartHours.factory(requiredHours, BigDecimal.ZERO, false);
    Period periodVo = Period.factory(startDate, dueDate);

    return FormerStudent.factory(accountId, regVo, campus, courseId, hoursVo, periodVo);
  }

  /**
   * Processes raw update inputs and conditionally mutates the state of an existing {@link FormerStudent}.
   *
   * @param existingStudent the current, reconstituted {@link FormerStudent} aggregate
   * @param regString the proposed new academic registration, or {@code null}/empty to skip
   * @param campus the proposed new campus, or {@code null} to skip
   * @param courseId the proposed new course ID, or {@code null} to skip
   * @param requiredHours the proposed new required hours, or {@code null} to skip
   * @param startDate the proposed new start date, or {@code null} to skip
   * @param dueDate the proposed new due date, or {@code null} to skip
   * @return a new {@link FormerStudent} domain aggregate reflecting the requested updates
   */
  public static FormerStudent processUpdateInput(
      FormerStudent existingStudent,
      String regString,
      Campi campus,
      UUID courseId,
      BigDecimal requiredHours,
      LocalDate startDate,
      LocalDate dueDate) {

    FormerStudent updated = existingStudent;

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
          CounterpartHours.factory(
              requiredHours,
              updated.getCounterpartHours().getCompletedHours(),
              updated.getCounterpartHours().getConcluded());
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

