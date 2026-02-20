package com.pug.academic.service.impl;

import com.pug.academic.domain.Course;
import com.pug.academic.domain.School;
import com.pug.academic.domain.SchoolRepository;
import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.academic.service.CourseService;
import com.pug.academic.service.SchoolService;
import com.pug.academic.service.dtos.SchoolCreateCommand;
import com.pug.academic.service.dtos.SchoolUpdateCommand;
import com.pug.academic.service.utils.SchoolProcessor;
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
public class SchoolServiceImpl implements SchoolService {

  private static final Logger LOG = Logger.getLogger(SchoolServiceImpl.class);

  @Inject SchoolRepository repo;
  @Inject CourseService courseService;

  @Transactional
  @Override
  public School save(SchoolCreateCommand cmd) {
    School schoolToPersist = SchoolProcessor.processCreateInput(cmd.name());

    if (schoolToPersist.hasErrors()) {
      throw new AppValidationException(schoolToPersist.getProblems());
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

    List<Problem> allCollectedProblems = new ArrayList<>();
    List<School> schoolsToPersist = new ArrayList<>();
    Set<String> processedNames = new HashSet<>();

    for (SchoolCreateCommand cmd : cmds) {
      School school = SchoolProcessor.processCreateInput(cmd.name());

      if (school.hasErrors()) {
        allCollectedProblems.addAll(school.getProblems());
      } else {
        String schoolName = school.getName();
        if (!processedNames.add(schoolName)) {
          allCollectedProblems.add(
              new Problem(AcademicErrorCodes.SCHOOL_ALREADY_EXISTS));
        } else {
          schoolsToPersist.add(school);
        }
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

    School updatedSchool = SchoolProcessor.processUpdateInput(current, cmd.name());

    if (updatedSchool.hasErrors()) {
      throw new AppValidationException(updatedSchool.getProblems());
    }

    if (!updatedSchool.getName().equals(current.getName())
        && existsByName(updatedSchool.getName())) {
      throw new DuplicateResourceException(
          AcademicErrorCodes.SCHOOL_ALREADY_EXISTS, Map.of("name", updatedSchool.getName()));
    }

    repo.update(updatedSchool);
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
    List<School> schools = repo.listAllSchools();
    for (School s : schools) {
      if (s.hasErrors()) {
        LOG.errorf(
            "Data integrity error: Corrupted School entity found in DB. Problems: %s",
            s.getProblemsSummary());
        throw new ResourceNotFoundException(AcademicErrorCodes.SCHOOL_NOT_FOUND);
      }
    }
    return schools;
  }

  @Override
  public School getById(UUID id) {
    School school =
        repo.findOptionalById(id)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        AcademicErrorCodes.SCHOOL_NOT_FOUND, Map.of("id", id)));

    if (school.hasErrors()) {
      LOG.errorf(
          "Data integrity error: School with ID %s in DB violates domain rules. Problems: %s",
          id, school.getProblemsSummary());
      throw new ResourceNotFoundException(AcademicErrorCodes.SCHOOL_NOT_FOUND, Map.of("id", id));
    }
    return school;
  }

  @Override
  public School getByName(String name) {
    String n = StringUtils.trim(name);
    School school =
        repo.findOptionalByName(n)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        AcademicErrorCodes.SCHOOL_NOT_FOUND, Map.of("name", n)));

    if (school.hasErrors()) {
      LOG.errorf(
          "Data integrity error: School with name %s in DB violates domain rules. Problems: %s",
          n, school.getProblemsSummary());
      throw new ResourceNotFoundException(AcademicErrorCodes.SCHOOL_NOT_FOUND, Map.of("name", n));
    }
    return school;
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
