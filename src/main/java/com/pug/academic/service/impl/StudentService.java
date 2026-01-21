package com.pug.academic.service.impl;

import com.pug.academic.domain.IStudentRepository;
import com.pug.academic.domain.Student;
import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.academic.domain.enums.Campi;
import com.pug.academic.domain.vos.AcademicRegistration;
import com.pug.academic.domain.vos.CounterpartHours;
import com.pug.academic.domain.vos.Period;
import com.pug.academic.service.ICourseService;
import com.pug.academic.service.IStudentService;
import com.pug.academic.service.dtos.StudentCreateCommand;
import com.pug.academic.service.dtos.StudentUpdateCommand;
import com.pug.identity.domain.Account;
import com.pug.identity.service.AccountService;
import com.pug.shared.domain.enums.DeleteKeys;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.CollectionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for managing Student entities.
 */
@ApplicationScoped
public class StudentService implements IStudentService {

  private static final Logger LOG = Logger.getLogger(StudentService.class);

  @Inject
  IStudentRepository repo;
  @Inject
  AccountService accountService;
  @Inject
  ICourseService courseService;

  /**
   * Helper method to process DTO input and build Student domain object (or update existing),
   * collecting all validation problems.
   *
   * @param accountId                  The account ID associated with the student.
   * @param academicRegistrationString The academic registration string from DTO.
   * @param campus                     The campus from DTO.
   * @param courseId                   The course ID from DTO.
   * @param requiredHours              The required hours from DTO.
   * @param completedHours             The completed hours from DTO.
   * @param startDate                  The start date from DTO.
   * @param dueDate                    The due date from DTO.
   * @param existingStudent            Optional existing student for updates (null for creation).
   * @param problems                   List to collect AppValidationException.Problem instances.
   * @return The constructed or updated Student domain object if no problems, or null if problems occurred.
   */
  private Student processStudentInput(
          UUID accountId,
          String academicRegistrationString,
          Campi campus,
          UUID courseId,
          BigDecimal requiredHours,
          BigDecimal completedHours,
          LocalDate startDate,
          LocalDate dueDate,
          Student existingStudent,
          List<AppValidationException.Problem> problems) {

    AcademicRegistration academicRegistrationVO = null;
    CounterpartHours counterpartHoursVO = null;
    Period periodVO = null;

    try {
      if (academicRegistrationString != null && !academicRegistrationString.isBlank()) {
        academicRegistrationVO = new AcademicRegistration(academicRegistrationString);
      }
    } catch (AppValidationException e) {
      problems.addAll(e.getProblems());
    }

    try {
      if (requiredHours != null && completedHours != null) {
        counterpartHoursVO = new CounterpartHours(requiredHours, completedHours);
      }
    } catch (AppValidationException e) {
      problems.addAll(e.getProblems());
    }

    try {
      if (startDate != null && dueDate != null) {
        periodVO = new Period(startDate, dueDate);
      }
    } catch (AppValidationException e) {
      problems.addAll(e.getProblems());
    }

    Student resultStudent = null;
    try {
      if (existingStudent == null) {
        resultStudent =
                Student.createNew(
                        accountId, academicRegistrationVO, campus, courseId, counterpartHoursVO, periodVO);
      } else {
        AcademicRegistration effectiveAcademicRegistration =
                (academicRegistrationVO != null) ? academicRegistrationVO : existingStudent.getAcademicRegistration();
        Campi effectiveCampus = (campus != null) ? campus : existingStudent.getCampus();
        UUID effectiveCourseId = (courseId != null) ? courseId : existingStudent.getCourseId();
        CounterpartHours effectiveCounterpartHours =
                (counterpartHoursVO != null) ? counterpartHoursVO : existingStudent.getCounterpartHours();
        Period effectivePeriod = (periodVO != null) ? periodVO : existingStudent.getPeriod();

        Student tempStudent = existingStudent;

        if (academicRegistrationVO != null && !effectiveAcademicRegistration.equals(tempStudent.getAcademicRegistration())) {
          tempStudent = tempStudent.changeAcademicRegistration(effectiveAcademicRegistration);
        }
        if (campus != null && !effectiveCampus.equals(tempStudent.getCampus())) {
          tempStudent = tempStudent.changeCampus(effectiveCampus);
        }
        if (courseId != null && !effectiveCourseId.equals(tempStudent.getCourseId())) {
          tempStudent = tempStudent.changeCourse(effectiveCourseId);
        }
        if (counterpartHoursVO != null && !effectiveCounterpartHours.equals(tempStudent.getCounterpartHours())) {
          tempStudent = tempStudent.changeCounterpartHours(effectiveCounterpartHours);
        }
        if (periodVO != null && !effectivePeriod.equals(tempStudent.getPeriod())) {
          tempStudent = tempStudent.changePeriod(effectivePeriod);
        }
        resultStudent = tempStudent;
      }
    } catch (AppValidationException e) {
      problems.addAll(e.getProblems());
    }
    return resultStudent;
  }

  @Transactional
  @Override
  public Student save(StudentCreateCommand cmd) {
    List<AppValidationException.Problem> problems = new ArrayList<>();
    Account account = null;

    courseService.getById(cmd.courseId());

    try {
      account = accountService.save(cmd.accountCreateCommand());
    } catch (AppValidationException e) {
      problems.addAll(e.getProblems());
    } catch (DuplicateResourceException e) {
      problems.add(new AppValidationException.Problem(e.getErrorCode(), "accountCreateCommand.emailString"));
    }

    Student studentToPersist = null;
    if (problems.isEmpty() && account != null) {
      studentToPersist =
              processStudentInput(
                      account.getId(),
                      cmd.academicRegistration(),
                      cmd.campus(),
                      cmd.courseId(),
                      cmd.requiredHours(),
                      cmd.completedHours(),
                      cmd.startDate(),
                      cmd.dueDate(),
                      null,
                      problems);
    } else {
      studentToPersist =
              processStudentInput(
                      null,
                      cmd.academicRegistration(),
                      cmd.campus(),
                      cmd.courseId(),
                      cmd.requiredHours(),
                      cmd.completedHours(),
                      cmd.startDate(),
                      cmd.dueDate(),
                      null,
                      problems);
    }


    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }

    if (existsByRegistration(studentToPersist.getAcademicRegistration().toString())) {
      throw new DuplicateResourceException(
              AcademicErrorCodes.STUDENT_ALREADY_EXISTS,
              Map.of("academicRegistration", studentToPersist.getAcademicRegistration().toString()));
    }
    return repo.persist(studentToPersist);
  }

  @Transactional
  @Override
  public List<Student> saveAll(Iterable<StudentCreateCommand> cmds) {
    if (CollectionUtils.isEmpty(cmds)) {
      return List.of();
    }

    List<AppValidationException.Problem> allCollectedProblems = new ArrayList<>();
    List<Student> studentsToPersist = new ArrayList<>();
    Set<String> processedRegistrations = new HashSet<>();
    Set<UUID> uniqueCourseIds = new HashSet<>();

    CollectionUtils.toStream(cmds).forEach(cmd -> uniqueCourseIds.add(cmd.courseId()));

    for (UUID courseId : uniqueCourseIds) {
      try {
        courseService.getById(courseId);
      } catch (ResourceNotFoundException e) {
        allCollectedProblems.add(new AppValidationException.Problem(AcademicErrorCodes.INVALID_COURSE_BLANK, "courseId"));
      }
    }

    if (!allCollectedProblems.isEmpty()) {
      throw new AppValidationException(allCollectedProblems);
    }

    List<Account> createdAccounts = new ArrayList<>();
    Map<StudentCreateCommand, AppValidationException.Problem> accountCreationProblemsMap = new java.util.HashMap<>();
    try {
      var accountCreateCommands = CollectionUtils.toStream(cmds)
              .map(StudentCreateCommand::accountCreateCommand)
              .toList();
      createdAccounts = accountService.saveAll(accountCreateCommands);
    } catch (AppValidationException e) {
      for (AppValidationException.Problem problem : e.getProblems()) {
        accountCreationProblemsMap.put(cmds.iterator().next(), problem);
      }
      allCollectedProblems.addAll(e.getProblems());
    }

    Map<Integer, Account> accountByIndex = new java.util.HashMap<>();
    for (int i = 0; i < createdAccounts.size(); i++) {
      accountByIndex.put(i, createdAccounts.get(i));
    }


    int cmdIndex = 0;
    for (StudentCreateCommand cmd : cmds) {
      List<AppValidationException.Problem> currentStudentProblems = new ArrayList<>();

      UUID studentAccountId = null;
      if (accountByIndex.containsKey(cmdIndex)) {
        studentAccountId = accountByIndex.get(cmdIndex).getId();
      } else if (accountCreationProblemsMap.containsKey(cmd)) {
        allCollectedProblems.add(accountCreationProblemsMap.get(cmd));
      } else {
        allCollectedProblems.add(new AppValidationException.Problem(AcademicErrorCodes.INVALID_STUDENT_ACCOUNT_BLANK, "accountCreateCommand"));
      }

      Student student =
              processStudentInput(
                      studentAccountId,
                      cmd.academicRegistration(),
                      cmd.campus(),
                      cmd.courseId(),
                      cmd.requiredHours(),
                      cmd.completedHours(),
                      cmd.startDate(),
                      cmd.dueDate(),
                      null,
                      currentStudentProblems);

      if (!currentStudentProblems.isEmpty()) {
        allCollectedProblems.addAll(currentStudentProblems);
      } else if (student != null) {
        String registration = student.getAcademicRegistration().toString();
        if (!processedRegistrations.add(registration)) {
          allCollectedProblems.add(
                  new AppValidationException.Problem(
                          AcademicErrorCodes.STUDENT_ALREADY_EXISTS, "academicRegistration"));
        }
        studentsToPersist.add(student);
      }
      cmdIndex++;
    }

    if (!allCollectedProblems.isEmpty()) {
      throw new AppValidationException(allCollectedProblems);
    }

    List<String> registrationsToPersist = studentsToPersist.stream()
            .map(s -> s.getAcademicRegistration().toString())
            .toList();

    if (repo.existsAnyByRegistrationIn(registrationsToPersist)) {
      throw new DuplicateResourceException(AcademicErrorCodes.STUDENT_ALREADY_EXISTS);
    }

    return repo.persistAll(studentsToPersist);
  }

  @Transactional
  @Override
  public Student update(UUID accountId, StudentUpdateCommand cmd) {
    Student current = getById(accountId);

    List<AppValidationException.Problem> problems = new ArrayList<>();

    if (cmd.accountUpdateCommand() != null) {
      try {
        accountService.update(accountId, cmd.accountUpdateCommand());
      } catch (AppValidationException e) {
        problems.addAll(e.getProblems());
      } catch (DuplicateResourceException e) {
        problems.add(new AppValidationException.Problem(e.getErrorCode(), "accountUpdateCommand.emailString"));
      }
    }

    if (cmd.courseId() != null && !cmd.courseId().equals(current.getCourseId())) {
      courseService.getById(cmd.courseId());
    }

    Student studentToUpdate =
            processStudentInput(
                    current.getAccountId(),
                    cmd.academicRegistration(),
                    cmd.campus(),
                    cmd.courseId(),
                    cmd.requiredHours(),
                    cmd.completedHours(),
                    cmd.startDate(),
                    cmd.dueDate(),
                    current,
                    problems);

    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }

    if (cmd.academicRegistration() != null
            && !cmd.academicRegistration().equals(current.getAcademicRegistration().toString())) {
      if (existsByRegistration(cmd.academicRegistration())) {
        throw new DuplicateResourceException(
                AcademicErrorCodes.STUDENT_ALREADY_EXISTS,
                Map.of("academicRegistration", cmd.academicRegistration()));
      }
    }

    repo.update(studentToUpdate);
    return getById(accountId);
  }

  @Transactional
  @Override
  public Map<DeleteKeys, Long> deleteAll(Iterable<UUID> accountIds) {
    if (CollectionUtils.isEmpty(accountIds)) {
      return Map.of(
              DeleteKeys.STUDENTS, 0L,
              DeleteKeys.ACCOUNTS, 0L,
              DeleteKeys.USERS, 0L);
    }

    long studentsDeleted = repo.deleteByIds(accountIds);

    Map<DeleteKeys, Long> deletedAccountsAndUsers = accountService.deleteAll(accountIds);

    return Map.of(
            DeleteKeys.STUDENTS, studentsDeleted,
            DeleteKeys.ACCOUNTS, deletedAccountsAndUsers.getOrDefault(DeleteKeys.ACCOUNTS, 0L),
            DeleteKeys.USERS, deletedAccountsAndUsers.getOrDefault(DeleteKeys.USERS, 0L));
  }

  @Override
  public List<Student> listAll() {
    try {
      return repo.listAllStudents();
    } catch (AppValidationException e) {
      LOG.errorf(e, "Data integrity error: Corrupted Student entity found in DB. Problems: %s",
              e.getProblems().stream().map(p -> p.code().getBundleKey() + (p.fieldName() != null ? "(" + p.fieldName() + ")" : "")).collect(Collectors.joining(", ")));
      throw new ResourceNotFoundException(AcademicErrorCodes.STUDENT_NOT_FOUND);
    }
  }

  @Override
  public List<Student> listAllByCourseId(UUID courseId) {
    if (courseId == null) {
      return List.of();
    }
    try {
      return repo.listAllByCourseId(courseId);
    } catch (AppValidationException e) {
      LOG.errorf(e, "Data integrity error: Corrupted Student entity found in DB while listing by course ID %s. Problems: %s",
              courseId, e.getProblems().stream().map(p -> p.code().getBundleKey() + (p.fieldName() != null ? "(" + p.fieldName() + ")" : "")).collect(Collectors.joining(", ")));
      throw new ResourceNotFoundException(AcademicErrorCodes.STUDENT_NOT_FOUND); // Treat as not found if corrupted
    }
  }

  @Override
  public Student getById(UUID accountId) {
    try {
      return repo.findOptionalById(accountId)
              .orElseThrow(
                      () ->
                              new ResourceNotFoundException(
                                      AcademicErrorCodes.STUDENT_NOT_FOUND, Map.of("accountId", accountId)));
    } catch (AppValidationException e) {
      LOG.errorf(e, "Data integrity error: Student with Account ID %s in DB violates domain rules. Problems: %s",
              accountId, e.getProblems().stream().map(p -> p.code().getBundleKey() + (p.fieldName() != null ? "(" + p.fieldName() + ")" : "")).collect(Collectors.joining(", ")));
      throw new ResourceNotFoundException(AcademicErrorCodes.STUDENT_NOT_FOUND, Map.of("accountId", accountId));
    }
  }

  @Override
  public boolean existsAnyByAccountIdIn(Iterable<UUID> accountIds) {
    return repo.existsAnyByAccountIdIn(accountIds);
  }

  @Override
  public boolean existsAnyByRegistrationIn(Iterable<String> registrations) {
    return repo.existsAnyByRegistrationIn(registrations);
  }

  @Override
  public boolean existsByRegistration(String registration) {
    return repo.existsAnyByRegistrationIn(List.of(registration));
  }

  @Override
  public boolean existsAnyByCourseIdIn(Iterable<UUID> courseIds) {
    return repo.existsAnyByCourseIdIn(courseIds);
  }
}