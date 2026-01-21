package com.pug.academic.service.impl;

import com.pug.academic.domain.Course;
import com.pug.academic.domain.ISchoolRepository;
import com.pug.academic.domain.School;
import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.academic.service.ICourseService;
import com.pug.academic.service.ISchoolService;
import com.pug.academic.service.dtos.SchoolCreateCommand;
import com.pug.academic.service.dtos.SchoolUpdateCommand;
import com.pug.shared.domain.enums.DeleteKeys;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.CollectionUtils;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.jboss.logging.Logger;

/** Service class for managing School entities. */
@ApplicationScoped
public class SchoolService implements ISchoolService {

  private static final Logger LOG = Logger.getLogger(SchoolService.class);

  @Inject ISchoolRepository repo;
  @Inject ICourseService courseService;

  /**
   * Helper method to process DTO input and build School domain object (or update existing),
   * collecting all validation problems.
   *
   * @param name The name string from DTO.
   * @param existingSchool Optional existing school for updates (null for creation).
   * @param problems List to collect AppValidationException.Problem instances.
   * @return The constructed or updated School domain object if no problems, or null if problems
   *     occurred.
   */
  private School processSchoolInput(
      String name, School existingSchool, List<AppValidationException.Problem> problems) {

    School resultSchool = null;
    try {
      if (existingSchool == null) {
        resultSchool = School.createNew(name);
      } else {
        String effectiveName = (name != null) ? name : existingSchool.getName();
        resultSchool = existingSchool.changeName(effectiveName);
      }
    } catch (AppValidationException e) {
      problems.addAll(e.getProblems());
    }
    return resultSchool;
  }

  @Transactional
  @Override
  public School save(SchoolCreateCommand cmd) {
    List<AppValidationException.Problem> problems = new ArrayList<>();

    School schoolToPersist = processSchoolInput(cmd.name(), null, problems);

    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }

    if (existsByName(schoolToPersist.getName())) {
      throw new DuplicateResourceException(
          AcademicErrorCodes.SCHOOL_ALREADY_EXISTS, Map.of("name", schoolToPersist.getName()));
    }
    return repo.persist(schoolToPersist);
  }

  @Transactional
  @Override
  public List<School> saveAll(Iterable<SchoolCreateCommand> cmds) {
    if (CollectionUtils.isEmpty(cmds)) {
      return List.of();
    }

    List<AppValidationException.Problem> allCollectedProblems = new ArrayList<>();
    List<School> schoolsToPersist = new ArrayList<>();
    Set<String> processedNames = new HashSet<>();

    for (SchoolCreateCommand cmd : cmds) {
      List<AppValidationException.Problem> currentSchoolProblems = new ArrayList<>();
      School school = processSchoolInput(cmd.name(), null, currentSchoolProblems);

      if (!currentSchoolProblems.isEmpty()) {
        allCollectedProblems.addAll(currentSchoolProblems);
      } else {
        String schoolName = school.getName();
        if (!processedNames.add(schoolName)) {
          allCollectedProblems.add(
              new AppValidationException.Problem(AcademicErrorCodes.SCHOOL_ALREADY_EXISTS, "name"));
        }
        schoolsToPersist.add(school);
      }
    }

    if (!allCollectedProblems.isEmpty()) {
      throw new AppValidationException(allCollectedProblems);
    }

    List<String> namesToPersist = schoolsToPersist.stream().map(School::getName).toList();

    if (repo.existsAnyByNameIn(namesToPersist)) {
      throw new DuplicateResourceException(AcademicErrorCodes.SCHOOL_ALREADY_EXISTS);
    }

    return repo.persistAll(schoolsToPersist);
  }

  @Transactional
  @Override
  public School update(UUID id, SchoolUpdateCommand cmd) {
    School current = getById(id);

    List<AppValidationException.Problem> problems = new ArrayList<>();

    School schoolToUpdate = processSchoolInput(cmd.name(), current, problems);

    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }

    if (cmd.name() != null && !cmd.name().equals(current.getName())) {
      if (existsByName(cmd.name())) {
        throw new DuplicateResourceException(
            AcademicErrorCodes.SCHOOL_ALREADY_EXISTS, Map.of("name", cmd.name()));
      }
    }

    repo.update(schoolToUpdate);
    return getById(id);
  }

  @Transactional
  @Override
  public Map<DeleteKeys, Long> deleteAll(Iterable<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return Map.of(
          DeleteKeys.SCHOOLS, 0L,
          DeleteKeys.COURSES, 0L,
          DeleteKeys.STUDENTS, 0L,
          DeleteKeys.ACCOUNTS, 0L,
          DeleteKeys.USERS, 0L);
    }

    Set<UUID> courseIdsToDelete = new HashSet<>();
    for (UUID schoolId : ids) {
      courseIdsToDelete.addAll(
          courseService.listAllBySchoolId(schoolId).stream()
              .map(Course::getId)
              .collect(Collectors.toSet()));
    }

    Map<DeleteKeys, Long> deletedCoursesAndDependents = courseService.deleteAll(courseIdsToDelete);

    long schoolsDeleted = repo.deleteByIds(ids);

    return Map.of(
        DeleteKeys.SCHOOLS, schoolsDeleted,
        DeleteKeys.COURSES, deletedCoursesAndDependents.getOrDefault(DeleteKeys.COURSES, 0L),
        DeleteKeys.STUDENTS, deletedCoursesAndDependents.getOrDefault(DeleteKeys.STUDENTS, 0L),
        DeleteKeys.ACCOUNTS, deletedCoursesAndDependents.getOrDefault(DeleteKeys.ACCOUNTS, 0L),
        DeleteKeys.USERS, deletedCoursesAndDependents.getOrDefault(DeleteKeys.USERS, 0L));
  }

  @Override
  public List<School> listAll() {
    try {
      return repo.listAllSchools();
    } catch (AppValidationException e) {
      LOG.errorf(
          e,
          "Data integrity error: Corrupted School entity found in DB. Problems: %s",
          e.getProblems().stream()
              .map(
                  p ->
                      p.code().getBundleKey()
                          + (p.fieldName() != null ? "(" + p.fieldName() + ")" : ""))
              .collect(Collectors.joining(", ")));
      throw new ResourceNotFoundException(AcademicErrorCodes.SCHOOL_NOT_FOUND);
    }
  }

  @Override
  public School getById(UUID id) {
    try {
      return repo.findOptionalById(id)
          .orElseThrow(
              () ->
                  new ResourceNotFoundException(
                      AcademicErrorCodes.SCHOOL_NOT_FOUND, Map.of("id", id)));
    } catch (AppValidationException e) {
      LOG.errorf(
          e,
          "Data integrity error: School with ID %s in DB violates domain rules. Problems: %s",
          id,
          e.getProblems().stream()
              .map(
                  p ->
                      p.code().getBundleKey()
                          + (p.fieldName() != null ? "(" + p.fieldName() + ")" : ""))
              .collect(Collectors.joining(", ")));
      throw new ResourceNotFoundException(AcademicErrorCodes.SCHOOL_NOT_FOUND, Map.of("id", id));
    }
  }

  @Override
  public School getByName(String name) {
    String n = StringUtils.trim(name);
    try {
      return repo.findOptionalByName(n)
          .orElseThrow(
              () ->
                  new ResourceNotFoundException(
                      AcademicErrorCodes.SCHOOL_NOT_FOUND, Map.of("name", n)));
    } catch (AppValidationException e) {
      LOG.errorf(
          e,
          "Data integrity error: School with name %s in DB violates domain rules. Problems: %s",
          name,
          e.getProblems().stream()
              .map(
                  p ->
                      p.code().getBundleKey()
                          + (p.fieldName() != null ? "(" + p.fieldName() + ")" : ""))
              .collect(Collectors.joining(", ")));
      throw new ResourceNotFoundException(AcademicErrorCodes.SCHOOL_NOT_FOUND, Map.of("name", n));
    }
  }

  @Override
  public boolean existsByName(String name) {
    if (StringUtils.isEmpty(name)) {
      return false;
    }
    return repo.existsByName(name);
  }

  @Override
  public boolean existsAnyByNameIn(Iterable<String> names) {
    return repo.existsAnyByNameIn(names);
  }
}
