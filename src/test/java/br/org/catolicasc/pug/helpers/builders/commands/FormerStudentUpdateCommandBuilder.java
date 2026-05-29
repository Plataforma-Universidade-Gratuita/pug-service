package br.org.catolicasc.pug.helpers.builders.commands;

import br.org.catolicasc.pug.academic.service.dtos.FormerStudentUpdateCommand;
import br.org.catolicasc.pug.helpers.TestNameGenerator;
import br.org.catolicasc.pug.identity.service.dtos.AccountUpdateCommand;
import br.org.catolicasc.pug.identity.service.dtos.UserUpdateCommand;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;

/**
 * Builder class for creating {@link FormerStudentUpdateCommand} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with random defaults. Since update commands treat {@code null} fields as
 * "no change", the default populates all fields to simulate a full update. Use {@code
 * withXxx(null)} to explicitly test partial update behavior.
 */
public class FormerStudentUpdateCommandBuilder {
  private String name = TestNameGenerator.generateRandomName();
  private String email = TestNameGenerator.generateUniqueEmail("test.com");
  private String password = null;
  private String academicRegistration = null;
  private Campi campus = getRandomCampus();
  private UUID courseId = null;
  private BigDecimal requiredHours = null;
  private LocalDate startDate = null;
  private LocalDate dueDate = null;

  private FormerStudentUpdateCommandBuilder() {}

  /**
   * Initializes a new builder with sensible defaults for a partial update.
   *
   * @return a new {@link FormerStudentUpdateCommandBuilder} instance
   */
  public static FormerStudentUpdateCommandBuilder aFormerStudentUpdateCommand() {
    return new FormerStudentUpdateCommandBuilder();
  }

  /**
   * Sets the name for the nested user update command.
   *
   * @param name the new name, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public FormerStudentUpdateCommandBuilder withName(String name) {
    this.name = name;
    return this;
  }

  /**
   * Sets the email for the nested account update command.
   *
   * @param email the new email, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public FormerStudentUpdateCommandBuilder withEmail(String email) {
    this.email = email;
    return this;
  }

  /**
   * Sets the password for the nested account update command.
   *
   * @param password the new password, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public FormerStudentUpdateCommandBuilder withPassword(String password) {
    this.password = password;
    return this;
  }

  /**
   * Sets the academic registration string.
   *
   * @param academicRegistration the new registration, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public FormerStudentUpdateCommandBuilder withAcademicRegistration(String academicRegistration) {
    this.academicRegistration = academicRegistration;
    return this;
  }

  /**
   * Sets the campus assignment.
   *
   * @param campus the new {@link Campi}, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public FormerStudentUpdateCommandBuilder withCampus(Campi campus) {
    this.campus = campus;
    return this;
  }

  /**
   * Sets the enrolled course identifier.
   *
   * @param courseId the new course UUID, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public FormerStudentUpdateCommandBuilder withCourseId(UUID courseId) {
    this.courseId = courseId;
    return this;
  }

  /**
   * Sets the required counterpart hours.
   *
   * @param requiredHours the new required hours, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public FormerStudentUpdateCommandBuilder withRequiredHours(BigDecimal requiredHours) {
    this.requiredHours = requiredHours;
    return this;
  }

  /**
   * Sets the enrollment start date.
   *
   * @param startDate the new start date, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public FormerStudentUpdateCommandBuilder withStartDate(LocalDate startDate) {
    this.startDate = startDate;
    return this;
  }

  /**
   * Sets the enrollment due date.
   *
   * @param dueDate the new due date, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public FormerStudentUpdateCommandBuilder withDueDate(LocalDate dueDate) {
    this.dueDate = dueDate;
    return this;
  }

  /**
   * Constructs the {@link FormerStudentUpdateCommand} using the current builder state, composing the
   * nested {@link AccountUpdateCommand} and {@link UserUpdateCommand} internally.
   *
   * @return a configured {@link FormerStudentUpdateCommand} instance
   */
  public FormerStudentUpdateCommand build() {
    UserUpdateCommand userCmd = (name != null) ? new UserUpdateCommand(name) : null;
    AccountUpdateCommand accCmd =
        (email != null || password != null || userCmd != null)
            ? new AccountUpdateCommand(email, password, null, userCmd)
            : null;
    return new FormerStudentUpdateCommand(
        accCmd, academicRegistration, campus, courseId, requiredHours, startDate, dueDate);
  }

  private static Campi getRandomCampus() {
    Campi[] values = Campi.values();
    return values[new Random().nextInt(values.length)];
  }
}

