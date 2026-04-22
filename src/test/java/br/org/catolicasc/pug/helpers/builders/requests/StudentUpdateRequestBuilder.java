package br.org.catolicasc.pug.helpers.builders.requests;

import br.org.catolicasc.pug.academic.presenter.dtos.StudentUpdateRequest;
import br.org.catolicasc.pug.helpers.TestNameGenerator;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;

/**
 * Builder class for creating {@link StudentUpdateRequest} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with sensible defaults for partial updates. Fields default to {@code
 * null} (no change) except for commonly tested fields like name and campus.
 */
public class StudentUpdateRequestBuilder {
  private String name = TestNameGenerator.generateRandomName();
  private String cpf = null;
  private String email = null;
  private String password = null;
  private String academicRegistration = null;
  private Campi campus = getRandomCampus();
  private UUID courseId = null;
  private BigDecimal requiredHours = null;
  private LocalDate startDate = null;
  private LocalDate dueDate = null;

  private StudentUpdateRequestBuilder() {}

  /**
   * Initializes a new builder with sensible defaults for a partial update.
   *
   * @return a new {@link StudentUpdateRequestBuilder} instance
   */
  public static StudentUpdateRequestBuilder aStudentUpdateRequest() {
    return new StudentUpdateRequestBuilder();
  }

  /**
   * Sets the student name.
   *
   * @param name the new name, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public StudentUpdateRequestBuilder withName(String name) {
    this.name = name;
    return this;
  }

  /**
   * Sets the CPF.
   *
   * @param cpf the new CPF, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public StudentUpdateRequestBuilder withCpf(String cpf) {
    this.cpf = cpf;
    return this;
  }

  /**
   * Sets the email address.
   *
   * @param email the new email, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public StudentUpdateRequestBuilder withEmail(String email) {
    this.email = email;
    return this;
  }

  /**
   * Sets the password.
   *
   * @param password the new password, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public StudentUpdateRequestBuilder withPassword(String password) {
    this.password = password;
    return this;
  }

  /**
   * Sets the academic registration.
   *
   * @param academicRegistration the new registration, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public StudentUpdateRequestBuilder withAcademicRegistration(String academicRegistration) {
    this.academicRegistration = academicRegistration;
    return this;
  }

  /**
   * Sets the campus assignment.
   *
   * @param campus the new {@link Campi}, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public StudentUpdateRequestBuilder withCampus(Campi campus) {
    this.campus = campus;
    return this;
  }

  /**
   * Sets the enrolled course identifier.
   *
   * @param courseId the new course UUID, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public StudentUpdateRequestBuilder withCourseId(UUID courseId) {
    this.courseId = courseId;
    return this;
  }

  /**
   * Sets the required counterpart hours.
   *
   * @param requiredHours the new required hours, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public StudentUpdateRequestBuilder withRequiredHours(BigDecimal requiredHours) {
    this.requiredHours = requiredHours;
    return this;
  }

  /**
   * Sets the enrollment start date.
   *
   * @param startDate the new start date, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public StudentUpdateRequestBuilder withStartDate(LocalDate startDate) {
    this.startDate = startDate;
    return this;
  }

  /**
   * Sets the enrollment due date.
   *
   * @param dueDate the new due date, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public StudentUpdateRequestBuilder withDueDate(LocalDate dueDate) {
    this.dueDate = dueDate;
    return this;
  }

  /**
   * Constructs the {@link StudentUpdateRequest} using the current builder state.
   *
   * @return a configured {@link StudentUpdateRequest} instance
   */
  public StudentUpdateRequest build() {
    return new StudentUpdateRequest(
        name,
        cpf,
        email,
        password,
        academicRegistration,
        campus,
        courseId,
        requiredHours,
        startDate,
        dueDate);
  }

  private static Campi getRandomCampus() {
    Campi[] values = Campi.values();
    return values[new Random().nextInt(values.length)];
  }
}
