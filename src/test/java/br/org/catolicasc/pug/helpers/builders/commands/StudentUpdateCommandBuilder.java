package br.org.catolicasc.pug.helpers.builders.commands;

import br.org.catolicasc.pug.academic.service.dtos.StudentUpdateCommand;
import br.org.catolicasc.pug.helpers.TestNameGenerator;
import br.org.catolicasc.pug.identity.service.dtos.AccountUpdateCommand;
import br.org.catolicasc.pug.identity.service.dtos.UserUpdateCommand;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;

/**
 * Builder class for creating {@link StudentUpdateCommand} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with random defaults. Since update commands treat {@code null} fields as
 * "no change", the default populates all fields to simulate a full update. Use {@code
 * withXxx(null)} to explicitly test partial update behavior.
 */
public class StudentUpdateCommandBuilder {
  private String name = TestNameGenerator.generateRandomName();
  private String email = "test-" + UUID.randomUUID().toString().substring(0, 8) + "@test.com";
  private String password = null;
  private String academicRegistration = null;
  private Campi campus = getRandomCampus();
  private UUID courseId = null;
  private BigDecimal requiredHours = null;
  private LocalDate startDate = null;
  private LocalDate dueDate = null;

  private StudentUpdateCommandBuilder() {}

  /**
   * Initializes a new builder with sensible defaults for a partial update.
   *
   * @return a new {@link StudentUpdateCommandBuilder} instance
   */
  public static StudentUpdateCommandBuilder aStudentUpdateCommand() {
    return new StudentUpdateCommandBuilder();
  }

  /**
   * Sets the name for the nested user update command.
   *
   * @param name the new name, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public StudentUpdateCommandBuilder withName(String name) {
    this.name = name;
    return this;
  }

  /**
   * Sets the email for the nested account update command.
   *
   * @param email the new email, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public StudentUpdateCommandBuilder withEmail(String email) {
    this.email = email;
    return this;
  }

  /**
   * Sets the password for the nested account update command.
   *
   * @param password the new password, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public StudentUpdateCommandBuilder withPassword(String password) {
    this.password = password;
    return this;
  }

  /**
   * Sets the academic registration string.
   *
   * @param academicRegistration the new registration, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public StudentUpdateCommandBuilder withAcademicRegistration(String academicRegistration) {
    this.academicRegistration = academicRegistration;
    return this;
  }

  /**
   * Sets the campus assignment.
   *
   * @param campus the new {@link Campi}, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public StudentUpdateCommandBuilder withCampus(Campi campus) {
    this.campus = campus;
    return this;
  }

  /**
   * Sets the enrolled course identifier.
   *
   * @param courseId the new course UUID, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public StudentUpdateCommandBuilder withCourseId(UUID courseId) {
    this.courseId = courseId;
    return this;
  }

  /**
   * Sets the required counterpart hours.
   *
   * @param requiredHours the new required hours, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public StudentUpdateCommandBuilder withRequiredHours(BigDecimal requiredHours) {
    this.requiredHours = requiredHours;
    return this;
  }

  /**
   * Sets the enrollment start date.
   *
   * @param startDate the new start date, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public StudentUpdateCommandBuilder withStartDate(LocalDate startDate) {
    this.startDate = startDate;
    return this;
  }

  /**
   * Sets the enrollment due date.
   *
   * @param dueDate the new due date, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public StudentUpdateCommandBuilder withDueDate(LocalDate dueDate) {
    this.dueDate = dueDate;
    return this;
  }

  /**
   * Constructs the {@link StudentUpdateCommand} using the current builder state, composing the
   * nested {@link AccountUpdateCommand} and {@link UserUpdateCommand} internally.
   *
   * @return a configured {@link StudentUpdateCommand} instance
   */
  public StudentUpdateCommand build() {
    UserUpdateCommand userCmd = (name != null) ? new UserUpdateCommand(name) : null;
    AccountUpdateCommand accCmd =
        (email != null || password != null || userCmd != null)
            ? new AccountUpdateCommand(email, password, userCmd)
            : null;
    return new StudentUpdateCommand(
        accCmd, academicRegistration, campus, courseId, requiredHours, startDate, dueDate);
  }

  private static Campi getRandomCampus() {
    Campi[] values = Campi.values();
    return values[new Random().nextInt(values.length)];
  }
}
