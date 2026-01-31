package com.pug.academic.service.impl;

import com.pug.academic.domain.Student;
import com.pug.academic.domain.StudentRepository;
import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.academic.service.CourseService;
import com.pug.academic.service.StudentService;
import com.pug.academic.service.dtos.StudentCreateCommand;
import com.pug.academic.service.dtos.StudentUpdateCommand;
import com.pug.academic.service.utils.StudentProcessor;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.jboss.logging.Logger;

/** Service class for managing Student entities. */
@ApplicationScoped
public class StudentServiceImpl implements StudentService {

  private static final Logger LOG = Logger.getLogger(StudentServiceImpl.class);

  @Inject StudentRepository repo;
  @Inject AccountService accountService;
  @Inject CourseService courseService;

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
      problems.add(new AppValidationException.Problem(e.getErrorCode()));
    }

    UUID accountId = (account != null) ? account.getId() : null;

    Student studentToPersist =
        StudentProcessor.processCreateInput(
            accountId,
            cmd.academicRegistration(),
            cmd.campus(),
            cmd.courseId(),
            cmd.requiredHours(),
            cmd.completedHours(),
            cmd.startDate(),
            cmd.dueDate());

    if (studentToPersist.hasErrors()) {
      problems.addAll(studentToPersist.getProblems());
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
        allCollectedProblems.add(
            new AppValidationException.Problem(AcademicErrorCodes.INVALID_COURSE_BLANK));
      }
    }

    if (!allCollectedProblems.isEmpty()) {
      throw new AppValidationException(allCollectedProblems);
    }

    List<Account> createdAccounts = new ArrayList<>();
    try {
      var accountCreateCommands =
          CollectionUtils.toStream(cmds).map(StudentCreateCommand::accountCreateCommand).toList();
      createdAccounts = accountService.saveAll(accountCreateCommands);
    } catch (AppValidationException e) {
      allCollectedProblems.addAll(e.getProblems());
    }

    if (!allCollectedProblems.isEmpty()) {
      throw new AppValidationException(allCollectedProblems);
    }

    Map<Integer, Account> accountByIndex = new HashMap<>();
    for (int i = 0; i < createdAccounts.size(); i++) {
      accountByIndex.put(i, createdAccounts.get(i));
    }

    int cmdIndex = 0;
    for (StudentCreateCommand cmd : cmds) {
      UUID studentAccountId = null;
      if (accountByIndex.containsKey(cmdIndex)) {
        studentAccountId = accountByIndex.get(cmdIndex).getId();
      }

      Student student =
          StudentProcessor.processCreateInput(
              studentAccountId,
              cmd.academicRegistration(),
              cmd.campus(),
              cmd.courseId(),
              cmd.requiredHours(),
              cmd.completedHours(),
              cmd.startDate(),
              cmd.dueDate());

      if (student.hasErrors()) {
        allCollectedProblems.addAll(student.getProblems());
      } else {
        String registration = student.getAcademicRegistration().toString();
        if (!processedRegistrations.add(registration)) {
          allCollectedProblems.add(
              new AppValidationException.Problem(AcademicErrorCodes.STUDENT_ALREADY_EXISTS));
        } else {
          studentsToPersist.add(student);
        }
      }
      cmdIndex++;
    }

    if (!allCollectedProblems.isEmpty()) {
      throw new AppValidationException(allCollectedProblems);
    }

    List<String> registrationsToPersist =
        studentsToPersist.stream().map(s -> s.getAcademicRegistration().toString()).toList();

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
        problems.add(new AppValidationException.Problem(e.getErrorCode()));
      }
    }

    if (cmd.courseId() != null && !cmd.courseId().equals(current.getCourseId())) {
      courseService.getById(cmd.courseId());
    }

    Student studentToUpdate =
        StudentProcessor.processUpdateInput(
            current,
            cmd.academicRegistration(),
            cmd.campus(),
            cmd.courseId(),
            cmd.requiredHours(),
            cmd.completedHours(),
            cmd.startDate(),
            cmd.dueDate());

    if (studentToUpdate.hasErrors()) {
      problems.addAll(studentToUpdate.getProblems());
    }

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
    List<Student> students = repo.listAllStudents();
    for (Student s : students) {
      if (s.hasErrors()) {
        LOG.errorf(
            "Data integrity error: Corrupted Student entity found in DB. Problems: %s",
            s.getProblemsSummary());
        throw new ResourceNotFoundException(AcademicErrorCodes.STUDENT_NOT_FOUND);
      }
    }
    return students;
  }

  @Override
  public List<Student> listAllByCourseId(UUID courseId) {
    if (courseId == null) {
      return List.of();
    }
    List<Student> students = repo.listAllByCourseId(courseId);
    for (Student s : students) {
      if (s.hasErrors()) {
        LOG.errorf(
            "Data integrity error: "
                + "Corrupted Student entity found in DB for Course %s. Problems: %s",
            courseId, s.getProblemsSummary());
        throw new ResourceNotFoundException(AcademicErrorCodes.STUDENT_NOT_FOUND);
      }
    }
    return students;
  }

  @Override
  public Student getById(UUID accountId) {
    Student student =
        repo.findOptionalById(accountId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        AcademicErrorCodes.STUDENT_NOT_FOUND, Map.of("accountId", accountId)));

    if (student.hasErrors()) {
      LOG.errorf(
          "Data integrity error: "
              + "Student with Account ID %s in DB violates domain rules. Problems: %s",
          accountId, student.getProblemsSummary());
      throw new ResourceNotFoundException(
          AcademicErrorCodes.STUDENT_NOT_FOUND, Map.of("accountId", accountId));
    }
    return student;
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
