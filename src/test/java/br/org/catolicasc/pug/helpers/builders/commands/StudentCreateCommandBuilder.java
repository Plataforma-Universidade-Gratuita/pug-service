package br.org.catolicasc.pug.helpers.builders.commands;

import br.org.catolicasc.pug.academic.service.dtos.StudentCreateCommand;
import br.org.catolicasc.pug.helpers.TestBrazilianIdentifierGenerator;
import br.org.catolicasc.pug.helpers.TestNameGenerator;
import br.org.catolicasc.pug.identity.service.dtos.AccountCreateCommand;
import br.org.catolicasc.pug.identity.service.dtos.UserCreateCommand;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import com.github.f4b6a3.uuid.UuidCreator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;

/**
 * Builder class for creating {@link StudentCreateCommand} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with random defaults for all fields, including the nested {@link
 * AccountCreateCommand} and {@link UserCreateCommand}. Tests can override individual fields as
 * needed for specific scenarios.
 */
public class StudentCreateCommandBuilder {
  private String cpf = TestBrazilianIdentifierGenerator.generateValidCpf();
  private String name = TestNameGenerator.generateRandomName();
  private String email = TestNameGenerator.generateUniqueEmail("test.com");
  private String password = "test-password";
  private String academicRegistration = TestNameGenerator.generateUniqueRegistration();
  private Campi campus = getRandomCampus();
  private UUID courseId = UuidCreator.getTimeOrderedEpoch();
  private BigDecimal requiredHours = new BigDecimal("100");
  private LocalDate startDate = LocalDate.now();
  private LocalDate dueDate = LocalDate.now().plusMonths(6);

  private StudentCreateCommandBuilder() {}

  /**
   * Initializes a new builder with random defaults.
   *
   * @return a new {@link StudentCreateCommandBuilder} instance
   */
  public static StudentCreateCommandBuilder aStudentCreateCommand() {
    return new StudentCreateCommandBuilder();
  }

  /**
   * Sets the CPF for the nested user command.
   *
   * @param cpf the 11-digit CPF string
   * @return this builder instance
   */
  public StudentCreateCommandBuilder withCpf(String cpf) {
    this.cpf = cpf;
    return this;
  }

  /**
   * Sets the name for the nested user command.
   *
   * @param name the full name of the student
   * @return this builder instance
   */
  public StudentCreateCommandBuilder withName(String name) {
    this.name = name;
    return this;
  }

  /**
   * Sets the email for the nested account command.
   *
   * @param email the email address
   * @return this builder instance
   */
  public StudentCreateCommandBuilder withEmail(String email) {
    this.email = email;
    return this;
  }

  /**
   * Sets the password for the nested account command.
   *
   * @param password the password hash
   * @return this builder instance
   */
  public StudentCreateCommandBuilder withPassword(String password) {
    this.password = password;
    return this;
  }

  /**
   * Sets the academic registration string.
   *
   * @param academicRegistration the university-issued registration
   * @return this builder instance
   */
  public StudentCreateCommandBuilder withAcademicRegistration(String academicRegistration) {
    this.academicRegistration = academicRegistration;
    return this;
  }

  /**
   * Sets the campus assignment.
   *
   * @param campus the {@link Campi} enum value
   * @return this builder instance
   */
  public StudentCreateCommandBuilder withCampus(Campi campus) {
    this.campus = campus;
    return this;
  }

  /**
   * Sets the enrolled course identifier.
   *
   * @param courseId the UUID of the course
   * @return this builder instance
   */
  public StudentCreateCommandBuilder withCourseId(UUID courseId) {
    this.courseId = courseId;
    return this;
  }

  /**
   * Sets the required counterpart hours.
   *
   * @param requiredHours the total required hours
   * @return this builder instance
   */
  public StudentCreateCommandBuilder withRequiredHours(BigDecimal requiredHours) {
    this.requiredHours = requiredHours;
    return this;
  }

  /**
   * Sets the enrollment start date.
   *
   * @param startDate the start date
   * @return this builder instance
   */
  public StudentCreateCommandBuilder withStartDate(LocalDate startDate) {
    this.startDate = startDate;
    return this;
  }

  /**
   * Sets the enrollment due date.
   *
   * @param dueDate the due date
   * @return this builder instance
   */
  public StudentCreateCommandBuilder withDueDate(LocalDate dueDate) {
    this.dueDate = dueDate;
    return this;
  }

  /**
   * Constructs the {@link StudentCreateCommand} using the current builder state, composing the
   * nested {@link AccountCreateCommand} and {@link UserCreateCommand} internally.
   *
   * @return a configured {@link StudentCreateCommand} instance
   */
  public StudentCreateCommand build() {
    UserCreateCommand userCmd = new UserCreateCommand(cpf, name);
    AccountCreateCommand accCmd =
        new AccountCreateCommand(email, AccountType.STUDENT, password, userCmd);
    return new StudentCreateCommand(
        accCmd, academicRegistration, campus, courseId, requiredHours, startDate, dueDate);
  }

  private static Campi getRandomCampus() {
    Campi[] values = Campi.values();
    return values[new Random().nextInt(values.length)];
  }
}
