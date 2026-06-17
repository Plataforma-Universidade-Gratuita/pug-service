package br.org.catolicasc.pug.helpers;

import br.org.catolicasc.pug.academic.domain.AreaOfExpertise;
import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.academic.domain.FormerStudent;
import br.org.catolicasc.pug.academic.infra.persistence.impl.AreaOfExpertiseRepositoryImpl;
import br.org.catolicasc.pug.academic.infra.persistence.impl.CourseRepositoryImpl;
import br.org.catolicasc.pug.academic.infra.persistence.impl.FormerStudentRepositoryImpl;
import br.org.catolicasc.pug.geo.domain.City;
import br.org.catolicasc.pug.geo.infra.CityMapper;
import br.org.catolicasc.pug.geo.infra.persistence.impl.CityRepositoryImpl;
import br.org.catolicasc.pug.helpers.builders.domain.AccountBuilder;
import br.org.catolicasc.pug.helpers.builders.domain.AdminBuilder;
import br.org.catolicasc.pug.helpers.builders.domain.AreaOfExpertiseBuilder;
import br.org.catolicasc.pug.helpers.builders.domain.AttendanceBuilder;
import br.org.catolicasc.pug.helpers.builders.domain.CourseBuilder;
import br.org.catolicasc.pug.helpers.builders.domain.EntityBuilder;
import br.org.catolicasc.pug.helpers.builders.domain.FormerStudentBuilder;
import br.org.catolicasc.pug.helpers.builders.domain.ProjectBuilder;
import br.org.catolicasc.pug.helpers.builders.domain.StaffBuilder;
import br.org.catolicasc.pug.helpers.builders.domain.UserBuilder;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.Admin;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.identity.infra.persistence.impl.AccountRepositoryImpl;
import br.org.catolicasc.pug.identity.infra.persistence.impl.AdminRepositoryImpl;
import br.org.catolicasc.pug.identity.infra.persistence.impl.UserRepositoryImpl;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.partner.domain.Staff;
import br.org.catolicasc.pug.partner.infra.persistence.impl.EntityRepositoryImpl;
import br.org.catolicasc.pug.partner.infra.persistence.impl.StaffRepositoryImpl;
import br.org.catolicasc.pug.project.domain.Attendance;
import br.org.catolicasc.pug.project.domain.Enrollment;
import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.project.domain.ProjectAreaOfExpertise;
import br.org.catolicasc.pug.project.infra.persistence.impl.AttendanceRepositoryImpl;
import br.org.catolicasc.pug.project.infra.persistence.impl.EnrollmentRepositoryImpl;
import br.org.catolicasc.pug.project.infra.persistence.impl.ProjectAreaOfExpertiseRepositoryImpl;
import br.org.catolicasc.pug.project.infra.persistence.impl.ProjectRepositoryImpl;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Factory class responsible for centralizing the creation and persistence of test data.
 *
 * <p>This helper ensures that entity graphs are persisted in the correct logical order, respecting
 * database foreign key constraints and simplifying the setup of integration tests.
 */
@ApplicationScoped
public class TestDataFactory {

  @Inject UserRepositoryImpl userRepository;
  @Inject AccountRepositoryImpl accountRepository;
  @Inject AreaOfExpertiseRepositoryImpl areaOfExpertiseRepository;
  @Inject CourseRepositoryImpl courseRepository;
  @Inject FormerStudentRepositoryImpl studentRepository;
  @Inject EntityRepositoryImpl entityRepository;
  @Inject StaffRepositoryImpl staffRepository;
  @Inject ProjectRepositoryImpl projectRepository;
  @Inject CityRepositoryImpl cityRepository;
  @Inject AdminRepositoryImpl adminRepository;
  @Inject EnrollmentRepositoryImpl enrollmentRepository;
  @Inject AttendanceRepositoryImpl attendanceRepository;
  @Inject ProjectAreaOfExpertiseRepositoryImpl projectAreaOfExpertiseRepository;

  public User createUser() {
    return userRepository.persist(UserBuilder.aUser().build());
  }

  public User createUser(User user) {
    return userRepository.persist(user);
  }

  public AreaOfExpertise createAreaOfExpertise() {
    return areaOfExpertiseRepository.persist(AreaOfExpertiseBuilder.aAreaOfExpertise().build());
  }

  public AreaOfExpertise createAreaOfExpertise(AreaOfExpertise areaOfExpertise) {
    return areaOfExpertiseRepository.persist(areaOfExpertise);
  }

  public City getAnyCity() {
    return CityMapper.toDomain(cityRepository.findAll().firstResult());
  }

  public Account createAccount(User user, AccountType type) {
    return accountRepository.persist(
        AccountBuilder.anAccount().forUser(user.getId()).withType(type).build());
  }

  public Account createAccount(Account account) {
    return accountRepository.persist(account);
  }

  public Course createCourse(AreaOfExpertise areaOfExpertise) {
    return courseRepository.persist(
        CourseBuilder.aCourse().withAreaOfExpertise(areaOfExpertise.getId()).build());
  }

  public Course createCourse(Course course) {
    return courseRepository.persist(course);
  }

  public Entity createEntity(City city) {
    return entityRepository.persist(EntityBuilder.anEntity().withCity(city.getId()).build());
  }

  public Entity createEntity(Entity entity) {
    return entityRepository.persist(entity);
  }

  public Admin createAdmin(Account account) {
    return adminRepository.persist(AdminBuilder.anAdmin().forAccount(account.getId()).build());
  }

  public FormerStudent createStudent(Account account, Course course) {
    return studentRepository.persist(
        FormerStudentBuilder.aStudent()
            .withAccountId(account.getId())
            .withCourse(course.getId())
            .build());
  }

  public Staff createStaff(Account account, Entity entity) {
    return staffRepository.persist(
        StaffBuilder.aStaff().forAccount(account.getId()).forEntity(entity.getId()).build());
  }

  public Project createProject(Entity entity, Account creator) {
    return projectRepository.persist(
        ProjectBuilder.aProject().withEntity(entity.getId()).withCreator(creator.getId()).build());
  }

  public void updateProject(Project project) {
    projectRepository.update(project);
  }

  public Enrollment createEnrollment(FormerStudent formerStudent, Project project) {
    return enrollmentRepository.persist(Enrollment.factory(formerStudent, project));
  }

  public Enrollment createApprovedEnrollment(FormerStudent formerStudent, Project project) {
    return enrollmentRepository.persist(Enrollment.factory(formerStudent, project).approve());
  }

  public ProjectAreaOfExpertise createProjectAreaOfExpertise(
      Project project, AreaOfExpertise areaOfExpertise) {
    return projectAreaOfExpertiseRepository.persist(
        ProjectAreaOfExpertise.factory(project.getId(), areaOfExpertise.getId()));
  }

  public Attendance createAttendance(Project project, FormerStudent formerStudent) {
    return attendanceRepository.persist(
        AttendanceBuilder.anAttendance().withProject(project).withStudent(formerStudent).build());
  }
}
